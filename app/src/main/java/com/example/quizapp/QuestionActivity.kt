package com.example.quizapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.quizapp.viewmodel.QuizViewModel
import com.example.quizapp.model.Question

class QuestionActivity : AppCompatActivity() {

    private lateinit var questionTextView: TextView
    private lateinit var trueFalseContainer: LinearLayout
    private lateinit var multipleChoiceContainer: LinearLayout
    private lateinit var trueButton: Button
    private lateinit var falseButton: Button
    private lateinit var option1: CheckBox
    private lateinit var option2: CheckBox
    private lateinit var option3: CheckBox
    private lateinit var option4: CheckBox
    private lateinit var nextQuestionButton: Button

    private val quizViewModel: QuizViewModel by viewModels()
    private var questionList: List<Question> = listOf()
    private var currentQuestionIndex = 0

    // Načtení dat z Intentu
    private lateinit var selectedCategory: String
    private lateinit var selectedDifficulty: String
    private lateinit var selectedType: String
    private lateinit var questions: Array<String>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_question)

        // Inicializace UI prvků
        questionTextView = findViewById(R.id.questionTextView)
        trueFalseContainer = findViewById(R.id.trueFalseContainer)
        multipleChoiceContainer = findViewById(R.id.multipleChoiceContainer)
        trueButton = findViewById(R.id.trueButton)
        falseButton = findViewById(R.id.falseButton)
        option1 = findViewById(R.id.option1)
        option2 = findViewById(R.id.option2)
        option3 = findViewById(R.id.option3)
        option4 = findViewById(R.id.option4)
        nextQuestionButton = findViewById(R.id.nextQuestionButton)

        // Načtení dat z Intentu
        selectedCategory = intent.getStringExtra("category").toString()
        selectedDifficulty = intent.getStringExtra("difficulty").toString()
        selectedType = intent.getStringExtra("questionType").toString()
        questions = intent.getStringArrayExtra("questions") ?: arrayOf()

        // Zobrazit první otázku, pokud existují
        if (questions.isNotEmpty()) {
            questionTextView.text = questions[currentQuestionIndex]
            displayAnswers(selectedType)
        }

        // Nastavení akce pro další otázku
        nextQuestionButton.setOnClickListener {
            if (currentQuestionIndex < questions.size - 1) {
                currentQuestionIndex++
                questionTextView.text = questions[currentQuestionIndex]
                displayAnswers(selectedType)
            } else {
                Toast.makeText(this, "No more questions", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Funkce pro zobrazení odpovědí na základě typu otázky
    private fun displayAnswers(questionType: String) {
        val currentQuestion = questions[currentQuestionIndex]
        if (questionType == "boolean") {
            trueFalseContainer.visibility = View.VISIBLE
            multipleChoiceContainer.visibility = View.GONE
        } else if (questionType == "multiple") {
            trueFalseContainer.visibility = View.GONE
            multipleChoiceContainer.visibility = View.VISIBLE

            // Nastavte text odpovědí pro Multiple Choice
            option1.text = "Option 1"
            option2.text = "Option 2"
            option3.text = "Option 3"
            option4.text = "Option 4"
            //TODO doladit zobrazení otázek
        }
    }

}