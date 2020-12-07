package com.explosion204.battleship.data.models

import com.explosion204.battleship.Constants.Companion.OUTCOME_FIRST_PLAYER_WON


data class GameResult(
    val firstPlayerId: String = "",
    val secondPlayerId: String = "",
    var opponentNickname: String = "",
    val outcome: String = OUTCOME_FIRST_PLAYER_WON
)