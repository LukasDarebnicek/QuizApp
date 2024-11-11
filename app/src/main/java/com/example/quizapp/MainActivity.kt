package com.example.quizapp

import android.content.Intent
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

        // Inicializace Spinnerů a tlačítka
        categorySpinner = findViewById(R.id.categorySpinner)
        difficultySpinner = findViewById(R.id.spinnerDifficulty)
        questionTypeSpinner = findViewById(R.id.spinnerQuestionType)
        val startQuizButton: Button = findViewById(R.id.buttonStartQuiz)

        // Pozorování na změny v kategoriích a jejich přidání do Spinneru
        quizViewModel.categories.observe(this, Observer { categories ->
            val categoryNames = categories.map { it.name }
            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categoryNames)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            categorySpinner.adapter = adapter
        })

        // Načtení kategorií při spuštění aplikace
        quizViewModel.loadCategories()

        startQuizButton.setOnClickListener {
            // Získání názvu vybrané kategorie a její ID
            val selectedCategoryName = categorySpinner.selectedItem.toString()
            val selectedCategoryId = quizViewModel.categories.value?.find { it.name == selectedCategoryName }?.id ?: 9

            // Získání obtížnosti a typu otázek
            val selectedDifficulty = difficultySpinner.selectedItem.toString().lowercase()
            val selectedType = if (questionTypeSpinner.selectedItem.toString() == "Multiple Choice") {
                "multiple"
            } else {
                "boolean"
            }

            // Zavolání API pro načtení otázek
            quizViewModel.getQuizQuestions(selectedCategoryId.toString(), selectedDifficulty, selectedType)

            // Logování požadavku
            Log.d("MainActivity", "Requested questions with category ID: $selectedCategoryId, " +
                    "difficulty: $selectedDifficulty, type: $selectedType")

            // Přechod do QuestionActivity a předání dat
            val intent = Intent(this, QuestionActivity::class.java).apply {
                putExtra("category", selectedCategoryId)
                putExtra("difficulty", selectedDifficulty)
                putExtra("questionType", selectedType)
            }

            // Pokud máte otázky, předáte je také
            quizViewModel.questions.observe(this, Observer { questions ->
                val questionList = questions.map { it.questionText }.toTypedArray()
                intent.putExtra("questions", questionList)
                startActivity(intent)
            })
        }
    }
}
