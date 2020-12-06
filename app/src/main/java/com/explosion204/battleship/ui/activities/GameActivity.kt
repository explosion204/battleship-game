package com.explosion204.battleship.ui.activities

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import com.explosion204.battleship.Constants.Companion.GAME_STATE_IN_LOBBY
import com.explosion204.battleship.Constants.Companion.GAME_STATE_IN_PROGRESS
import com.explosion204.battleship.Constants.Companion.GAME_STATE_PAUSED
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

        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        connectivityManager?.let {
            it.registerDefaultNetworkCallback(object : ConnectivityManager.NetworkCallback() {
                override fun onLost(network: Network) {
                    AlertDialog.Builder(this@GameActivity)
                        .setMessage(getString(R.string.force_leave_message))
                        .setPositiveButton(getString(R.string.close)) { _, _ ->
                            it.unregisterNetworkCallback(this)
                            finish()
                        }

                        .setCancelable(false)
                        .show()

                    super.onLost(network)
                }
            })
        }
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
            GAME_STATE_IN_PROGRESS -> {
                gameViewModel.setGameRunning(false)
            }
        }
    }
}