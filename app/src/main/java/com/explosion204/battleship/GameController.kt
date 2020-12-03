package com.explosion204.battleship

import androidx.lifecycle.MutableLiveData
import com.explosion204.battleship.Constants.Companion.FIRE_REQUEST_PASS
import com.explosion204.battleship.Constants.Companion.FIRE_RESPONSE_HIT
import com.explosion204.battleship.Constants.Companion.FIRE_RESPONSE_MISS
import com.explosion204.battleship.Constants.Companion.FIRE_RESPONSE_PASS
import com.explosion204.battleship.Constants.Companion.MATRIX_FREE_CELL
import com.explosion204.battleship.Constants.Companion.MATRIX_HIT_CELL
import com.explosion204.battleship.Constants.Companion.MATRIX_MISSED_CELL
import com.explosion204.battleship.Constants.Companion.MATRIX_TAKEN_CELL

class GameController {
    var gameState = Constants.GAME_STATE_LOADING

    private var innerMatrix = Matrix(10, 10)
    private var innerOpponentMatrix = Matrix(10, 10)

    val matrix = MutableLiveData(innerMatrix)
    val opponentMatrix = MutableLiveData(innerOpponentMatrix)


    fun processFireRequest(fireRequest: String): String { // i-j
        if (fireRequest != FIRE_REQUEST_PASS) {
            val tokens = fireRequest.split('-')
            val i = tokens[0].toInt()
            val j = tokens[1].toInt()

            return when (matrix.value!![i, j]) {
                MATRIX_FREE_CELL -> {
                    innerMatrix[i, j] = MATRIX_MISSED_CELL
                    matrix.postValue(innerMatrix)
                    "$i-$j-$FIRE_RESPONSE_MISS"
                }
                MATRIX_TAKEN_CELL -> {
                    innerMatrix[i, j] = MATRIX_HIT_CELL
                    matrix.postValue(innerMatrix)
                    "$i-$j-$FIRE_RESPONSE_HIT"
                }
                else -> "0-0-$FIRE_RESPONSE_PASS"
            }
        } else {
            return "0-0-$FIRE_RESPONSE_PASS"
        }
    }

    fun processFireResponse(fireResponse: String, onHit: () -> Unit, onMiss: () -> Unit) {
        val tokens = fireResponse.split('-')
        val i = tokens[0].toInt()
        val j = tokens[1].toInt()
        when (tokens[2]) {
            FIRE_RESPONSE_HIT -> {
                innerOpponentMatrix[i, j] = MATRIX_HIT_CELL
                opponentMatrix.postValue(innerOpponentMatrix)
                onHit()
            }
            FIRE_RESPONSE_MISS -> {
                innerOpponentMatrix[i, j] = MATRIX_MISSED_CELL
                opponentMatrix.postValue(innerOpponentMatrix)
                onMiss()
            }
            else -> {
            }
        }
    }

    fun generateMatrix() {
        innerMatrix = MatrixGenerator.generate(10, 10)
        matrix.postValue(innerMatrix)
    }
}