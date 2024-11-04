package com.example.quizapp.api

import com.example.quizapp.model.CategoryResponse
import com.example.quizapp.model.QuizResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface TriviaApiService {
    @GET("api_category.php") // Tento endpoint je pro načítání kategorií
    fun getCategories(): Call<CategoryResponse>

    @GET("api.php") // Endpoint pro načítání otázek
    fun getQuestions(
        @Query("amount") amount: Int = 10,
        @Query("category") category: String,
        @Query("difficulty") difficulty: String,
        @Query("type") type: String
    ): Call<QuizResponse>
}