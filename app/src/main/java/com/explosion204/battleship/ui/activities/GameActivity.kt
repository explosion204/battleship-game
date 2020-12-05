package com.explosion204.battleship.ui.activities

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import com.explosion204.battleship.Constants.Companion.GAME_STATE_IN_LOBBY
import com.explosion204.battleship.R
import com.explosion204.battleship.viewmodels.GameViewModel
import com.explosion204.battleship.viewmodels.ViewModelFactory
import dagger.android.support.DaggerAppCompatActivity
import javax.inject.Inject

class GameActivity : DaggerAppCompatActivity() {
    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val gameViewModel: GameViewModel by viewModels {
        viewModelFactory
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)
    }

    override fun onBackPressed() {
        when (gameViewModel.gameState) {
            GAME_STATE_IN_LOBBY -> {
                AlertDialog.Builder(this)
                    .setMessage(getString(R.string.leave_lobby))
                    .setPositiveButton(R.string.leave) { _, _ ->
                        super.onBackPressed()
                    }
                    .setNegativeButton(R.string.cancel) { _, _ -> }
                    .setCancelable(false)
                    .show()
            }
        }
    }
}