package com.explosion204.battleship.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import com.explosion204.battleship.Constants.Companion.USER_ID
import com.explosion204.battleship.Constants.Companion.USER_NICKNAME
import com.explosion204.battleship.R
import com.explosion204.battleship.ui.activities.BattleshipActivity
import com.explosion204.battleship.ui.activities.LobbyActivity
import com.explosion204.battleship.viewmodels.UserViewModel
import com.explosion204.battleship.viewmodels.ViewModelFactory
import com.google.firebase.auth.FirebaseAuth
import dagger.android.support.DaggerFragment
import javax.inject.Inject

class StartupFragment : DaggerFragment(), FirebaseAuth.AuthStateListener {
    private val mAuth = FirebaseAuth.getInstance()

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val userViewModel : UserViewModel by activityViewModels {
        viewModelFactory
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_startup, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        view.findViewById<ImageButton>(R.id.host_button).setOnClickListener {
            val intent = Intent(requireContext(), LobbyActivity::class.java)
            startActivity(intent)
        }

        view.findViewById<ImageButton>(R.id.join_button).setOnClickListener {
            val intent = Intent(requireContext(), BattleshipActivity::class.java)
            startActivity(intent)
        }

        view.findViewById<ImageButton>(R.id.edit_button).setOnClickListener {
            val dialogFragment = EditUserDialogFragment()
            val args = Bundle()
            args.putString(USER_NICKNAME, userViewModel.userNickname)
            args.putString(USER_ID, mAuth.currentUser!!.uid)

            dialogFragment.arguments = args
            dialogFragment.show(requireActivity().supportFragmentManager, "DIALOG_FRAGMENT")
        }
    }

    override fun onStart() {
        super.onStart()
        mAuth.addAuthStateListener(this)
    }

    override fun onStop() {
        super.onStop()
        mAuth.removeAuthStateListener(this)
    }

    override fun onAuthStateChanged(mAuth: FirebaseAuth) {

        if (mAuth.currentUser == null) {
            Navigation.findNavController(requireView()).navigate(R.id.action_startupFragment_to_signinFragment)
        }
    }

}