package com.practicum.playlistmaker.player.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentMediaBinding
import com.practicum.playlistmaker.library.domain.entity.Playlist
import com.practicum.playlistmaker.library.presentation.FavouritesViewModel
import com.practicum.playlistmaker.library.ui.PlaylistsAdapter
import com.practicum.playlistmaker.player.presentation.PlayerViewModel
import com.practicum.playlistmaker.search.domain.entity.Track
import com.practicum.playlistmaker.search.ui.SearchFragment
import com.practicum.playlistmaker.search.ui.TrackAdapter
import com.practicum.playlistmaker.utils.Debounce
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

@Suppress("DEPRECATION")

class PlayerFragment: Fragment() {



    private lateinit var binding: FragmentMediaBinding
    private lateinit var playButton: ImageButton
    private lateinit var timer: TextView
    private lateinit var recyclerView: RecyclerView

    private lateinit var onPlaylistClickDebounce: (Playlist) -> Unit

    private val playlistAdapter = PlaylistSmallAdapter {
        onPlaylistClickDebounce(it)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMediaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        playButton = binding.playButton
        timer = binding.timer
        recyclerView = binding.playlistsSmallRecyclerView
        recyclerView.adapter = playlistAdapter

        val track = requireArguments().getSerializable(ARGS_TRACK) as Track
        val viewModel by viewModel<PlayerViewModel> { parametersOf(track) }

        preparePlayer(track)

        viewModel.observePlayerState.observe(viewLifecycleOwner) {
            playButton.isEnabled = it.isPlayButtonEnabled
            timer.text = it.progress

            playButton.setImageResource(playButtonImage(it.isPlaying))
        }

        viewModel.observeIsFavourite.observe(viewLifecycleOwner) {isFavourite ->
            if (isFavourite) {
                binding.likeButton.setImageResource(R.drawable.like_button_active)
            } else {
                binding.likeButton.setImageResource(R.drawable.like_button)
            }
        }

        viewModel.observePlaylistsLiveData.observe(viewLifecycleOwner) {
            playlistAdapter.playlists.addAll(it)
            playlistAdapter.notifyDataSetChanged()
        }

        viewModel.observerToastMessage.observe(viewLifecycleOwner) {
            Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
        }

        binding.menuButton.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.likeButton.setOnClickListener {
            viewModel.onFavoriteClicked()
        }

        playButton.setOnClickListener {
            viewModel.playbackControl()
        }

        val bottomSheetContainer = binding.playlistsBottomSheet
        val overlay = binding.overlay

        val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetContainer).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
        }

        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {

            override fun onStateChanged(bottomSheet: View, newState: Int) {

                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        overlay.visibility = View.GONE
                    }
                    else -> {
                        overlay.visibility = View.VISIBLE
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        })

        binding.addCollectionButton.setOnClickListener{
            playlistAdapter.playlists.clear()
            viewModel.updatePlaylists()
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        binding.newPlaylistButton.setOnClickListener{
            findNavController().navigate(R.id.action_playerFragment_to_addNewPlaylistFragment)
        }

        onPlaylistClickDebounce = Debounce().debounce(
            CLICK_DEBOUNCE_DELAY_MILLIS,
            viewLifecycleOwner.lifecycleScope,
            false,
        ) {
            viewModel.addTrackToPlaylist(track, it)
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }
    }

    private fun playButtonImage(isPlaying: Boolean): Int {
        return if (isPlaying) {
            R.drawable.pause_button
        } else {
            R.drawable.play_button
        }
    }
    private fun preparePlayer(track: Track) {
        binding.trackName.text = track.trackName
        binding.trackAuthor.text = track.artistName
        binding.trackDuration.text = track.trackTime
        binding.trackAlbum.text = track.collectionName
        binding.trackYear.text = track.releaseDate.substring(0, 4)
        binding.trackGenre.text = track.primaryGenreName
        binding.trackCountry.text = track.country
        playButton.setImageResource(R.drawable.play_button)

        if (track.collectionName == "") {
            binding.albumGroup.visibility = View.GONE
        }

        Glide.with(this)
            .load(track.artworkUrl100)
            .placeholder(R.drawable.placeholder)
            .transform(
                CenterCrop(),
                RoundedCorners(8)
            )
            .into(binding.trackCover)
    }

    companion object {
        private const val ARGS_TRACK = "track"

        fun createArgs(track: Track): Bundle = bundleOf(ARGS_TRACK to track)

        private const val CLICK_DEBOUNCE_DELAY_MILLIS = 200L
    }
}

