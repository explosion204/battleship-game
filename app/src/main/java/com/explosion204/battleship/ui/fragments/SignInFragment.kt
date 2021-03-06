package com.explosion204.battleship.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.navigation.Navigation
import com.explosion204.battleship.Constants.Companion.GOOGLE_SIGN_IN
import com.explosion204.battleship.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import dagger.android.support.DaggerFragment

class SignInFragment : DaggerFragment() {
    private lateinit var mAuth: FirebaseAuth
    private lateinit var loadingView: ProgressBar
    private lateinit var signInWithGoogleButton: CardView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_signin, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mAuth = FirebaseAuth.getInstance()
        mAuth.signOut()

        if (mAuth.currentUser != null) {
            Navigation.findNavController(view)
                .navigate(R.id.action_signinFragment_to_startupFragment)
        }

        loadingView = view.findViewById(R.id.loading_view)
        signInWithGoogleButton = view.findViewById(R.id.sign_in_google)
        signInWithGoogleButton.setOnClickListener {
            signInWithGoogle()
        }
    }

    private fun signInWithGoogle() {
        signInWithGoogleButton.isEnabled = false
        loadingView.visibility = View.VISIBLE

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        val mGoogleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)

        startActivityForResult(mGoogleSignInClient.signInIntent, GOOGLE_SIGN_IN)
    }

    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        mAuth.signInWithCredential(credential)
            .addOnSuccessListener {
                Navigation.findNavController(requireView())
                    .navigate(R.id.action_signinFragment_to_startupFragment)
            }
            .addOnFailureListener {
                signInWithGoogleButton.isEnabled = true
                loadingView.visibility = View.GONE
                Toast.makeText(
                    requireContext(),
                    getString(R.string.google_sign_in_error2),
                    Toast.LENGTH_LONG
                ).show()
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            GOOGLE_SIGN_IN -> {
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                try {
                    val account = task.getResult(ApiException::class.java)
                    if (account != null) {
                        firebaseAuthWithGoogle(account)
                    }
                } catch (e: ApiException) {
                    signInWithGoogleButton.isEnabled = true
                    loadingView.visibility = View.GONE
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.google_sign_in_error_1),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }
}