package com.explosion204.battleship.ui.fragments

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.widget.AppCompatEditText
import androidx.fragment.app.activityViewModels
import com.explosion204.battleship.Constants.Companion.IS_HOST_EXTRA
import com.explosion204.battleship.Constants.Companion.SESSION_ID_EXTRA
import com.explosion204.battleship.R
import com.explosion204.battleship.ui.activities.GameActivity
import com.explosion204.battleship.viewmodels.GameViewModel
import com.explosion204.battleship.viewmodels.ViewModelFactory
import com.google.firebase.auth.FirebaseAuth
import dagger.android.support.DaggerDialogFragment
import java.lang.NumberFormatException
import javax.inject.Inject

class JoinGameDialogFragment : DaggerDialogFragment() {
    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private val gameViewModel: GameViewModel by activityViewModels {
        viewModelFactory
    }


    private lateinit var lobbyId: AppCompatEditText
    private val mAuth = FirebaseAuth.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_join_game_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        lobbyId = view.findViewById(R.id.lobby_id)

        view.findViewById<Button>(R.id.connect_button).setOnClickListener {
            it.isEnabled = false

            if (mAuth.currentUser != null) {
                try {
                    gameViewModel.fetchSession(
                        lobbyId.text.toString().toLong(),
                        mAuth.currentUser!!.uid,
                        { // success callback
                            if (gameViewModel.guestId.value != null) {
                                if (gameViewModel.hostId.value != mAuth.currentUser!!.uid) {
                                    val intent = Intent(requireContext(), GameActivity::class.java)
                                    intent.putExtra(IS_HOST_EXTRA, false)
                                    intent.putExtra(SESSION_ID_EXTRA, lobbyId.text.toString().toLong())
                                    startActivity(intent)
                                    dismiss()
                                } else {
                                    it.isEnabled = true
                                    Toast.makeText(requireContext(), getString(R.string.lobby_is_full), Toast.LENGTH_SHORT).show()
                                }
                            } else {
                                it.isEnabled = true
                                Toast.makeText(requireContext(), getString(R.string.lobby_is_full), Toast.LENGTH_SHORT).show()
                            }
                        },
                        { // failure callback
                            it.isEnabled = true
                            Toast.makeText(requireContext(), getString(R.string.invalid_lobby_id), Toast.LENGTH_SHORT).show()
                        }
                    )
                } catch (e: NumberFormatException) {
                    it.isEnabled = true
                    Toast.makeText(requireContext(), getString(R.string.invalid_lobby_id), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)

        return dialog
    }
}