package com.explosion204.battleship

interface OnGameEventsListener {
    fun onHostReadyChanged(newValue: Boolean)
    fun onGuestReadyChanged(newValue: Boolean)
    fun onGameRunningChanged(newValue: Boolean)
    fun onHostTurnChanged(newValue: Boolean)
    fun onPlayerMatrixChanged(newValue: Matrix)
    fun onOpponentMatrixChanged(newValue: Matrix)
    fun onFireRequestProcessed(i: Int, j: Int, response: String)
    fun onFireResponseProcessed(i: Int, j: Int, response: String, hostTurn: Boolean)
}