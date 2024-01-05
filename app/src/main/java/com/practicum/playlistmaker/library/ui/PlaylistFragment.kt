package com.practicum.playlistmaker.library.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentPlaylistsBinding
import com.practicum.playlistmaker.library.domain.entity.Playlist
import com.practicum.playlistmaker.library.presentation.FavouritesState
import com.practicum.playlistmaker.library.presentation.PlaylistsState
import com.practicum.playlistmaker.library.presentation.PlaylistsViewModel
import com.practicum.playlistmaker.search.domain.entity.Track
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistFragment: Fragment() {

    private val playlistsViewModel by viewModel<PlaylistsViewModel>()
    private lateinit var recyclerView: RecyclerView

    private lateinit var binding: FragmentPlaylistsBinding

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

        playlistsViewModel.observePlaylistsState().observe(viewLifecycleOwner) {
            render(it)
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

        recyclerView.adapter = PlaylistsAdapter(playlists)
    }

    companion object {
        fun newInstance(): PlaylistFragment {
            return PlaylistFragment()
        }
    }

}