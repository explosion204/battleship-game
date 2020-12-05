package com.explosion204.battleship

import androidx.lifecycle.MutableLiveData
import com.explosion204.battleship.Constants.Companion.FIRE_REQUEST_PASS
import com.explosion204.battleship.Constants.Companion.FIRE_RESPONSE_HIT
import com.explosion204.battleship.Constants.Companion.FIRE_RESPONSE_MISS
import com.explosion204.battleship.Constants.Companion.FIRE_RESPONSE_PASS
import com.explosion204.battleship.Constants.Companion.GAME_STATE_IN_PROGRESS
import com.explosion204.battleship.Constants.Companion.GAME_STATE_PAUSED
import com.explosion204.battleship.Constants.Companion.MATRIX_FREE_CELL
import com.explosion204.battleship.Constants.Companion.MATRIX_HIT_CELL
import com.explosion204.battleship.Constants.Companion.MATRIX_MISSED_CELL
import com.explosion204.battleship.Constants.Companion.MATRIX_TAKEN_CELL

class GameController(val isHost: Boolean) {
    private var onGameEventsListener: OnGameEventsListener? = null

    private var playerMatrix = Matrix(10, 10)
    private var opponentMatrix = Matrix(10, 10)

    var hostReady = false
        set(value) {
            field = value
            onGameEventsListener?.onHostReadyChanged(value)
        }

    var guestReady = false
        set(value) {
            field = value
            onGameEventsListener?.onGuestReadyChanged(value)
        }

    var gameRunning = false
        set(value) {
            if (hostReady && guestReady) {
                field = value
                onGameEventsListener?.onGameRunningChanged(value)
            }
        }
    private var hostTurn = true
    private var lockRequestProcessing = false
    private var lockResponseProcessing = true

    fun setOnGameEventsListener(listener: OnGameEventsListener) {
        onGameEventsListener = listener
    }

//    fun setHostReady(value: Boolean) {
//        hostReady = value
//        onGameEventsListener?.onHostReadyChanged(value)
//    }

//    fun setGuestReady(value: Boolean) {
//        guestReady = value
//        onGameEventsListener?.onGuestReadyChanged(value)
//    }

//    fun setGameRunning(value: Boolean) {
//        if (hostReady && guestReady) {
//            gameRunning = value
//            onGameEventsListener?.onGameRunningChanged(value)
//        }
//    }

    fun setHostTurn(value: Boolean) {
        if (hostTurn != value) {
            hostTurn = value

            if (isHost) {
                lockRequestProcessing = hostTurn
                lockResponseProcessing = !hostTurn
            } else {
                lockRequestProcessing = !hostTurn
                lockResponseProcessing = hostTurn
            }

            onGameEventsListener?.onHostTurnChanged(value)
        }
    }

    fun processFireRequest(i: Int, j: Int) {
        if (!lockRequestProcessing) {
            when (playerMatrix[i, j]) {
                MATRIX_FREE_CELL -> {
                    playerMatrix[i, j] = MATRIX_MISSED_CELL
                    onGameEventsListener?.onPlayerMatrixChanged(playerMatrix)
                    onGameEventsListener?.onFireRequestProcessed(i, j, FIRE_RESPONSE_MISS)
                }
                MATRIX_TAKEN_CELL -> {
                    playerMatrix[i, j] = MATRIX_HIT_CELL
                    onGameEventsListener?.onPlayerMatrixChanged(playerMatrix)
                    onGameEventsListener?.onFireRequestProcessed(i, j, FIRE_RESPONSE_HIT)
                }
            }
        }
    }

    fun processFireResponse(i: Int, j: Int, fireResponse: String) {
        if (!lockResponseProcessing) {
            when (fireResponse) {
                FIRE_RESPONSE_HIT -> {
                    opponentMatrix[i, j] = MATRIX_HIT_CELL
                    onGameEventsListener?.onOpponentMatrixChanged(opponentMatrix)
                    onGameEventsListener?.onFireResponseProcessed(i, j, FIRE_RESPONSE_HIT, hostTurn)
                }
                FIRE_RESPONSE_MISS -> {
                    opponentMatrix[i, j] = MATRIX_MISSED_CELL
                    onGameEventsListener?.onOpponentMatrixChanged(opponentMatrix)
                    onGameEventsListener?.onFireResponseProcessed(
                        i,
                        j,
                        FIRE_RESPONSE_MISS,
                        hostTurn
                    )
                }
            }
        }
    }

    fun generateMatrix() {
        playerMatrix = MatrixGenerator.generate(10, 10)
        onGameEventsListener?.onPlayerMatrixChanged(playerMatrix)
    }
}