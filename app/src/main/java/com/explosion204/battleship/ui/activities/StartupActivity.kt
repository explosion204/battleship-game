package com.explosion204.battleship.ui.activities

import android.os.Bundle
import com.explosion204.battleship.R
import dagger.android.support.DaggerAppCompatActivity

class StartupActivity : DaggerAppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_startup)
    }
}