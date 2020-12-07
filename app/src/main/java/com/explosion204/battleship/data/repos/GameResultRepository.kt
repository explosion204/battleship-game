package com.explosion204.battleship.data.repos

import com.explosion204.battleship.data.models.GameResult
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import javax.inject.Inject

class GameResultRepository @Inject constructor(private val fireStore: FirebaseFirestore) {

    fun getGameResults(
        userId: String,
        onSuccess: (results: ArrayList<GameResult>) -> Unit,
        onFailure: () -> Unit
    ) {
        fireStore.collection("gameResults")
            .whereEqualTo("firstPlayerId", userId)
            .get()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    val docs = it.result

                    if (docs != null && docs.size() > 0) {
                        val gameResults = buildGameResults(docs)
                        onSuccess(gameResults)
                    } else {
                        fireStore.collection("gameResults")
                            .whereEqualTo("secondPlayerId", userId)
                            .get()
                            .addOnCompleteListener {
                                val docs = it.result

                                if (docs != null && docs.size() > 0) {
                                    val gameResults = buildGameResults(docs)
                                    onSuccess(gameResults)
                                } else {
                                    onFailure()
                                }
                            }
                    }
                }
            }
    }

    fun addGameResult(gameResult: GameResult) {
        fireStore.collection("gameResults").add(gameResult)
    }

    private fun buildGameResults(docs: QuerySnapshot): ArrayList<GameResult> {
        val list = ArrayList<GameResult>()
        docs.forEach {
            list.add(
                GameResult(
                    firstPlayerId = it["firstPlayerId"].toString(),
                    secondPlayerId = it["secondPlayerId"].toString(),
                    opponentNickname = it["outcome"].toString()
                )
            )
        }

        return list
    }
}