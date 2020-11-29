package com.explosion204.battleship.data.models

data class Session(
    var id: String? = null,
    val hostId: String,
    val guestId: String? = null,
    var hostReady: Boolean,
    var guestReady: Boolean,
    var gameRunning: Boolean,
    var hostTurn: Boolean,
    var fireRequest: String,
    var fireResponse: String,
    var hostShips: Int,
    var guestShips: Int
)