package com.explosion204.battleship.ui.fragments

import android.app.Activity.RESULT_OK
import android.app.Dialog
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatEditText
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import com.explosion204.battleship.Constants.Companion.PICK_IMAGE_CODE
import com.explosion204.battleship.Constants.Companion.USER_ID
import com.explosion204.battleship.R
import com.explosion204.battleship.ui.util.CircleTransform
import com.explosion204.battleship.viewmodels.UserViewModel
import com.explosion204.battleship.viewmodels.ViewModelFactory
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso
import dagger.android.support.DaggerDialogFragment
import javax.inject.Inject

class EditUserDialogFragment : DaggerDialogFragment() {
    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val userViewModel: UserViewModel by activityViewModels {
        viewModelFactory
    }

    private lateinit var nicknameEditText: AppCompatEditText
    private lateinit var profileImageView: ImageView
    private lateinit var saveChangesButton: Button

    private val mAuth = FirebaseAuth.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_user_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        nicknameEditText = view.findViewById(R.id.nickname)
        saveChangesButton = view.findViewById(R.id.save_changes_button)

        if (userViewModel.userNickname.isNotEmpty()) {
            nicknameEditText.setText(userViewModel.userNickname)
        }

        saveChangesButton.setOnClickListener {
            userViewModel.setUserNickname(requireArguments().getString(USER_ID)!!, nicknameEditText.text.toString())
            dismiss()
        }
        profileImageView = view.findViewById<ImageButton>(R.id.profile_image)

        profileImageView.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            if (intent.resolveActivity(requireActivity().packageManager) != null) {
                startActivityForResult(intent, PICK_IMAGE_CODE)
            }
        }

        if (mAuth.currentUser != null) {
            userViewModel.getUser(mAuth.currentUser!!.uid).observe(viewLifecycleOwner) {
                if (it.nickname.isNotEmpty()) {
                    saveChangesButton.isEnabled = true
                    nicknameEditText.setText(it.nickname)
                }
                Picasso.get().load(it.profileImageUri).transform(CircleTransform()).into(profileImageView)
            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)

        return dialog
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            PICK_IMAGE_CODE -> {
                if (resultCode == RESULT_OK) {
                    if (data != null && data.data != null) {
                        var stream = requireActivity().contentResolver.openInputStream(data.data!!)
                        var bitmap = BitmapFactory.decodeStream(stream)
                        if (mAuth.currentUser != null) {
                            userViewModel.uploadProfileImage(mAuth.currentUser!!.uid, bitmap)
                        }
                    }
                }
            }
        }
    }
}