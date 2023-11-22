package com.practicum.playlistmaker.search.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentSearchBinding
import com.practicum.playlistmaker.player.ui.PlayerActivity
import com.practicum.playlistmaker.search.domain.entity.Track
import com.practicum.playlistmaker.search.presentation.SearchState
import com.practicum.playlistmaker.search.presentation.SearchViewModel
import com.practicum.playlistmaker.utils.Debounce
import org.koin.androidx.viewmodel.ext.android.viewModel

const val CURRENT_TRACK = "track"

class SearchFragment:Fragment() {

    private var countValue = ""
    private var searchInputForReload = ""

    private lateinit var searchPlaceholder: LinearLayout
    private lateinit var searchPlaceholderMessage: TextView
    private lateinit var searchPlaceholderImage: ImageView
    private lateinit var errorButton: Button
    private lateinit var recyclerView: RecyclerView
    private lateinit var recyclerViewSavedTracks: RecyclerView
    private lateinit var inputEditText: EditText
    private lateinit var clearButton: Button
    private lateinit var searchHistory: LinearLayout
    private lateinit var clearHistoryButton: Button
    private lateinit var progressBar: ProgressBar

    private val trackAdapter = TrackAdapter {
        onTrackClickDebounce(it)
    }

    private val savedTrackAdapter = TrackAdapter {
        onTrackClickDebounce(it)
    }

    private var textWatcher: TextWatcher? = null

    private val viewModel by viewModel<SearchViewModel>()

    private lateinit var binding: FragmentSearchBinding

    private lateinit var onTrackClickDebounce: (Track) -> Unit

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        onTrackClickDebounce = Debounce().debounce(
            CLICK_DEBOUNCE_DELAY_MILLIS,
            viewLifecycleOwner.lifecycleScope,
            false,
        ) {
            val mediaIntent = Intent(requireContext(), PlayerActivity::class.java)
            mediaIntent.putExtra(CURRENT_TRACK, it);
            startActivity(mediaIntent)
            viewModel.addTrackToHistory(it)
        }

        setViews()
        setAdapter()
        setListeners()
        searchHistory()

        viewModel.observeState().observe(viewLifecycleOwner) {
            render(it)
        }

        viewModel.getSavedTracksLiveData().observe(viewLifecycleOwner) {
            savedTrackAdapter.tracks.clear()
            savedTrackAdapter.tracks.addAll(it)
            savedTrackAdapter.notifyDataSetChanged()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        textWatcher?.let { inputEditText.removeTextChangedListener(it) }
    }
    private fun setViews() {
        searchPlaceholder = binding.searchPlaceholder
        searchPlaceholderMessage = binding.searchPlaceholderMessage
        searchPlaceholderImage = binding.searchPlaceholderImage
        recyclerView = binding.recyclerView
        recyclerViewSavedTracks = binding.recyclerViewSaved
        errorButton = binding.errorButton
        inputEditText = binding.searchInput
        clearButton = binding.searchInputClearButton
        searchHistory = binding.historyView
        clearHistoryButton = binding.clearHistoryButton
        progressBar = binding.progressBar
    }

    private fun setAdapter() {
        recyclerView.adapter = trackAdapter
        recyclerViewSavedTracks.adapter = savedTrackAdapter
    }

    private fun setListeners() {
        errorButton.setOnClickListener{
            inputEditText.setText(searchInputForReload)
            viewModel.makeSearch(searchInputForReload)
        }

        clearButton.setOnClickListener {
            inputEditText.setText("")

            viewModel.clearTracks()

            val inputMethodManager = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(inputEditText.windowToken, 0)
        }

        clearHistoryButton.setOnClickListener {
            viewModel.clearHistory()
            searchHistory.visibility = View.GONE
        }

        textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // empty
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearButton.visibility = clearButtonVisibility(s)

                if (s.isNullOrEmpty()) {
                    viewModel.clearTracks()
                    viewModel.showHistoryTracks()
                } else {
                    searchPlaceholder.visibility = View.GONE
                    searchHistory.visibility = View.GONE
                    recyclerView.visibility = View.VISIBLE

                    viewModel.searchDebounce(changedText = s?.toString() ?: "")
                    searchInputForReload = s?.toString() ?: ""
                    inputEditText.requestFocus()
                    countValue = inputEditText.text.toString()
                }
            }

            override fun afterTextChanged(s: Editable?) {
                // empty
            }
        }

        textWatcher?.let { inputEditText.addTextChangedListener(it) }
    }

    private fun searchHistory() {
        inputEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && inputEditText.text.isEmpty()){
                viewModel.showHistoryTracks()
            }
        }
    }

    private fun clearButtonVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    private fun showLoading() {
        searchPlaceholder.visibility = View.GONE
        searchHistory.visibility = View.GONE
        recyclerView.visibility = View.GONE
        errorButton.visibility = View.GONE

        progressBar.visibility = View.VISIBLE
    }

    private fun showError(errorMessage: String) {
        searchHistory.visibility = View.GONE
        recyclerView.visibility = View.GONE
        progressBar.visibility = View.GONE

        errorButton.visibility = View.VISIBLE
        searchPlaceholder.visibility = View.VISIBLE

        searchPlaceholderImage.setImageResource(R.drawable.no_internet)
        searchPlaceholderMessage.text = errorMessage
    }

    private fun showEmpty(message: String) {
        searchHistory.visibility = View.GONE
        recyclerView.visibility = View.GONE
        progressBar.visibility = View.GONE
        errorButton.visibility = View.GONE

        searchPlaceholder.visibility = View.VISIBLE

        searchPlaceholderImage.setImageResource(R.drawable.empty_result)
        searchPlaceholderMessage.text = message
    }

    private fun showSearchContent(tracks: ArrayList<Track>) {
        searchPlaceholder.visibility = View.GONE
        searchHistory.visibility = View.GONE
        progressBar.visibility = View.GONE
        errorButton.visibility = View.GONE

        recyclerView.visibility = View.VISIBLE

        trackAdapter.tracks.clear()
        trackAdapter.tracks.addAll(tracks)
        trackAdapter.notifyDataSetChanged()
    }

    private fun showSavedContent(tracks: ArrayList<Track>) {
        searchPlaceholder.visibility = View.GONE
        progressBar.visibility = View.GONE
        recyclerView.visibility = View.GONE
        errorButton.visibility = View.GONE

        searchHistory.visibility = View.VISIBLE

        savedTrackAdapter.tracks.clear()
        savedTrackAdapter.tracks.addAll(tracks)
        savedTrackAdapter.notifyDataSetChanged()
    }

    private fun render(state: SearchState) {
        when (state) {
            is SearchState.Loading -> showLoading()
            is SearchState.SearchContent -> showSearchContent(state.tracks)
            is SearchState.SavedContent -> showSavedContent(state.tracks)
            is SearchState.Error -> showError(state.errorMessage)
            is SearchState.Empty -> showEmpty(state.message)
        }
    }

    companion object {
        private const val CLICK_DEBOUNCE_DELAY_MILLIS = 200L
    }
}