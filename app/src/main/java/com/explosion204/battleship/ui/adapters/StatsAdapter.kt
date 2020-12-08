package com.explosion204.battleship.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.explosion204.battleship.Constants.Companion.OUTCOME_FIRST_PLAYER_WON
import com.explosion204.battleship.Constants.Companion.OUTCOME_SECOND_PLAYER_WON
import com.explosion204.battleship.R
import com.explosion204.battleship.data.models.GameResult

class StatsAdapter(
    private val context: Context,
    private var collection: ArrayList<GameResult>,
    private val userId: String
) :
    RecyclerView.Adapter<StatsAdapter.StatsViewHolder>() {

    private val inflater = LayoutInflater.from(context)

    fun setCollection(collection: ArrayList<GameResult>) {
        this.collection = collection
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StatsViewHolder {
        val view = inflater.inflate(R.layout.game_result_item, parent, false)

        return StatsViewHolder(view)
    }

    override fun getItemCount(): Int {
        return collection.size
    }

    override fun onBindViewHolder(holder: StatsViewHolder, position: Int) {
        val gameResult = collection[position]
        holder.outcomeTextView.text = when (gameResult.outcome) {
            OUTCOME_FIRST_PLAYER_WON -> if (userId == gameResult.firstPlayerId) context.getString(R.string.won) else context.getString(R.string.lost)
            OUTCOME_SECOND_PLAYER_WON -> if (userId == gameResult.firstPlayerId) context.getString(R.string.lost) else context.getString(R.string.won)
            else -> ""
        }
        holder.nicknameTextView.text = gameResult.opponentNickname
    }

    inner class StatsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val outcomeTextView: TextView = itemView.findViewById(R.id.outcome)
        val nicknameTextView: TextView = itemView.findViewById(R.id.nickname)
    }

}