//class PlayerActivity : AppCompatActivity() {
//    private var binding: ActivityMediaBinding? = null
//    private lateinit var playButton: ImageButton
//    private lateinit var timer: TextView
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        binding = ActivityMediaBinding.inflate(layoutInflater)
//        setContentView(binding?.root)
//
//        playButton = findViewById(R.id.playButton)
//        timer = findViewById(R.id.timer)
//
//        intent?.let {
//            val track = intent.extras?.getSerializable(CURRENT_TRACK) as Track
//            val viewModel by viewModel<PlayerViewModel> { parametersOf(track) }
//
//            preparePlayer(track)
//
//            viewModel.observePlayerState.observe(this) {
//                playButton.isEnabled = it.isPlayButtonEnabled
//                timer.text = it.progress
//
//                playButton.setImageResource(playButtonImage(it.isPlaying))
//            }
//
//            viewModel.observeIsFavourite.observe(this) {isFavourite ->
//                if (isFavourite) {
//                    binding?.likeButton?.setImageResource(R.drawable.like_button_active)
//                } else {
//                    binding?.likeButton?.setImageResource(R.drawable.like_button)
//                }
//            }
//
//            binding?.menuButton?.setOnClickListener {
//                super.onBackPressed()
//            }
//
//            binding?.likeButton?.setOnClickListener {
//                viewModel.onFavoriteClicked()
//            }
//
//            playButton.setOnClickListener {
//                viewModel.playbackControl()
//            }
//
//            val bottomSheetContainer = findViewById<LinearLayout>(R.id.playlists_bottom_sheet)
//            val overlay = findViewById<View>(R.id.overlay)
//
//            val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetContainer).apply {
//                state = BottomSheetBehavior.STATE_HIDDEN
//            }
//
//            bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
//
//                override fun onStateChanged(bottomSheet: View, newState: Int) {
//
//                    when (newState) {
//                        BottomSheetBehavior.STATE_HIDDEN -> {
//                            overlay.visibility = View.GONE
//                        }
//                        else -> {
//                            overlay.visibility = View.VISIBLE
//                        }
//                    }
//                }
//
//                override fun onSlide(bottomSheet: View, slideOffset: Float) {}
//            })
//
//            binding?.addCollectionButton?.setOnClickListener{
//                bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
//            }
//
//            binding?.newPlaylistButton?.setOnClickListener{
//                val addPlaylistFragment = AddNewPlaylistFragment()
//                val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
//                transaction.replace(android.R.id.content, addPlaylistFragment)
//                transaction.addToBackStack(null)
//                transaction.commit()
//                bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
//            }
//        }
//    }
//    private fun playButtonImage(isPlaying: Boolean): Int {
//        return if (isPlaying) {
//             R.drawable.pause_button
//        } else {
//            R.drawable.play_button
//        }
//    }
//    private fun preparePlayer(track: Track) {
//        binding?.trackName?.text = track.trackName
//        binding?.trackAuthor?.text = track.artistName
//        binding?.trackDuration?.text = track.trackTime
//        binding?.trackAlbum?.text = track.collectionName
//        binding?.trackYear?.text = track.releaseDate.substring(0, 4)
//        binding?.trackGenre?.text = track.primaryGenreName
//        binding?.trackCountry?.text = track.country
//        playButton.setImageResource(R.drawable.play_button)
//
//        if (track.collectionName == "") {
//            binding?.albumGroup?.visibility = View.GONE
//        }
//
//        Glide.with(this)
//            .load(track.artworkUrl100)
//            .placeholder(R.drawable.placeholder)
//            .transform(
//                CenterCrop(),
//                RoundedCorners(8)
//            )
//            .into(binding?.trackCover!!)
//    }
//}