package com.practicum.playlistmaker

import android.annotation.SuppressLint
import android.content.Context
import android.media.Image
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SearchActivity : AppCompatActivity() {
    private var countValue = ""

    private val tracks = ArrayList<Track>()
    private val apiBaseUrl = "https://itunes.apple.com"
    private val trackAdapter = TrackAdapter(tracks)

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

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        searchPlaceholder = findViewById(R.id.searchPlaceholder)
        searchPlaceholderMessage = findViewById(R.id.searchPlaceholderMessage)
        searchPlaceholderImage = findViewById(R.id.searchPlaceholderImage)
        recyclerView = findViewById(R.id.recyclerView)
        errorButton = findViewById(R.id.errorButton)
        val backButton = findViewById<Button>(R.id.back_button)
        val inputEditText = findViewById<EditText>(R.id.search_input)
        val clearButton = findViewById<Button>(R.id.search_input_clear_button)
        var searchInputForReload = ""

        recyclerView.adapter = trackAdapter

        fun makeSearch(text: String) {
            searchInputForReload = text
            itunesService.search(text).enqueue(object :Callback<TracksResponse> {
                override fun onResponse(call: Call<TracksResponse>, response: Response<TracksResponse>) {
                    if (response.isSuccessful) {
                        tracks.clear()
                        if (response.body()?.results?.isNotEmpty() == true) {
                            searchPlaceholder.visibility = View.GONE
                            recyclerView.visibility = View.VISIBLE
                            tracks.addAll(response.body()?.results!!)
                            trackAdapter.notifyDataSetChanged()
                        } else {
                            showPlaceholder(getString(R.string.nothing_found))
                        }
                    } else {
                        showPlaceholder(getString(R.string.something_went_wrong))
                    }
                }
                override fun onFailure(call: Call<TracksResponse>, t: Throwable) {
                    showPlaceholder(getString(R.string.something_went_wrong))
                }
            })
        }

        inputEditText.setOnEditorActionListener { _, actionId, _ ->
            makeSearch(inputEditText.text.toString())
            true
        }

        errorButton.setOnClickListener{
            inputEditText.setText(searchInputForReload)
            makeSearch(searchInputForReload)
        }

        if (savedInstanceState != null) {
            countValue = savedInstanceState.getString(INPUT_VALUE,"")
            inputEditText.setText(countValue)
        }

        backButton.setOnClickListener {
            super.onBackPressed()
        }

        clearButton.setOnClickListener {
            inputEditText.setText("")
            tracks.clear()
            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(inputEditText.windowToken, 0)
        }

        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // empty
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                inputEditText.requestFocus()
                clearButton.visibility = clearButtonVisibility(s)
                countValue = inputEditText.text.toString()
            }

            override fun afterTextChanged(s: Editable?) {
                // empty
            }
        }
        inputEditText.addTextChangedListener(simpleTextWatcher)

    }
    private fun showPlaceholder(text: String) {
            tracks.clear()
            trackAdapter.notifyDataSetChanged()
            recyclerView.visibility = View.GONE
            searchPlaceholder.visibility = View.VISIBLE
            searchPlaceholderMessage.text = text

            if (text == getString(R.string.nothing_found)) {
                searchPlaceholderImage.setImageResource(R.drawable.empty_result)
                errorButton.visibility = View.GONE
            } else if (text == getString(R.string.something_went_wrong)) {
                searchPlaceholderImage.setImageResource(R.drawable.no_internet)
                errorButton.visibility = View.VISIBLE
            }
    }

    private fun clearButtonVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    companion object {
        private const val INPUT_VALUE = "INPUT_VALUE"
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(INPUT_VALUE,countValue)
    }
}