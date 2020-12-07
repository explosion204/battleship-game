package com.explosion204.battleship.viewmodels

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.explosion204.battleship.*
import com.explosion204.battleship.Constants.Companion.ERROR
import com.explosion204.battleship.Constants.Companion.FIRE_REQUEST_PASS
import com.explosion204.battleship.Constants.Companion.FIRE_RESPONSE_MISS
import com.explosion204.battleship.Constants.Companion.FIRE_RESPONSE_PASS
import com.explosion204.battleship.Constants.Companion.GAME_STATE_IN_LOBBY
import com.explosion204.battleship.Constants.Companion.GAME_STATE_IN_PROGRESS
import com.explosion204.battleship.Constants.Companion.GAME_STATE_LOADING
import com.explosion204.battleship.Constants.Companion.GAME_STATE_PAUSED
import com.explosion204.battleship.Constants.Companion.GUEST_DISCONNECTED
import com.explosion204.battleship.Constants.Companion.HOST_DISCONNECTED
import com.explosion204.battleship.data.models.Session
import com.explosion204.battleship.data.repos.SessionRepository
import com.explosion204.battleship.data.repos.UserRepository
import com.explosion204.battleship.ui.util.CircleTransform
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import java.lang.Exception
import javax.inject.Inject

// User interacts with UI -> view model handles user actions and updates DB ->
// view model observes DB changes and sends them to GameController to be processed ->
// GameController notifies view model about its internal changes -> view model updates ui
class GameViewModel @Inject constructor(
    private val sessionRepository: SessionRepository,
    private val userRepository: UserRepository
) : ViewModel() {
    private var gameController: GameController? = null

    var gameState = GAME_STATE_LOADING
    var sessionId = MutableLiveData<Long?>(null)
    var hostId = MutableLiveData("")
    var guestId = MutableLiveData("")
    var playerMatrix = MutableLiveData(Matrix(10, 10))
    var opponentMatrix = MutableLiveData(Matrix(10, 10))
    var hostReady = MutableLiveData(false)
    var guestReady = MutableLiveData(false)
    var gameRunning = MutableLiveData(false)
    var hostTurn = MutableLiveData(true)
    var hostBitmap = MutableLiveData<Bitmap>()
    var guestBitmap = MutableLiveData<Bitmap>()

    private var picassoTargetHost: Target? = null
    private var picassoTargetGuest: Target? = null

    private var hostProfileImageLoaded = false
    private var guestProfileImageLoaded = false
    private var valueListener: ValueEventListener? = null

    private fun setListeners() {
        gameController?.setOnGameEventsListener(object : GameController.OnGameEventsListener {
            override fun onHostReadyChanged(newValue: Boolean) {
                hostReady.postValue(newValue)
            }

            override fun onGuestReadyChanged(newValue: Boolean) {
                guestReady.postValue(newValue)
            }

            override fun onGameRunningChanged(newValue: Boolean) {
                gameRunning.postValue(newValue)
                gameState = if (newValue) {
                    GAME_STATE_IN_PROGRESS
                } else {
                    GAME_STATE_PAUSED
                }
            }

            override fun onHostTurnChanged(newValue: Boolean) {
                hostTurn.postValue(newValue)
            }

            override fun onPlayerMatrixChanged(newValue: Matrix) {
                playerMatrix.postValue(newValue)
            }

            override fun onOpponentMatrixChanged(newValue: Matrix) {
                opponentMatrix.postValue(newValue)
            }

            override fun onFireRequestProcessed(i: Int, j: Int, response: String) {
                if (sessionId.value != null) {
                    sessionRepository.updateSessionValue(
                        sessionId.value!!,
                        "fireResponse",
                        "$i-$j-$response",
                        null
                    )
                }
            }

            override fun onFireResponseProcessed(
                i: Int,
                j: Int,
                response: String,
                hostTurn: Boolean
            ) {
                when (response) {
                    FIRE_RESPONSE_MISS -> {
                        sessionRepository.updateSessionValue(
                            sessionId.value!!,
                            "fireRequest",
                            FIRE_REQUEST_PASS,
                            null
                        )
                        sessionRepository.updateSessionValue(
                            sessionId.value!!,
                            "fireResponse",
                            FIRE_RESPONSE_PASS,
                            null
                        )
                        sessionRepository.updateSessionValue(
                            sessionId.value!!,
                            "hostTurn",
                            !hostTurn,
                            null
                        )
                    }
                }
            }
        })
    }

    // TODO: Delete session after the game finished]
    // TODO: Second guest cannot connect to lobby (implemented, not tested)
    // TODO: Cannot connect to lobby with the same uid as host (implemented, not tested)
    // TODO: Auth service
    // Initialize new session if user is host (!!!onComplete callback executed only in GAME_STATE_LOADING!!!)
    fun initNewSession(userId: String, onComplete: () -> Unit) {
        gameController = GameController(isHost = true)
        setListeners()
        sessionRepository.initNewSession(userId) {
            initLiveData(it, true) {
                if (hostProfileImageLoaded) {
                    if (gameState == GAME_STATE_LOADING) {
                        onComplete()
                        gameState = GAME_STATE_IN_LOBBY
                    }
                }
            }
        }
    }

    // Fetch data from existing session if user is guest
    fun fetchSession(
        sessionId: Long,
        userId: String,
        onComplete: () -> Unit,
        onFailure: () -> Unit
    ) {
        gameController = GameController(isHost = false)
        setListeners()
        sessionRepository.findSession(sessionId,
            { ref ->
                sessionRepository.updateSessionValue(sessionId, "guestId", userId) {
                    initLiveData(ref, false) {
                        if (guestProfileImageLoaded && hostProfileImageLoaded) {
                            onComplete()
                            gameState = GAME_STATE_IN_LOBBY
                        }
                    }
                }
            },
            {
                onFailure()
            })
    }

    // Subscribe lifecycle-aware fields of view model to database changes
    private fun initLiveData(ref: DatabaseReference, isHost: Boolean, onComplete: () -> Unit) {
        valueListener = ref.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                Log.e(ERROR, error.toString())
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                val session = snapshot.getValue(Session::class.java)

                if (session != null) {
                    if (sessionId.value != session.id) {
                        sessionId.postValue(session.id)
                    }

                    if (this@GameViewModel.hostId.value != session.hostId) {
                        this@GameViewModel.hostId.postValue(session.hostId)

                        if (session.hostId == HOST_DISCONNECTED && sessionId.value != null) {
                            sessionRepository.deleteSession(sessionId.value!!)
                        }

                        if (!hostProfileImageLoaded) {
                            fetchHostProfileImageBitmap(session.hostId) {
                                onComplete()
                            }
                        }
                    }

                    if (guestId.value != session.guestId) {
                        guestId.postValue(session.guestId)

                        if (session.guestId != null && !guestProfileImageLoaded) {
                            fetchGuestProfileImageBitmap(session.guestId) {
                                onComplete()
                            }
                        }

                        if (session.guestId != GUEST_DISCONNECTED) {
                            guestProfileImageLoaded = false
                        }
                    }

                    if (gameController?.hostReady != session.hostReady) {
                        gameController?.hostReady = session.hostReady
                    }

                    if (gameController?.guestReady != session.guestReady) {
                        gameController?.guestReady = session.guestReady
                    }

                    if (gameController?.gameRunning != session.gameRunning) {
                        gameController?.gameRunning = session.gameRunning
                    }

                    gameController?.setHostTurn(session.hostTurn)

                    if (session.fireRequest != FIRE_REQUEST_PASS) {
                        val requestTokens = session.fireRequest.split('-')
                        gameController?.processFireRequest(
                            requestTokens[0].toInt(),
                            requestTokens[1].toInt()
                        )
                    }

                    if (session.fireResponse != FIRE_RESPONSE_PASS) {
                        val responseTokens = session.fireResponse.split('-')
                        gameController?.processFireResponse(
                            responseTokens[0].toInt(),
                            responseTokens[1].toInt(),
                            responseTokens[2]
                        )
                    }
                }
            }

        })

        if (isHost) {
            sessionRepository.setValueOnDisconnect(ref, "hostId", HOST_DISCONNECTED)
        } else {
            sessionRepository.setValueOnDisconnect(ref, "guestId", GUEST_DISCONNECTED)
        }
    }

    private fun fetchHostProfileImageBitmap(hostId: String, onComplete: () -> Unit) {
        userRepository.getProfileImageUri(hostId) { profileImageUri ->
            picassoTargetHost = object : Target {
                override fun onPrepareLoad(placeHolderDrawable: Drawable?) {}

                override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {}

                override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                    hostBitmap.postValue(bitmap)
                    hostProfileImageLoaded = true
                    onComplete()
                }
            }
            Picasso.get().load(profileImageUri).transform(CircleTransform())
                .into(picassoTargetHost!!)
        }
    }

    private fun fetchGuestProfileImageBitmap(guestId: String, onComplete: () -> Unit) {
        userRepository.getProfileImageUri(guestId) { profileImageUri ->
            picassoTargetGuest = object : Target {
                override fun onPrepareLoad(placeHolderDrawable: Drawable?) {}

                override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {}

                override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                    guestBitmap.postValue(bitmap)
                    guestProfileImageLoaded = true
                    onComplete()
                }
            }
            Picasso.get().load(profileImageUri).transform(CircleTransform())
                .into(picassoTargetGuest!!)
        }
    }

    fun generateMatrix() {
        if (gameController != null) {
            gameController!!.generateMatrix()
        }
    }

    fun changeReady() {
        if (gameController != null) {
            if (sessionId.value != null) {
                sessionRepository.updateSessionValue(
                    sessionId.value!!,
                    if (gameController!!.isHost) "hostReady" else "guestReady",
                    if (gameController!!.isHost) !hostReady.value!! else !guestReady.value!!,
                    null
                )
            }
        }
    }

    fun setGameRunning(status: Boolean) {
        if (sessionId.value != null) {
            sessionRepository.updateSessionValue(sessionId.value!!, "gameRunning", status, null)
        }
    }

    fun postFireRequest(i: Int, j: Int) {
        if (sessionId.value != null) {
            sessionRepository.updateSessionValue(sessionId.value!!, "fireRequest", "$i-$j", null)
        }
    }

    private fun leaveLobby() {
        if (gameController != null && sessionId.value != null) {
            if (gameController!!.isHost) {
                sessionRepository.updateSessionValue(sessionId.value!!, "hostId", HOST_DISCONNECTED, null)
                sessionRepository.detachValueEventListener(sessionId.value!!, valueListener!!)

                if (guestId.value!! == GUEST_DISCONNECTED) {
                    sessionRepository.deleteSession(sessionId.value!!)
                }

            } else if (sessionId.value != null) {
                sessionRepository.updateSessionValue(sessionId.value!!, "guestId", GUEST_DISCONNECTED, null)
                sessionRepository.detachValueEventListener(sessionId.value!!, valueListener!!)
                sessionRepository.updateSessionValue(sessionId.value!!, "guestReady", false, null)

                if (hostId.value!! == HOST_DISCONNECTED) {
                    sessionRepository.deleteSession(sessionId.value!!)
                }
            }

            gameController = null
        }
    }

    override fun onCleared() {
        leaveLobby()
        super.onCleared()
    }
}