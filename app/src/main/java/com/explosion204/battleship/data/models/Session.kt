package com.explosion204.battleship.data.models

import com.explosion204.battleship.Constants.Companion.FIRE_REQUEST_PASS
import com.explosion204.battleship.Constants.Companion.FIRE_RESPONSE_PASS

data class Session(
    var id: Long? = null,
    val hostId: String = "",
    val guestId: String? = null,
    var hostReady: Boolean = false,
    var guestReady: Boolean = false,
    var gameRunning: Boolean = false,
    var hostTurn: Boolean = false,
    var fireRequest: String = FIRE_REQUEST_PASS,
    var fireResponse: String = "0-0-$FIRE_RESPONSE_PASS",
    var hostShips: Int = 10,
    var guestShips: Int = 10
)