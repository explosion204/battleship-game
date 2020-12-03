package com.explosion204.battleship.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.explosion204.battleship.Constants
import com.explosion204.battleship.GameController
import com.explosion204.battleship.Matrix
import com.explosion204.battleship.R
import com.explosion204.battleship.ui.adapters.MatrixAdapter
import com.explosion204.battleship.ui.interfaces.OnItemClickListener
import com.explosion204.battleship.viewmodels.GameViewModel
import com.explosion204.battleship.viewmodels.UserViewModel
import com.explosion204.battleship.viewmodels.ViewModelFactory
import dagger.android.support.DaggerFragment
import javax.inject.Inject

class BattleshipFragment : DaggerFragment() {
    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private val gameViewModel: GameViewModel by activityViewModels {
        viewModelFactory
    }
    private val userViewModel: UserViewModel by activityViewModels {
        viewModelFactory
    }

    private lateinit var playerMatrixView: RecyclerView
    private lateinit var opponentMatrixView: RecyclerView
    private lateinit var playerMatrixAdapter: MatrixAdapter
    private lateinit var opponentMatrixAdapter: MatrixAdapter
    private lateinit var playerNickname: TextView
    private lateinit var opponentNickname: TextView
    private lateinit var playerPic: ImageView
    private lateinit var opponentPic: ImageView
    private lateinit var playerStatus: TextView
    private lateinit var opponentStatus: TextView

    private var isHost = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_battleship, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        playerMatrixView = view.findViewById(R.id.player_matrix)
        opponentMatrixView = view.findViewById(R.id.opponent_matrix)
        playerNickname = view.findViewById(R.id.player_nickname)
        opponentNickname = view.findViewById(R.id.opponent_nickname)
        playerPic = view.findViewById(R.id.player_pic)
        opponentPic = view.findViewById(R.id.opponent_pic)
        playerStatus = view.findViewById(R.id.player_status)
        opponentStatus = view.findViewById(R.id.opponent_status)

        val playerMatrix = gameViewModel.gameController.matrix.value!!
        val opponentMatrix = gameViewModel.gameController.opponentMatrix.value!!

        playerMatrixView.layoutManager =
            GridLayoutManager(requireContext(), playerMatrix.rowCapacity())
        playerMatrixAdapter = MatrixAdapter(requireContext(), playerMatrix)
        playerMatrixView.adapter = playerMatrixAdapter

        opponentMatrixView.layoutManager =
            GridLayoutManager(requireContext(), opponentMatrix.rowCapacity())
        opponentMatrixAdapter = MatrixAdapter(requireContext(), opponentMatrix)
        opponentMatrixView.adapter = opponentMatrixAdapter

        isHost = requireActivity().intent.getBooleanExtra(Constants.IS_HOST_EXTRA, false)

        setLayoutParams()
        setListeners()
        setObservables()
    }

    private fun setLayoutParams() {
        val metrics = resources.displayMetrics

        val playerMatrixLayout = requireView().findViewById<LinearLayout>(R.id.player_matrix_layout)
        val opponentMatrixLayout =
            requireView().findViewById<LinearLayout>(R.id.opponent_matrix_layout)

        playerMatrixLayout.layoutParams.width = metrics.widthPixels / 2
        opponentMatrixLayout.layoutParams.width = metrics.widthPixels / 2

        playerMatrixView.layoutParams.height = metrics.heightPixels - convertToPixels(70)
        playerMatrixView.layoutParams.width = metrics.widthPixels / 2 - convertToPixels(20)
        opponentMatrixView.layoutParams.height = metrics.heightPixels - convertToPixels(70)
        opponentMatrixView.layoutParams.width = metrics.widthPixels / 2 - convertToPixels(20)
    }

    private fun convertToPixels(dp: Int): Int {
        return dp * resources.displayMetrics.densityDpi / 160
    }

    private fun setListeners() {
        opponentMatrixAdapter.setOnItemClickListener(object : OnItemClickListener {
            override fun onItemClick(item: Any) {
                gameViewModel.sendFireRequest(item.toString())
            }
        })
    }

    private fun setObservables() {
        gameViewModel.hostId.observe(viewLifecycleOwner, Observer { userId ->
            if (userId.isNotEmpty()) {
                when (userId) {
                    Constants.HOST_DISCONNECTED -> {
                        requireActivity().finish()
                    }
                    else -> {
                        userViewModel.getUser(userId).observe(viewLifecycleOwner, Observer { user ->
                            if (isHost) {
                                playerNickname.text = user.nickname
                            } else {
                                opponentNickname.text = user.nickname
                            }
                        })
                    }
                }
            }
        })

        gameViewModel.guestId.observe(viewLifecycleOwner, Observer { userId ->
            if (userId != null && userId.isNotEmpty()) {
                when (userId) {
                    Constants.GUEST_DISCONNECTED -> {
                        requireActivity().finish()
                    }
                    else -> {
                        userViewModel.getUser(userId).observe(viewLifecycleOwner, Observer { user ->
                            if (!isHost) {
                                playerNickname.text = user.nickname
                            } else {
                                opponentNickname.text = user.nickname
                            }
                        })
                    }
                }
            }
        })

        gameViewModel.hostBitmap.observe(viewLifecycleOwner, Observer {
            if (isHost) {
                playerPic.setImageBitmap(it)
            } else {
                opponentPic.setImageBitmap(it)
            }
        })

        gameViewModel.guestBitmap.observe(viewLifecycleOwner, Observer {
            if (!isHost) {
                playerPic.setImageBitmap(it)
            } else {
                opponentPic.setImageBitmap(it)
            }
        })

        gameViewModel.hostTurn.observe(viewLifecycleOwner, Observer {
            if (isHost) {
                if (it) {
                    playerStatus.text = getString(R.string.firing)
                    opponentStatus.text = ""
                } else {
                    playerStatus.text = ""
                    opponentStatus.text = getString(R.string.firing)
                }
            } else {
                if (it) {
                    playerStatus.text = ""
                    opponentStatus.text = getString(R.string.firing)
                } else {
                    playerStatus.text = getString(R.string.firing)
                    opponentStatus.text = ""
                }
            }
        })

        gameViewModel.gameController.matrix.observe(viewLifecycleOwner, Observer {
            playerMatrixAdapter.setMatrix(it)
        })

        gameViewModel.gameController.opponentMatrix.observe(viewLifecycleOwner, Observer {
            opponentMatrixAdapter.setMatrix(it)
        })
    }
}