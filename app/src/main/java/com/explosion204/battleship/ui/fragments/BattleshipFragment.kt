package com.explosion204.battleship.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.explosion204.battleship.Matrix
import com.explosion204.battleship.R
import com.explosion204.battleship.ui.adapters.MatrixAdapter
import dagger.android.support.DaggerFragment

class BattleshipFragment : DaggerFragment() {
    private lateinit var playerMatrixView: RecyclerView
    private lateinit var opponentMatrixView: RecyclerView
    private val playerMatrix = Matrix(10, 10)
    private val opponentMatrix = Matrix(10, 10)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_battleship, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        playerMatrixView = view.findViewById(R.id.player_matrix)
        opponentMatrixView = view.findViewById(R.id.opponent_matrix)

        playerMatrixView.layoutManager = GridLayoutManager(requireContext(), playerMatrix.rowCapacity())
        playerMatrixView.adapter = MatrixAdapter(requireContext(), playerMatrix)

        opponentMatrixView.layoutManager = GridLayoutManager(requireContext(), opponentMatrix.rowCapacity())
        opponentMatrixView.adapter = MatrixAdapter(requireContext(), opponentMatrix)

        setLayoutParams()
    }

    private fun setLayoutParams() {
        val metrics = resources.displayMetrics

        val playerMatrixLayout = requireView().findViewById<LinearLayout>(R.id.player_matrix_layout)
        val opponentMatrixLayout = requireView().findViewById<LinearLayout>(R.id.opponent_matrix_layout)

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