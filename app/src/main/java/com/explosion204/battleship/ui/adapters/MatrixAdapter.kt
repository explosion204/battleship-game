package com.explosion204.battleship.ui.adapters

import android.app.Activity
import android.content.Context
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.explosion204.battleship.Matrix
import com.explosion204.battleship.R

class MatrixAdapter(private val context: Context, private var matrix: Matrix) : RecyclerView.Adapter<MatrixAdapter.CellViewHolder>() {
    private val inflater = LayoutInflater.from(context)

    fun setMatrix(matrix: Matrix) {
        this.matrix = matrix
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CellViewHolder {
        val view = inflater.inflate(R.layout.cell_item, parent, false)

        val width = parent.width / matrix.rowCapacity()
        val height = parent.height / matrix.rowsCount()

        view.layoutParams.height = height
        view.layoutParams.width = width

        return CellViewHolder(view)
    }

    override fun getItemCount(): Int {
        return matrix.rowsCount() * matrix.rowCapacity()
    }

    override fun onBindViewHolder(holder: CellViewHolder, position: Int) {
        val i = position % matrix.rowCapacity()
        val j = position / matrix.rowsCount()

        if (matrix[i, j]) {
            holder.cellImageView.setImageResource(R.drawable.black_square)
        }
        else {
            holder.cellImageView.setImageResource(R.drawable.square)
        }
    }

    class CellViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        val cellImageView: ImageView = itemView.findViewById(R.id.cell_image_view)
    }
}