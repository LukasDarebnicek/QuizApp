package com.example.quizapp.api

import com.example.quizapp.model.CategoryResponse
import retrofit2.Call
import retrofit2.http.GET

interface TriviaApiService {
    @GET("api_category.php")
    fun getCategories(): Call<CategoryResponse>
}
