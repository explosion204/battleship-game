package com.explosion204.battleship.ui.activities

import android.os.Bundle
import android.support.wearable.activity.WearableActivity

class BattleshipActivity : WearableActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_battleship)

        // Enables Always-on
        setAmbientEnabled()
    }
}