package com.explosion204.battleship.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.explosion204.battleship.R
import com.explosion204.battleship.ui.adapters.StatsAdapter
import com.explosion204.battleship.viewmodels.UserViewModel
import com.explosion204.battleship.viewmodels.ViewModelFactory
import com.google.firebase.auth.FirebaseAuth
import dagger.android.support.DaggerFragment
import javax.inject.Inject

class StatsFragment : DaggerFragment() {
    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private val userViewModel: UserViewModel by activityViewModels {
        viewModelFactory
    }

    private lateinit var gameResultsView: RecyclerView
    private lateinit var adapter: StatsAdapter
    private val mAuth = FirebaseAuth.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_stats, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        gameResultsView = view.findViewById(R.id.game_results)
        gameResultsView.layoutManager = LinearLayoutManager(requireContext())

        if (mAuth.currentUser != null) {
            adapter = StatsAdapter(requireContext(), ArrayList(), mAuth.currentUser!!.uid)
            gameResultsView.adapter = adapter
            userViewModel.fetchGameResults(mAuth.currentUser!!.uid)
            setObservables()
        }
    }

    private fun setObservables() {
        userViewModel.gameResults.observe(viewLifecycleOwner, Observer {
            adapter.setCollection(it)
        })
    }
}