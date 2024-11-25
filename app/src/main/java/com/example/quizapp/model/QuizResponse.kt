package com.example.quizapp.model

import com.google.gson.annotations.SerializedName

data class QuizResponse(
    @SerializedName("response_code") val responseCode: Int, // Přidejte tuto řádku
    @SerializedName("results") val results: List<Question>
)