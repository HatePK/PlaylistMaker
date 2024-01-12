package com.practicum.playlistmaker.library.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.dialog.MaterialDialogs
import com.google.gson.Gson
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentPlaylistUnitBinding
import com.practicum.playlistmaker.library.domain.entity.Playlist
import com.practicum.playlistmaker.library.presentation.AddNewPlaylistViewModel
import com.practicum.playlistmaker.library.presentation.FavouritesState
import com.practicum.playlistmaker.library.presentation.PlaylistUnitState
import com.practicum.playlistmaker.library.presentation.PlaylistUnitViewModel
import com.practicum.playlistmaker.player.ui.PlayerFragment
import com.practicum.playlistmaker.search.domain.entity.Track
import com.practicum.playlistmaker.search.ui.TrackAdapter
import com.practicum.playlistmaker.utils.Debounce
import com.practicum.playlistmaker.utils.NumbersEnding
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class PlaylistUnitFragment: Fragment() {

    private var binding: FragmentPlaylistUnitBinding? = null
    private var recyclerView: RecyclerView? = null
    private lateinit var onTrackClickDebounce: (Track) -> Unit
    private lateinit var onLongClickListener: (Track) -> AlertDialog
    private lateinit var playlist: Playlist

    private val viewModel by viewModel<PlaylistUnitViewModel> { parametersOf(playlist) }

    private val tracksList = ArrayList<Track>()

    private val trackAdapter = PlaylistTrackAdapter (
        {onTrackClickDebounce(it)},
        {onLongClickListener(it)}
    )


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPlaylistUnitBinding.inflate(inflater, container, false)
        return binding?.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        playlist = requireArguments().getSerializable(ARGS_PLAYLIST) as Playlist

        binding?.playlistName?.text = playlist.playlistName
        binding?.playlistDescription?.text = playlist.playlistDescription
        binding?.playlistSmallName?.text = playlist.playlistName

        recyclerView = binding?.tracksRecyclerView
        recyclerView?.adapter = trackAdapter

        onTrackClickDebounce = Debounce().debounce(
            CLICK_DEBOUNCE_DELAY_MILLIS,
            viewLifecycleOwner.lifecycleScope,
            false,
        ) {
            findNavController().navigate(R.id.action_playlistUnitFragment_to_playerFragment, PlayerFragment.createArgs(it))
        }

        viewModel.tracksDuration.observe(viewLifecycleOwner) {
            val ending = NumbersEnding().editSecCase("минут", it.toInt())
            binding?.playlistDuration?.text = "$it $ending"
        }

        viewModel.tracksAmount.observe(viewLifecycleOwner) {
            val ending = NumbersEnding().edit("трек", it)
            binding?.playlistAmountOfTracks?.text = "$it $ending"
            binding?.playlistSmallDescription?.text = "$it $ending"
        }

        viewModel.playlistsState.observe(viewLifecycleOwner) {
            render(it)
        }

        viewModel.playlistInfo.observe(viewLifecycleOwner) {
            binding?.playlistDescription?.text = it.playlistDescription
            binding?.playlistName?.text = it.playlistName
            binding?.playlistSmallName?.text = it.playlistName
            binding?.playlistDescription?.text = it.playlistDescription

            Glide.with(requireActivity())
                .load(playlist.playlistCoverUri)
                .placeholder(R.drawable.placeholder)
                .transform(
                    CenterCrop()
                )
                .into(binding?.trackCover!!)

            Glide.with(requireActivity())
                .load(playlist.playlistCoverUri)
                .placeholder(R.drawable.placeholder)
                .transform(
                    CenterCrop()
                )
                .into(binding?.playlistSmallCover!!)
        }

        val bottomSheetContainer = binding?.playlistsMenuBottomSheet
        val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetContainer!!)

        bottomSheetBehavior.apply {
            state = BottomSheetBehavior.STATE_HIDDEN
        }

        Glide.with(requireActivity())
            .load(playlist.playlistCoverUri)
            .placeholder(R.drawable.placeholder)
            .transform(
                CenterCrop()
            )
            .into(binding?.trackCover!!)

        Glide.with(requireActivity())
            .load(playlist.playlistCoverUri)
            .placeholder(R.drawable.placeholder)
            .transform(
                CenterCrop()
            )
            .into(binding?.playlistSmallCover!!)

        onLongClickListener = {track ->
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(requireActivity().getString(R.string.playlist_unit_ensure_about_deleting))
                .setNeutralButton("") { dialog, which -> }
                .setNegativeButton("Нет") { dialog, which -> }
                .setPositiveButton("Да") { dialog, which ->
                    viewModel.deleteTrack(track.trackId)
                }.show()
        }

        binding?.shareButton?.setOnClickListener {
            if (tracksList.isEmpty()) {
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle(requireActivity().getString(R.string.playlist_unit_no_tracks_for_sharing))
                    .setNeutralButton("") { dialog, which -> }
                    .setPositiveButton("Закрыть") { dialog, which -> }.show()
            } else {
                viewModel.sharePlaylist()
            }
        }

        val overlay = binding?.overlay

        binding?.actionMenuButton?.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {

            override fun onStateChanged(bottomSheet: View, newState: Int) {

                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        overlay?.visibility = View.GONE
                    }
                    else -> {
                        overlay?.visibility = View.VISIBLE
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        })

        binding?.actionMenuShareButton?.setOnClickListener {
            if (tracksList.isEmpty()) {
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle(requireActivity().getString(R.string.playlist_unit_no_tracks_for_sharing))
                    .setNeutralButton("") { dialog, which -> }
                    .setPositiveButton("Закрыть") { dialog, which -> }.show()
            } else {
                viewModel.sharePlaylist()
            }
        }

        binding?.actionMenuEditButton?.setOnClickListener {
            findNavController().navigate(R.id.action_playlistUnitFragment_to_editPlaylistFragment, EditPlaylistFragment.createArgs(playlist))
        }

        binding?.actionMenuDeleteButton?.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Хотите удалить плейлист «${playlist.playlistName}»?")
                .setNeutralButton("") { dialog, which -> }
                .setNegativeButton("Нет") { dialog, which -> }
                .setPositiveButton("Да") { dialog, which ->
                    viewModel.deletePlaylist()
                    findNavController().navigateUp()
                }.show()
        }

        binding?.menuButton?.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.updateAfterEdit()
    }

    private fun render(state: PlaylistUnitState) {
        when (state) {
            is PlaylistUnitState.Loading -> showLoading()
            is PlaylistUnitState.Empty -> showEmpty(state.message)
            is PlaylistUnitState.Content -> showTracks(state.tracks)
        }
    }

    private fun showLoading() {
        recyclerView?.visibility = View.GONE
        binding?.placeholder?.visibility = View.GONE
    }

    private fun showEmpty(message: String) {
        recyclerView?.visibility = View.GONE
        binding?.placeholder?.visibility = View.VISIBLE

        binding?.placeholder?.text = message
    }

    private fun showTracks(tracks: List<Track>) {
        recyclerView?.visibility = View.VISIBLE
        binding?.placeholder?.visibility = View.GONE

        tracksList.clear()
        tracksList.addAll(tracks)

        trackAdapter.tracks.clear()
        trackAdapter.tracks.addAll(tracks)
        trackAdapter.notifyDataSetChanged()
    }

    companion object {
        private const val ARGS_PLAYLIST = "playlist"

        fun createArgs(playlist: Playlist): Bundle = bundleOf(ARGS_PLAYLIST to playlist)

        private const val CLICK_DEBOUNCE_DELAY_MILLIS = 200L
    }
}