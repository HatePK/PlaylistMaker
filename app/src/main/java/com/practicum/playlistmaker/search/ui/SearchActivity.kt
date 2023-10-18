package com.practicum.playlistmaker.search.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.player.ui.MediaActivity
import com.practicum.playlistmaker.search.domain.entity.Track
import com.practicum.playlistmaker.search.presentation.SearchState
import com.practicum.playlistmaker.search.presentation.SearchViewModel

const val CURRENT_TRACK = "track"

class SearchActivity : AppCompatActivity() {

    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }

    private var countValue = ""
    private var searchInputForReload = ""

    private lateinit var searchPlaceholder: LinearLayout
    private lateinit var searchPlaceholderMessage: TextView
    private lateinit var searchPlaceholderImage: ImageView
    private lateinit var errorButton: Button
    private lateinit var recyclerView: RecyclerView
    private lateinit var recyclerViewSavedTracks: RecyclerView
    private lateinit var backButton: Button
    private lateinit var inputEditText: EditText
    private lateinit var clearButton: Button
    private lateinit var searchHistory: LinearLayout
    private lateinit var clearHistoryButton: Button
    private lateinit var progressBar: ProgressBar

    private val trackAdapter = TrackAdapter {
        if (clickDebounce()) {
            val mediaIntent = Intent(this, MediaActivity::class.java)
            mediaIntent.putExtra(CURRENT_TRACK, it);
            startActivity(mediaIntent)
            viewModel.addTrackToHistory(it)}
    }

    private val savedTrackAdapter = TrackAdapter {
        if (clickDebounce()) {
            val mediaIntent = Intent(this, MediaActivity::class.java)
            mediaIntent.putExtra(CURRENT_TRACK, it);
            startActivity(mediaIntent)
        }
    }

    private var isClickAllowed = true
    private val handler = Handler(Looper.getMainLooper())
    private var textWatcher: TextWatcher? = null

    private lateinit var viewModel: SearchViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        setViews()
        setAdapter()
        setListeners()
        searchHistory()

        viewModel = ViewModelProvider(this, SearchViewModel.getViewModelFactory())[SearchViewModel::class.java]

        viewModel.observeState().observe(this) {
            render(it)
        }

        viewModel.getSavedTracksLiveData().observe(this) {
            savedTrackAdapter.tracks.clear()
            savedTrackAdapter.tracks.addAll(it)
            savedTrackAdapter.notifyDataSetChanged()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        textWatcher?.let { inputEditText.removeTextChangedListener(it) }
    }

    private fun clickDebounce() : Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

    private fun setViews() {
        searchPlaceholder = findViewById(R.id.searchPlaceholder)
        searchPlaceholderMessage = findViewById(R.id.searchPlaceholderMessage)
        searchPlaceholderImage = findViewById(R.id.searchPlaceholderImage)
        recyclerView = findViewById(R.id.recyclerView)
        recyclerViewSavedTracks = findViewById(R.id.recyclerViewSaved)
        errorButton = findViewById(R.id.errorButton)
        backButton = findViewById(R.id.back_button)
        inputEditText = findViewById(R.id.search_input)
        clearButton = findViewById(R.id.search_input_clear_button)
        searchHistory = findViewById(R.id.historyView)
        clearHistoryButton = findViewById(R.id.clearHistoryButton)
        progressBar = findViewById(R.id.progressBar)
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

        backButton.setOnClickListener {
            onBackPressed()
        }

        clearButton.setOnClickListener {
            inputEditText.setText("")

            viewModel.clearTracks()

            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
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
                    viewModel.showHistoryTracks()
                    viewModel.clearTracks()
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
}
