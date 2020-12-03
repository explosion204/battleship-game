package com.explosion204.battleship

class Constants {
    companion object {
        const val ERROR = "ERROR"
        const val GOOGLE_SIGN_IN = 1
        const val PICK_IMAGE_CODE = 2
        const val USER_ID = "com.explosion204.battleship.USER_ID"
        const val IS_HOST_EXTRA = "com.explosion204.battleship.IS_HOST_EXTRA"
        const val SESSION_ID_EXTRA = "com.explosion204.battleship.SESSION_ID_EXTRA"

        const val SESSIONS_DB = "sessions"

        const val GAME_STATE_LOADING = "com.explosion204.battleship.GAME_STATE_LOADING"
        const val GAME_STATE_IN_LOBBY = "com.explosion204.battleship.GAME_STATE_IN_LOBBY"
        const val GAME_STATE_IN_PROGRESS = "com.explosion204.battleship.GAME_STATE_IN_PROGRESS"
        const val GAME_STATE_PAUSED = "com.explosion204.battleship.GAME_STATE_PAUSED"

        const val FIRE_REQUEST_PASS = "com.explosion204.battleship.FIRE_REQUEST_PASS"
        const val FIRE_RESPONSE_PASS = "com.explosion204.battleship.FIRE_RESPONSE_PASS"
        const val FIRE_RESPONSE_HIT = "com.explosion204.battleship.FIRE_RESPONSE_HIT"
        const val FIRE_RESPONSE_MISS = "com.explosion204.battleship.FIRE_RESPONSE_MISS"
        const val HOST_DISCONNECTED = "com.explosion204.battleship.HOST_DISCONNECTED"
        const val GUEST_DISCONNECTED = "com.explosion204.battleship.GUEST_DISCONNECTED"

        const val MATRIX_FREE_CELL: Byte = 0
        const val MATRIX_TAKEN_CELL: Byte = 1
        const val MATRIX_HIT_CELL: Byte = 2
        const val MATRIX_MISSED_CELL: Byte = 3
    }
}