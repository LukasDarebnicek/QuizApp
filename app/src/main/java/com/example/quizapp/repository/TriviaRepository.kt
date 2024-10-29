package com.example.quizapp.repository

import com.example.quizapp.api.TriviaApiService
import com.example.quizapp.model.CategoryResponse
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class TriviaRepository {

    private val api: TriviaApiService

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://opentdb.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        api = retrofit.create(TriviaApiService::class.java)
    }

    fun getCategories(): Call<CategoryResponse> {
        return api.getCategories()
    }
}
