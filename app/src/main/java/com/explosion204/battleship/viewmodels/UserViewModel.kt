package com.explosion204.battleship.viewmodels

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.explosion204.battleship.data.models.GameResult
import com.explosion204.battleship.data.models.User
import com.explosion204.battleship.data.repos.GameResultRepository
import com.explosion204.battleship.data.repos.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class UserViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val gameResultRepository: GameResultRepository
) : ViewModel() {
    var userNickname = ""
    var gameResults = MutableLiveData(ArrayList<GameResult>())

    fun getUser(id: String): LiveData<User> {
        val user = MutableLiveData<User>()

        userRepository.getUser(id) {
            it.addSnapshotListener { value, _ ->
                if (value != null) {
                    user.postValue(
                        User(
                            value["userId"].toString(),
                            value["nickname"].toString(),
                            value["profileImageUri"].toString()
                        )
                    )
                    userNickname = value["nickname"].toString()
                }
            }
        }

        return user
    }

    fun setUserNickname(id: String, newNickname: String) {
        userRepository.setUserNickname(id, newNickname)
    }

    fun uploadProfileImage(uid: String, bitmap: Bitmap) {
        userRepository.uploadProfileImage(uid, bitmap)
    }

    fun fetchGameResults(userId: String) {
        val results = ArrayList<GameResult>()
        gameResultRepository.getGameResults(userId, { gameResults ->
            gameResults.forEach { result ->
                userRepository.getUser(if (userId != result.firstPlayerId) result.firstPlayerId else result.secondPlayerId) {
                    it.addSnapshotListener { value, _ ->
                        if (value != null) {
                            result.opponentNickname = value["nickname"].toString()
                            results.add(result)
                            this.gameResults.postValue(results)
                        }
                    }
                }
            }
        }, {})
    }
}