package com.example.quizapp.model

data class QuizResponse(
    val response_code: Int,
    val results: List<Question> // Ujistěte se, že zde je správně definovaná vlastnost results
)

