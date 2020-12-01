package com.explosion204.battleship.viewmodels

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.explosion204.battleship.Constants.Companion.ERROR
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception
import javax.inject.Inject

class GameViewModel @Inject constructor(private val sessionRepository: SessionRepository, private val userRepository: UserRepository) : ViewModel() {
    var sessionId = MutableLiveData<Long?>(null)
    var hostId = MutableLiveData("")
    var guestId = MutableLiveData("")
    var hostReady = MutableLiveData(false)
    var guestReady = MutableLiveData(false)
    var gameRunning = MutableLiveData(false)
    var hostTurn = MutableLiveData(false)
    var fireRequest = MutableLiveData("")
    var fireResponse = MutableLiveData("")
    var hostShips = MutableLiveData(0)
    var guestShips = MutableLiveData(0)

    var hostBitmap = MutableLiveData<Bitmap>()
    var guestBitmap = MutableLiveData<Bitmap>()

    var lockRequestUpdates = false
    var lockResponseUpdates = false

    private var isHost = true

    private var picassoTargetHost: Target? = null
    private var picassoTargetGuest: Target? = null

    private var hostProfileImageLoaded = false
    private var guestProfileImageLoaded = false


    // TODO: Delete session after the game finished
    // TODO: Delete session after host leaved
    // TODO: Implement guest disconnect
    // TODO: Second guest cannot connect to full lobby
    // Initialize new session if user is host
    fun initNewSession(userId: String, onComplete: () -> Unit) {
        sessionRepository.initNewSession(userId) {
            initMutableData(it) {
                if (hostProfileImageLoaded) {
                    onComplete()
                }
            }
        }
    }

    // Fetch data from existing session if user is guest
    fun fetchSession(sessionId: Long, userId: String, onComplete: () -> Unit) {
        sessionRepository.findSession(sessionId,
        { ref ->
            isHost = false
            ref.child("guestId").setValue(userId).addOnSuccessListener {
                initMutableData(ref) {
                    if (guestProfileImageLoaded && hostProfileImageLoaded) {
                        onComplete()
                    }
                }
            }
        },
        {
            //TODO: Implement failure callback
        })
    }

    // Subscribe lifecycle-aware fields of View Model to database changes
    private fun initMutableData(ref: DatabaseReference, onComplete: () -> Unit) {
        ref.addValueEventListener(object : ValueEventListener {
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

                        fetchHostProfileImageBitmap(session.hostId) {
                            onComplete()
                        }
                    }

                    if (guestId.value != session.guestId) {
                        guestId.postValue(session.guestId)

                        if (session.guestId != null) {
                            fetchGuestProfileImageBitmap(session.guestId) {
                                onComplete()
                            }
                        }
                    }

                    if (hostReady.value != session.hostReady) {
                        hostReady.postValue(session.hostReady)
                    }

                    if (guestReady.value != session.guestReady) {
                        guestReady.postValue(session.guestReady)
                    }

                    if (gameRunning.value != session.gameRunning) {
                        gameRunning.postValue(session.gameRunning)
                    }

                    if (hostTurn.value != session.hostTurn) {
                        hostTurn.postValue(session.hostTurn)
                    }

                    if (fireRequest.value != session.fireRequest && !lockRequestUpdates) {
                        fireRequest.postValue(session.fireRequest)
                    }

                    if (fireResponse.value != session.fireResponse && !lockResponseUpdates) {
                        fireResponse.postValue(session.fireResponse)
                    }

                    if (hostShips.value != session.hostShips) {
                        hostShips.postValue(session.hostShips)
                    }

                    if (guestShips.value != session.guestShips) {
                        guestShips.postValue(session.guestShips)
                    }
                }
            }

        })
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
            Picasso.get().load(profileImageUri).transform(CircleTransform()).into(picassoTargetHost!!)
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
            Picasso.get().load(profileImageUri).transform(CircleTransform()).into(picassoTargetGuest!!)
        }
    }

    fun changeReady() {
        if (sessionId.value != null) {
            sessionRepository.updateSessionValue(sessionId.value!!,
                if (isHost) "hostReady" else "guestReady",
                if (isHost) !hostReady.value!! else !guestReady.value!!)
        }
    }

    fun setGameRunning(status: Boolean) {
        if (sessionId.value != null && isHost && hostReady.value!! && guestReady.value!!) {
            sessionRepository.updateSessionValue(sessionId.value!!, "gameRunning", status)
        }
    }

    fun sendFireRequest(i: Int, j: Int) {
        if (sessionId.value != null) {
            sessionRepository.updateSessionValue(sessionId.value!!, "fireRequest", "$i-$j")
        }
    }

    fun finishTurn() {
        if (sessionId.value != null) {
            sessionRepository.updateSessionValue(sessionId.value!!, "gameRunning", !hostTurn.value!!)
        }
    }

    fun findSession(sessionId: Long, onSuccess: () -> Unit, onFailure: () -> Unit) {
        sessionRepository.findSession(sessionId, { onSuccess() }, onFailure)
    }
}