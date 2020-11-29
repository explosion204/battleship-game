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

class LobbyFragment : DaggerFragment() {
    private lateinit var matrixView: RecyclerView
    private val matrix = Matrix(10, 10)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_lobby, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        matrixView = view.findViewById(R.id.matrix)

        matrixView.layoutManager = GridLayoutManager(requireContext(), matrix.rowCapacity())
        matrixView.adapter = MatrixAdapter(requireContext(), matrix)

        setLayoutParams()
    }

    private fun setLayoutParams() {
        val metrics = resources.displayMetrics

        val matrixLayout = requireView().findViewById<LinearLayout>(R.id.matrix_layout)
        val playersLayout = requireView().findViewById<LinearLayout>(R.id.players_layout)

        matrixLayout.layoutParams.width = metrics.widthPixels / 2
        playersLayout.layoutParams.width = metrics.widthPixels / 2
        matrixLayout.layoutParams.height = metrics.heightPixels - convertToPixels(50)
        playersLayout.layoutParams.height = metrics.heightPixels - convertToPixels(50)

        matrixView.layoutParams.height = matrixLayout.layoutParams.height - convertToPixels(60)
    }

    private fun convertToPixels(dp: Int): Int {
        return dp * resources.displayMetrics.densityDpi / 160
    }
}