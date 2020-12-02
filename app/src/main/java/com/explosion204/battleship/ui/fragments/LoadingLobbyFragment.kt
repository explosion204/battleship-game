package com.explosion204.battleship.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import com.explosion204.battleship.Constants
import com.explosion204.battleship.R
import com.explosion204.battleship.viewmodels.GameViewModel
import com.explosion204.battleship.viewmodels.ViewModelFactory
import com.google.firebase.auth.FirebaseAuth
import dagger.android.support.DaggerFragment
import javax.inject.Inject

class LoadingLobbyFragment : DaggerFragment() {
    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val gameViewModel: GameViewModel by activityViewModels {
        viewModelFactory
    }
    private val mAuth = FirebaseAuth.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_loading_lobby, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        if (mAuth.currentUser != null) {
            if (requireActivity().intent.getBooleanExtra(Constants.IS_HOST_EXTRA, false)) {
                gameViewModel.initNewSession(mAuth.currentUser!!.uid) {
                    Navigation.findNavController(requireView()).navigate(R.id.action_loadingLobbyFragment_to_lobbyFragment)
                }
            }
            else {
                gameViewModel.fetchSession(requireActivity().intent.getLongExtra(Constants.SESSION_ID_EXTRA, 0), mAuth.currentUser!!.uid,
                { // success
                    Navigation.findNavController(requireView()).navigate(R.id.action_loadingLobbyFragment_to_lobbyFragment)
                }, // failure
                {
                    requireActivity().finish()
                })
            }
        }
    }
}