package com.explosion204.battleship.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.Navigation
import com.explosion204.battleship.Constants.Companion.IS_HOST_EXTRA
import com.explosion204.battleship.Constants.Companion.USER_ID
import com.explosion204.battleship.R
import com.explosion204.battleship.ui.activities.GameActivity
import com.explosion204.battleship.viewmodels.ViewModelFactory
import com.google.firebase.auth.FirebaseAuth
import dagger.android.support.DaggerFragment
import javax.inject.Inject

class StartupFragment : DaggerFragment(), FirebaseAuth.AuthStateListener {
    private val mAuth = FirebaseAuth.getInstance()

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_startup, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        view.findViewById<Button>(R.id.host_button).setOnClickListener {
            val intent = Intent(requireContext(), GameActivity::class.java)
            intent.putExtra(IS_HOST_EXTRA, true)
            startActivity(intent)
        }

        view.findViewById<Button>(R.id.join_button).setOnClickListener {
            val dialogFragment = JoinGameDialogFragment()
            dialogFragment.show(requireActivity().supportFragmentManager, "DIALOG_FRAGMENT")
        }

        // TODO: fetch data before
        view.findViewById<Button>(R.id.edit_button).setOnClickListener {
            val dialogFragment = EditUserDialogFragment()
            val args = Bundle()
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