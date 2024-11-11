package com.example.quizapp.viewmodel
import android.util.Log
import com.example.quizapp.model.Question
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.quizapp.model.Category
import com.example.quizapp.model.CategoryResponse
import com.example.quizapp.model.QuizResponse
import com.example.quizapp.repository.TriviaRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.google.gson.Gson



class QuizViewModel : ViewModel() {

    private val repository = TriviaRepository()

    private val _categories = MutableLiveData<List<Category>>()
    val categories: LiveData<List<Category>> get() = _categories

    // LiveData pro otázky
    private val _questions = MutableLiveData<List<Question>>()
    val questions: LiveData<List<Question>> get() = _questions

    private val _categoryMap = mutableMapOf<String, Int>()
    val categoryMap: Map<String, Int> get() = _categoryMap

    fun loadCategories() {
        repository.getCategories().enqueue(object : Callback<CategoryResponse> {
            override fun onResponse(call: Call<CategoryResponse>, response: Response<CategoryResponse>) {
                if (response.isSuccessful) {
                    response.body()?.trivia_categories?.let { categories ->
                        _categories.value = categories
                        // Naplníme mapu názvů a ID
                        _categoryMap.putAll(categories.associate { it.name to it.id })
                    }
                }
            }

            override fun onFailure(call: Call<CategoryResponse>, t: Throwable) {
                Log.e("QuizViewModel", "Failed to load categories: ${t.message}")
            }
        })
    }

    // Přidání metody getQuizQuestions
    fun getQuizQuestions(category: String, difficulty: String, type: String) {
        repository.getQuestions(category, difficulty, type).enqueue(object : Callback<QuizResponse> {
            override fun onResponse(call: Call<QuizResponse>, response: Response<QuizResponse>) {
                if (response.isSuccessful) {
                    _questions.value = response.body()?.results

                    // Použití Gson k převodu odpovědi na JSON string
                    val gson = Gson()
                    val jsonResponse = gson.toJson(response.body())
                    Log.d("QuizViewModel", "JSON Response: $jsonResponse") // Zalogování JSON odpovědi
                } else {
                    Log.e("QuizViewModel", "Response was not successful: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<QuizResponse>, t: Throwable) {
                Log.e("QuizViewModel", "Failed to get questions: ${t.message}")
            }
        })
    }

}