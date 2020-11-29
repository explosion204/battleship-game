package com.explosion204.battleship.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.LinearLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.explosion204.battleship.Matrix
import com.explosion204.battleship.R
import com.explosion204.battleship.ui.adapters.MatrixAdapter

class LobbyActivity : AppCompatActivity() {
    private lateinit var matrixView: RecyclerView
    private val matrix = Matrix(10, 10)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lobby)

        matrixView = findViewById(R.id.matrix)

        matrixView.layoutManager = GridLayoutManager(this, matrix.rowCapacity())
        matrixView.adapter = MatrixAdapter(this, matrix)

        setLayoutParams()
    }

    private fun setLayoutParams() {
        val metrics = resources.displayMetrics

        val matrixLayout = findViewById<LinearLayout>(R.id.matrix_layout)
        val playersLayout = findViewById<LinearLayout>(R.id.players_layout)

        matrixLayout.layoutParams.width = metrics.widthPixels / 2
        playersLayout.layoutParams.width = metrics.widthPixels / 2
        matrixLayout.layoutParams.height = metrics.heightPixels - convertToPixels(50)
        playersLayout.layoutParams.height = metrics.heightPixels - convertToPixels(50)

        //matrixView.layoutParams.height = metrics.heightPixels - convertToPixels(50)
        matrixView.layoutParams.height = matrixLayout.layoutParams.height - convertToPixels(60)
    }

    private fun convertToPixels(dp: Int): Int {
        return dp * resources.displayMetrics.densityDpi / 160
    }
}