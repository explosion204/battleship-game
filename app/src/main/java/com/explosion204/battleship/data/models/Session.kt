package com.explosion204.battleship.data.models

import com.explosion204.battleship.Constants.Companion.FIRE_REQUEST_PASS
import com.explosion204.battleship.Constants.Companion.FIRE_RESPONSE_PASS
import com.explosion204.battleship.Constants.Companion.GUEST_DISCONNECTED

data class Session(
    var id: Long? = null,
    val hostId: String = "",
    val guestId: String? = GUEST_DISCONNECTED,
    var hostReady: Boolean = false,
    var guestReady: Boolean = false,
    var gameRunning: Boolean = false,
    var hostTurn: Boolean = false,
    var fireRequest: String = FIRE_REQUEST_PASS,
    var fireResponse: String = FIRE_RESPONSE_PASS,
    var hostDefeated: Boolean = false,
    var guestDefeated: Boolean = false
)