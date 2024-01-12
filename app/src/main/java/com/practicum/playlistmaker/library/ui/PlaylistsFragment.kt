package com.practicum.playlistmaker.library.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentPlaylistsBinding
import com.practicum.playlistmaker.library.domain.entity.Playlist
import com.practicum.playlistmaker.library.presentation.PlaylistsState
import com.practicum.playlistmaker.library.presentation.PlaylistsViewModel
import com.practicum.playlistmaker.player.ui.PlayerFragment
import com.practicum.playlistmaker.utils.Debounce
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistsFragment: Fragment() {

    private val playlistsViewModel by viewModel<PlaylistsViewModel>()
    private lateinit var recyclerView: RecyclerView

    private lateinit var binding: FragmentPlaylistsBinding
    private lateinit var onPlaylistClickDebounce: (Playlist) -> Unit

    private val playlistsAdapter = PlaylistsAdapter{
        onPlaylistClickDebounce(it)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPlaylistsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.newPlaylistButton.setOnClickListener{
            findNavController().navigate(R.id.action_libraryFragment_to_addNewPlaylistFragment)
        }

        recyclerView = binding.playlistsRecyclerView
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        recyclerView.adapter = playlistsAdapter

        playlistsViewModel.observePlaylistsState().observe(viewLifecycleOwner) {
            render(it)
        }

        onPlaylistClickDebounce = Debounce().debounce(
            CLICK_DEBOUNCE_DELAY_MILLIS,
            viewLifecycleOwner.lifecycleScope,
            false,
        ) {
            findNavController().navigate(R.id.action_libraryFragment_to_playlistUnitFragment, PlaylistUnitFragment.createArgs(it))
        }
    }

    override fun onResume() {
        super.onResume()
        playlistsViewModel.updatePlaylists()
    }

    private fun render(state: PlaylistsState) {
        when (state) {
            is PlaylistsState.Empty -> showEmpty(state.message)
            is PlaylistsState.Content -> showTracks(state.favourites)
            is PlaylistsState.Loading -> showLoading()
        }
    }

    private fun showLoading() {
        recyclerView.visibility = View.GONE
        binding.placeholderImage.visibility = View.GONE
        binding.placeHolderText.visibility = View.GONE
    }

    private fun showEmpty(message: String) {
        recyclerView.visibility = View.GONE
        binding.placeholderImage.visibility = View.VISIBLE
        binding.placeHolderText.visibility = View.VISIBLE

        binding.placeHolderText.text = message
    }

    private fun showTracks(playlists: List<Playlist>) {
        recyclerView.visibility = View.VISIBLE
        binding.placeholderImage.visibility = View.GONE
        binding.placeHolderText.visibility = View.GONE

        playlistsAdapter.playlists.clear()
        playlistsAdapter.playlists.addAll(playlists)
        playlistsAdapter.notifyDataSetChanged()
    }

    companion object {
        fun newInstance(): PlaylistsFragment {
            return PlaylistsFragment()
        }

        private const val CLICK_DEBOUNCE_DELAY_MILLIS = 200L
    }

}