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
import android.text.Html



class QuizViewModel : ViewModel() {

    private val repository = TriviaRepository()

    private val _categories = MutableLiveData<List<Category>>()
    val categories: LiveData<List<Category>> get() = _categories

    // LiveData pro otázky
    private val _questions = MutableLiveData<List<Question>>()
    val questions: LiveData<List<Question>> get() = _questions

    private val _categoryMap = mutableMapOf<String, Int>()
    val categoryMap: Map<String, Int> get() = _categoryMap

    // LiveData pro chyby
    private val _apiError = MutableLiveData<Int>()
    val apiError: LiveData<Int> get() = _apiError

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
                    val resultCode = response.body()?.responseCode ?: 0
                    if (resultCode == 0) {
                        // Dekódování HTML entit ve výsledcích
                        val decodedQuestions = response.body()?.results?.map { question ->
                            question.copy(
                                questionText = decodeHtmlEntities(question.questionText),
                                correctAnswer = decodeHtmlEntities(question.correctAnswer),
                                incorrectAnswers = question.incorrectAnswers.map { decodeHtmlEntities(it) }
                            )
                        }
                        _questions.value = decodedQuestions

                        // Použití Gson k převodu odpovědi na JSON string
                        val gson = Gson()
                        val jsonResponse = gson.toJson(response.body())
                        //Log.d("QuizViewModel", "JSON Response: $jsonResponse") // Zalogování JSON odpovědi

                        _apiError.value = 0 // Bez chyby
                    } else {
                        //Log.e("QuizViewModel", "API Error Code: $resultCode")
                        _apiError.value = resultCode // Nastavení chybového kódu
                    }
                } else {
                    //Log.e("QuizViewModel", "Response was not successful: ${response.errorBody()?.string()}")
                    _apiError.value = 5 // Obecná chyba, například rate limit
                }
            }

            override fun onFailure(call: Call<QuizResponse>, t: Throwable) {
                //Log.e("QuizViewModel", "Failed to get questions: ${t.message}")
                _apiError.value = 5 // Nastavení chyby při selhání volání
            }
        })
    }

    private fun decodeHtmlEntities(text: String): String {
        return Html.fromHtml(text, Html.FROM_HTML_MODE_LEGACY).toString()
    }
}
