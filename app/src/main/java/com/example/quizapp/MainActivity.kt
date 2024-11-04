package com.example.quizapp
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.quizapp.viewmodel.QuizViewModel
import android.widget.Button

class MainActivity : AppCompatActivity() {

    private val quizViewModel: QuizViewModel by viewModels()
    private lateinit var categorySpinner: Spinner
    private lateinit var difficultySpinner: Spinner
    private lateinit var questionTypeSpinner: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        categorySpinner = findViewById(R.id.categorySpinner)
        difficultySpinner = findViewById(R.id.spinnerDifficulty)
        questionTypeSpinner = findViewById(R.id.spinnerQuestionType)


        val categorySpinner: Spinner = findViewById(R.id.categorySpinner)
        val difficultySpinner: Spinner = findViewById(R.id.spinnerDifficulty)
        val questionTypeSpinner: Spinner = findViewById(R.id.spinnerQuestionType)
        val startQuizButton: Button = findViewById(R.id.buttonStartQuiz)

        // Pozoruj změny v kategoriích a naplň Spinner
        quizViewModel.categories.observe(this, Observer { categories ->
            val categoryNames = categories.map { it.name }
            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categoryNames)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            categorySpinner.adapter = adapter
        })

        // Načti kategorie při spuštění
        quizViewModel.loadCategories()

        startQuizButton.setOnClickListener {
            val selectedCategoryName = categorySpinner.selectedItem.toString()
            val selectedCategoryId = quizViewModel.categoryMap[selectedCategoryName] ?: 9 // Defaultní hodnota, např. 9
            val selectedDifficulty = difficultySpinner.selectedItem.toString().lowercase()
            val selectedType = if (questionTypeSpinner.selectedItem.toString() == "Multiple Choice") {
                "multiple"
            } else {
                "boolean"
            }

            // Zavolání API metody pro otázky s ID kategorie
            quizViewModel.getQuizQuestions(selectedCategoryId.toString(), selectedDifficulty, selectedType)

            Log.d("MainActivity", "Requested questions with category ID: $selectedCategoryId, " +
                    "difficulty: $selectedDifficulty, type: $selectedType")
        }
    }
}