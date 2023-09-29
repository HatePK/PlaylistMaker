package com.practicum.playlistmaker

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
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
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

const val SAVED_TRACKS = "saved_tracks"
const val CURRENT_TRACK = "track"
class SearchActivity : AppCompatActivity() {
    private var countValue = ""

    private val tracks = ArrayList<Track>()
    private val savedTracks = ArrayList<Track>()
    private val apiBaseUrl = "https://itunes.apple.com"
    private val trackAdapter = TrackAdapter(tracks, this)
    private val savedTrackAdapter = TrackAdapter(savedTracks, this)
    private var searchInputForReload = ""

    private val retrofit = Retrofit.Builder()
        .baseUrl(apiBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val itunesService = retrofit.create(AppleApi::class.java)

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


    private var isClickAllowed = true
    private val handler = Handler(Looper.getMainLooper())

    companion object {
        private const val INPUT_VALUE = "INPUT_VALUE"
        private const val CLICK_DEBOUNCE_DELAY = 1000L
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }

    private val searchRunnable = Runnable {
        makeSearch(inputEditText.text.toString())
    }

    private fun searchDebounce() {
        searchPlaceholder.visibility = View.GONE
        handler.removeCallbacks(searchRunnable)
        handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
    }

    @SuppressLint("MissingInflatedId", "NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        val sharedPreferences = getSharedPreferences(SHARED_PREFERENCES, MODE_PRIVATE)

        setViews()
        setAdapter()
        setListeners()
        saveState(savedInstanceState)
        searchHistory()

        val listener =
            SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
                if (key == SAVED_TRACKS) {
                    val sharedPrefsTracks = SearchHistory(this).returnSavedTracks()
                    savedTracks.clear()
                    savedTracks.addAll(sharedPrefsTracks)
                    savedTrackAdapter.notifyDataSetChanged()
                }
            }

        sharedPreferences.registerOnSharedPreferenceChangeListener(listener)
    }
    fun addTrackToHistory(track: Track) {
        val searchHistory = SearchHistory(this)
        searchHistory.onListElementClick(track)
    }

    fun goToMedia(track: Track) {
        if (clickDebounce()) {
            val mediaIntent = Intent(this, MediaActivity::class.java)
            mediaIntent.putExtra(CURRENT_TRACK, track);
            startActivity(mediaIntent)
        }
    }

    private fun setAdapter() {
        recyclerView.adapter = trackAdapter
        recyclerViewSavedTracks.adapter = savedTrackAdapter
    }
    private fun makeSearch(text: String) {
        recyclerView.visibility = View.GONE
        progressBar.visibility = View.VISIBLE

        searchInputForReload = text
        itunesService.search(text).enqueue(object :Callback<TracksResponse> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(call: Call<TracksResponse>, response: Response<TracksResponse>) {
                if (response.isSuccessful) {
                    tracks.clear()
                    progressBar.visibility = View.GONE
                    if (response.body()?.results?.isNotEmpty() == true) {
                        recyclerView.visibility = View.VISIBLE
                        tracks.addAll(response.body()?.results!!)
                        trackAdapter.notifyDataSetChanged()
                    } else {
                        showPlaceholder(SearchStatus.NOTHING_FOUND)
                    }
                } else {
                    showPlaceholder(SearchStatus.NO_INTERNET)
                }
            }
            override fun onFailure(call: Call<TracksResponse>, t: Throwable) {
                progressBar.visibility = View.GONE
                showPlaceholder(SearchStatus.NO_INTERNET)
            }
        })
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

    private fun searchHistory() {
        inputEditText.setOnFocusChangeListener { _, hasFocus ->
            val sharedPrefsTracks = SearchHistory(this).returnSavedTracks()
            savedTracks.addAll(sharedPrefsTracks)
            savedTrackAdapter.notifyDataSetChanged()

            recyclerView.visibility = View.GONE
            searchPlaceholder.visibility = View.GONE
            searchHistory.visibility = if (hasFocus && inputEditText.text.isEmpty() && savedTracks.size > 0) View.VISIBLE else View.GONE
        }
    }

    private fun setListeners() {
//        inputEditText.setOnEditorActionListener { _, actionId, _ ->
//            makeSearch(inputEditText.text.toString())
//            true
//        }

        errorButton.setOnClickListener{
            inputEditText.setText(searchInputForReload)
            makeSearch(searchInputForReload)
        }

        backButton.setOnClickListener {
            super.onBackPressed()
        }

        clearButton.setOnClickListener {
            recyclerView.visibility = View.GONE
            searchPlaceholder.visibility = View.GONE
            inputEditText.setText("")
            tracks.clear()
            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(inputEditText.windowToken, 0)
        }

        clearHistoryButton.setOnClickListener {
            SearchHistory(this).clearSavedTracks()
            savedTracks.clear()
            searchHistory.visibility = View.GONE
        }

        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // empty
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchDebounce()
                inputEditText.requestFocus()
                clearButton.visibility = clearButtonVisibility(s)
                countValue = inputEditText.text.toString()
                searchHistory.visibility = if (inputEditText.hasFocus() && s?.isEmpty() == true  && savedTracks.size > 0) View.VISIBLE else View.GONE
                if (inputEditText.hasFocus() && s?.isEmpty() == true && savedTracks.size > 0) {
                    handler.removeCallbacks(searchRunnable)
                    searchPlaceholder.visibility = View.GONE
                    recyclerView.visibility = View.GONE
                    searchHistory.visibility = View.VISIBLE
                }
            }

            override fun afterTextChanged(s: Editable?) {
                // empty
            }
        }

        inputEditText.addTextChangedListener(simpleTextWatcher)
    }
    private fun showPlaceholder(status: SearchStatus) {
            tracks.clear()
            trackAdapter.notifyDataSetChanged()
            recyclerView.visibility = View.GONE
            searchPlaceholder.visibility = View.VISIBLE

            if (status == SearchStatus.NOTHING_FOUND) {
                searchPlaceholderImage.setImageResource(R.drawable.empty_result)
                searchPlaceholderMessage.text = getString(R.string.nothing_found)
                errorButton.visibility = View.GONE
            } else if (status == SearchStatus.NO_INTERNET) {
                searchPlaceholderImage.setImageResource(R.drawable.no_internet)
                searchPlaceholderMessage.text = getString(R.string.something_went_wrong)
                errorButton.visibility = View.VISIBLE
            }
    }

    private fun saveState(savedInstanceState: Bundle?) {
        if (savedInstanceState != null) {
            countValue = savedInstanceState.getString(INPUT_VALUE,"")
            inputEditText.setText(countValue)

            val sharedPrefsTracks = SearchHistory(this).returnSavedTracks()
            savedTracks.clear()
            savedTracks.addAll(sharedPrefsTracks)
            savedTrackAdapter.notifyDataSetChanged()
        }
    }
    private fun clearButtonVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(INPUT_VALUE,countValue)
    }

    private fun clickDebounce() : Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }
}