package com.example.quizapp.viewmodel
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



class QuizViewModel : ViewModel() {

    private val repository = TriviaRepository()

    private val _categories = MutableLiveData<List<Category>>()
    val categories: LiveData<List<Category>> get() = _categories

    // LiveData pro otázky
    private val _questions = MutableLiveData<List<Question>>()
    //val questions: LiveData<List<Question>> get() = _questions

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

    // Přidání metody getQuizQuestions
    fun getQuizQuestions(category: String, difficulty: String, type: String) {
        repository.getQuestions(category, difficulty, type).enqueue(object : Callback<QuizResponse> {
            override fun onResponse(call: Call<QuizResponse>, response: Response<QuizResponse>) {
                if (response.isSuccessful) {
                    _questions.value = response.body()?.results // Předpokládá, že QuestionResponse obsahuje seznam otázek
                }
            }

            override fun onFailure(call: Call<QuizResponse>, t: Throwable) {
                // Ošetřete chyby podle potřeby
            }
        })
    }
}
