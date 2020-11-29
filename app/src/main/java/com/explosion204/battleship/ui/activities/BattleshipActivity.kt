package com.explosion204.battleship.ui.activities

import android.os.Bundle
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.explosion204.battleship.Matrix
import com.explosion204.battleship.R
import com.explosion204.battleship.ui.adapters.MatrixAdapter

class BattleshipActivity : AppCompatActivity() {

    private lateinit var playerMatrixView: RecyclerView
    private lateinit var opponentMatrixView: RecyclerView
    private val playerMatrix = Matrix(10, 10)
    private val opponentMatrix = Matrix(10, 10)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_battleship)

        playerMatrixView = findViewById(R.id.player_matrix)
        opponentMatrixView = findViewById(R.id.opponent_matrix)

        playerMatrixView.layoutManager = GridLayoutManager(this, playerMatrix.rowCapacity())
        playerMatrixView.adapter = MatrixAdapter(this, playerMatrix)

        opponentMatrixView.layoutManager = GridLayoutManager(this, opponentMatrix.rowCapacity())
        opponentMatrixView.adapter = MatrixAdapter(this, opponentMatrix)

        setLayoutParams()
    }

    private fun setLayoutParams() {
        val metrics = resources.displayMetrics

        val playerMatrixLayout = findViewById<LinearLayout>(R.id.player_matrix_layout)
        val opponentMatrixLayout = findViewById<LinearLayout>(R.id.opponent_matrix_layout)

        playerMatrixLayout.layoutParams.width = metrics.widthPixels / 2
        opponentMatrixLayout.layoutParams.width = metrics.widthPixels / 2

        playerMatrixView.layoutParams.height = metrics.heightPixels - convertToPixels(70)
        playerMatrixView.layoutParams.width = metrics.widthPixels / 2 - convertToPixels(20)
        opponentMatrixView.layoutParams.height = metrics.heightPixels - convertToPixels(70)
        opponentMatrixView.layoutParams.width = metrics.widthPixels / 2 - convertToPixels(20)
    }

    private fun convertToPixels(dp: Int): Int {
        return dp * resources.displayMetrics.densityDpi / 160
    }
}