package com.practicum.playlistmaker.library.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentFavouritesBinding
import com.practicum.playlistmaker.library.presentation.FavouritesState
import com.practicum.playlistmaker.library.presentation.FavouritesViewModel
import com.practicum.playlistmaker.player.ui.PlayerFragment
import com.practicum.playlistmaker.search.domain.entity.Track
import com.practicum.playlistmaker.search.ui.CURRENT_TRACK
import com.practicum.playlistmaker.search.ui.TrackAdapter
import com.practicum.playlistmaker.utils.Debounce
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavouritesFragment: Fragment() {

    private val favouritesViewModel by viewModel<FavouritesViewModel>()

    private lateinit var recyclerView: RecyclerView

    private lateinit var binding: FragmentFavouritesBinding
    private lateinit var onTrackClickDebounce: (Track) -> Unit

    private val trackAdapter = TrackAdapter {
        onTrackClickDebounce(it)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFavouritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = binding.recyclerView
        recyclerView.adapter = trackAdapter

        favouritesViewModel.observeFavouritesState().observe(viewLifecycleOwner) {
            render(it)
        }

        onTrackClickDebounce = Debounce().debounce(
            CLICK_DEBOUNCE_DELAY_MILLIS,
            viewLifecycleOwner.lifecycleScope,
            false,
        ) {
            findNavController().navigate(R.id.action_libraryFragment_to_playerFragment, PlayerFragment.createArgs(it))
        }
    }

    override fun onResume() {
        super.onResume()
        favouritesViewModel.updateFavourites()
    }

    private fun render(state: FavouritesState) {
        when (state) {
            is FavouritesState.Loading -> showLoading()
            is FavouritesState.Empty -> showEmpty(state.message)
            is FavouritesState.Content -> showTracks(state.favourites)
        }
    }

    private fun showLoading() {
        recyclerView.visibility = View.GONE
        binding.favouritesPlaceholder.visibility = View.GONE
    }

    private fun showEmpty(message: String) {
        recyclerView.visibility = View.GONE
        binding.favouritesPlaceholder.visibility = View.VISIBLE

        binding.placeHolderText.text = message
    }

    private fun showTracks(tracks: List<Track>) {
        recyclerView.visibility = View.VISIBLE
        binding.favouritesPlaceholder.visibility = View.GONE

        trackAdapter.tracks.clear()
        trackAdapter.tracks.addAll(tracks)
        trackAdapter.notifyDataSetChanged()
    }

    companion object {
        private const val CLICK_DEBOUNCE_DELAY_MILLIS = 300L
        fun newInstance(): FavouritesFragment {
            return FavouritesFragment()
        }
    }
}