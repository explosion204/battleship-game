package com.explosion204.battleship.viewmodels

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.explosion204.battleship.data.models.User
import com.explosion204.battleship.data.repos.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class UserViewModel @Inject constructor(private val userRepository: UserRepository) : ViewModel() {
    var userNickname = ""

    private val user = MutableLiveData<User>()

    fun getUser(id: String): LiveData<User>  {
        viewModelScope.launch(Dispatchers.IO) {
            userRepository.getUser(id).addSnapshotListener { value, _ ->
                if (value != null) {
                    user.postValue(User(
                        value["userId"].toString(),
                        value["nickname"].toString(),
                        value["profileImageUri"].toString()
                    ))
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
}