package com.example.quizapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.quizapp.model.Category
import com.example.quizapp.model.CategoryResponse
import com.example.quizapp.repository.TriviaRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class QuizViewModel : ViewModel() {

    private val repository = TriviaRepository()

    private val _categories = MutableLiveData<List<Category>>()
    val categories: LiveData<List<Category>> get() = _categories

    fun loadCategories() {
        repository.getCategories().enqueue(object : Callback<CategoryResponse> {
            override fun onResponse(call: Call<CategoryResponse>, response: Response<CategoryResponse>) {
                if (response.isSuccessful) {
                    _categories.value = response.body()?.trivia_categories
                }
            }

            override fun onFailure(call: Call<CategoryResponse>, t: Throwable) {
                // Ošetřete chyby podle potřeby
            }
        })
    }
}
