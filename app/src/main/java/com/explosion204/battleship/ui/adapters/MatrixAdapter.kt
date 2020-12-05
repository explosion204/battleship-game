package com.explosion204.battleship.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.explosion204.battleship.Constants.Companion.MATRIX_FREE_CELL
import com.explosion204.battleship.Constants.Companion.MATRIX_HIT_CELL
import com.explosion204.battleship.Constants.Companion.MATRIX_MISSED_CELL
import com.explosion204.battleship.Constants.Companion.MATRIX_TAKEN_CELL
import com.explosion204.battleship.Matrix
import com.explosion204.battleship.R
import com.explosion204.battleship.ui.interfaces.OnItemClickListener

class MatrixAdapter(private val context: Context, private var matrix: Matrix) :
    RecyclerView.Adapter<MatrixAdapter.CellViewHolder>() {
    private val inflater = LayoutInflater.from(context)
    private var itemClickListener: OnItemClickListener? = null
    private var clickAllowed = true

    fun setOnItemClickListener(listener: OnItemClickListener) {
        itemClickListener = listener
    }

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
        clickAllowed = true

        when (matrix[i, j]) {
            MATRIX_FREE_CELL -> {
                holder.cellImageView.setImageResource(R.drawable.square)
                holder.itemView.setOnClickListener {
                    if (itemClickListener != null && clickAllowed) {
                        itemClickListener!!.onItemClick(i, j)
                        clickAllowed = false
                    }
                }
            }
            MATRIX_TAKEN_CELL -> {
                holder.cellImageView.setImageResource(R.drawable.black_square)
            }
            MATRIX_HIT_CELL -> {
                holder.cellImageView.setImageResource(R.drawable.crossed_square)
            }
            MATRIX_MISSED_CELL -> {
                holder.cellImageView.setImageResource(R.drawable.pointed_square)
            }
        }
    }

    class CellViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        val cellImageView: ImageView = itemView.findViewById(R.id.cell_image_view)
    }
}