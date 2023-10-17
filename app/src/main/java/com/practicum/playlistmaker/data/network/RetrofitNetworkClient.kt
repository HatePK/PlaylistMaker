package com.practicum.playlistmaker.data.network

import com.practicum.playlistmaker.data.NetworkClient
import com.practicum.playlistmaker.data.dto.Response
import com.practicum.playlistmaker.data.dto.TracksSearchRequest
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitNetworkClient: NetworkClient {
    private val apiBaseUrl = "https://itunes.apple.com"

    private val retrofit = Retrofit.Builder()
        .baseUrl(apiBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val itunesService = retrofit.create(AppleApi::class.java)


    override fun doRequest(dto: Any): Response {
        return if (dto is TracksSearchRequest) {
            val response = itunesService.search(dto.expression).execute()

            val body = response.body() ?: Response()
            body.apply { resultCode = response.code() }
        } else {
            Response().apply{ resultCode = 400 }
        }
    }
}