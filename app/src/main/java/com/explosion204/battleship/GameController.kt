package com.explosion204.battleship

import androidx.lifecycle.MutableLiveData

class GameController {
    var gameState = Constants.GAME_STATE_LOADING

    val matrix = MutableLiveData(Matrix(10, 10))
    val opponentMatrix = MutableLiveData(Matrix(10, 10))

    fun processFireRequest(fireRequest: String): String {
        return ""
    }
}