package com.explosion204.battleship.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.explosion204.battleship.Constants.Companion.GUEST_DISCONNECTED
import com.explosion204.battleship.Constants.Companion.HOST_DISCONNECTED
import com.explosion204.battleship.Constants.Companion.IS_HOST_EXTRA
import com.explosion204.battleship.Constants.Companion.SESSION_ID_EXTRA
import com.explosion204.battleship.GameController
import com.explosion204.battleship.Matrix
import com.explosion204.battleship.R
import com.explosion204.battleship.ui.adapters.MatrixAdapter
import com.explosion204.battleship.ui.util.CircleTransform
import com.explosion204.battleship.viewmodels.GameViewModel
import com.explosion204.battleship.viewmodels.UserViewModel
import com.explosion204.battleship.viewmodels.ViewModelFactory
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso
import dagger.android.support.DaggerFragment
import javax.inject.Inject

class LobbyFragment : DaggerFragment() {
    private lateinit var matrixView: RecyclerView
    private lateinit var matrixAdapter: MatrixAdapter

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val gameViewModel: GameViewModel by activityViewModels {
        viewModelFactory
    }
    private val userViewModel: UserViewModel by activityViewModels {
        viewModelFactory
    }

    private lateinit var lobbyId: TextView
    private lateinit var hostPlayerPic: ImageView
    private lateinit var hostPlayerNickname: TextView
    private lateinit var guestPlayerPic: ImageView
    private lateinit var guestPlayerNickname: TextView
    private lateinit var hostReady: ImageView
    private lateinit var guestReady: ImageView
    private lateinit var readyButton: Button
    private lateinit var randomizeButton: Button
    private lateinit var startButton: Button

    private var isHost = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_lobby, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        matrixView = view.findViewById(R.id.matrix)
        val initialMatrix = Matrix(10, 10)
        matrixView.layoutManager = GridLayoutManager(requireContext(), initialMatrix.rowCapacity())
        matrixAdapter = MatrixAdapter(requireContext(), initialMatrix)
        matrixView.adapter = matrixAdapter
        setLayoutParams()

        gameViewModel.gameController.generateMatrix()

        lobbyId = view.findViewById(R.id.lobby_id)
        hostPlayerPic = view.findViewById(R.id.host_player_pic)
        hostPlayerNickname = view.findViewById(R.id.host_player_nickname)
        guestPlayerPic = view.findViewById(R.id.guest_player_pic)
        guestPlayerNickname = view.findViewById(R.id.guest_player_nickname)
        hostReady = view.findViewById(R.id.host_ready)
        guestReady = view.findViewById(R.id.guest_ready)
        readyButton = view.findViewById(R.id.ready_button)
        randomizeButton = view.findViewById(R.id.randomize_button)
        startButton = view.findViewById(R.id.start_button)

        isHost = requireActivity().intent.getBooleanExtra(IS_HOST_EXTRA, false)

        setObservables()
        setListeners()
    }

    private fun setLayoutParams() {
        val metrics = resources.displayMetrics

        val matrixLayout = requireView().findViewById<LinearLayout>(R.id.matrix_layout)
        val playersLayout = requireView().findViewById<LinearLayout>(R.id.players_layout)

        matrixLayout.layoutParams.width = metrics.widthPixels / 2
        playersLayout.layoutParams.width = metrics.widthPixels / 2
        matrixLayout.layoutParams.height = metrics.heightPixels - convertToPixels(50)
        playersLayout.layoutParams.height = metrics.heightPixels - convertToPixels(50)

        matrixView.layoutParams.height = matrixLayout.layoutParams.height - convertToPixels(60)
    }

    private fun convertToPixels(dp: Int): Int {
        return dp * resources.displayMetrics.densityDpi / 160
    }

    private fun setListeners() {
        readyButton.setOnClickListener {
            gameViewModel.changeReady()
        }

        randomizeButton.setOnClickListener {
            gameViewModel.gameController.generateMatrix()
        }

        startButton.setOnClickListener {
            gameViewModel.setGameRunning(true)
        }
    }

    private fun setObservables() {
        gameViewModel.sessionId.observe(viewLifecycleOwner, Observer {
            lobbyId.text = "${getString(R.string.lobby_id)}: $it"
        })

        gameViewModel.hostId.observe(viewLifecycleOwner, Observer { userId ->
            if (userId.isNotEmpty()) {
                when (userId) {
                    HOST_DISCONNECTED -> {
                        requireActivity().finish()
                    }
                    else -> {
                        userViewModel.getUser(userId).observe(viewLifecycleOwner, Observer { user ->
                            hostPlayerNickname.text = user.nickname
                        })
                    }
                }
            }
        })

        gameViewModel.guestId.observe(viewLifecycleOwner, Observer { userId ->
            if (userId != null && userId.isNotEmpty()) {
                when (userId) {
                    GUEST_DISCONNECTED -> {
                        guestPlayerNickname.text = getString(R.string.empty_slot)
                        guestPlayerPic.setImageResource(0)
                    }
                    else -> {
                        userViewModel.getUser(userId).observe(viewLifecycleOwner, Observer { user ->
                            guestPlayerNickname.text = user.nickname
                        })
                    }
                }
            }
        })

        gameViewModel.hostBitmap.observe(viewLifecycleOwner, Observer {
            hostPlayerPic.setImageBitmap(it)
        })

        gameViewModel.guestBitmap.observe(viewLifecycleOwner, Observer {
            guestPlayerPic.setImageBitmap(it)
        })

        gameViewModel.hostReady.observe(viewLifecycleOwner, Observer {
            if (isHost) {
                readyButton.text =
                    if (it) getString(R.string.not_ready) else getString(R.string.ready)
            }

            hostReady.setColorFilter(
                ContextCompat.getColor(
                    requireContext(),
                    if (it) R.color.colorPrimary else android.R.color.darker_gray
                )
            )

            startButton.isEnabled = it && gameViewModel.guestReady.value!!
        })

        gameViewModel.guestReady.observe(viewLifecycleOwner, Observer {
            if (!isHost) {
                readyButton.text =
                    if (it) getString(R.string.not_ready) else getString(R.string.ready)
            }

            guestReady.setColorFilter(
                ContextCompat.getColor(
                    requireContext(),
                    if (it) R.color.colorPrimary else android.R.color.darker_gray
                )
            )

            startButton.isEnabled = it && gameViewModel.hostReady.value!!
        })

        gameViewModel.gameController.matrix.observe(viewLifecycleOwner, Observer {
            matrixAdapter.setMatrix(it)
        })

        gameViewModel.gameRunning.observe(viewLifecycleOwner, Observer {
            if (it) {
                Navigation.findNavController(requireView())
                    .navigate(R.id.action_lobbyFragment_to_battleshipFragment)
            }
        })
    }
}