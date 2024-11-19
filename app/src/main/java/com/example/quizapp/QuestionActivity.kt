package com.example.quizapp

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.quizapp.model.Question

class QuestionActivity : AppCompatActivity() {

    private lateinit var questionTextView: TextView
    private lateinit var trueFalseContainer: LinearLayout
    private lateinit var multipleChoiceContainer: LinearLayout
    private lateinit var trueButton: Button
    private lateinit var falseButton: Button
    private lateinit var option1: RadioButton
    private lateinit var option2: RadioButton
    private lateinit var option3: RadioButton
    private lateinit var option4: RadioButton
    private lateinit var actionButton: Button
    private lateinit var scoreTextView: TextView
    private lateinit var returnButton: Button
    private lateinit var feedbackTextView: TextView
    private var questionList: List<Question> = listOf()
    private var currentQuestionIndex = 0

    private lateinit var selectedCategory: String
    private lateinit var selectedDifficulty: String
    private lateinit var selectedType: String
    private lateinit var questions: Array<Question>

    private var score = 0  // Počáteční skóre

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
        actionButton = findViewById(R.id.actionButton)
        scoreTextView = findViewById(R.id.scoreTextView)
        returnButton = findViewById(R.id.returnButton)
        scoreTextView = findViewById(R.id.scoreTextView)

        // Inicializace TextView pro feedback (slovní hodnocení)
        feedbackTextView = TextView(this)
        feedbackTextView.textSize = 18f
        feedbackTextView.setTextColor(Color.BLACK)

        // Přidání feedback TextView do layoutu
        val layout = findViewById<LinearLayout>(R.id.scoreLayout)
        layout.addView(feedbackTextView)

        // Načtení dat z Intentu
        selectedCategory = intent.getStringExtra("category").toString()
        selectedDifficulty = intent.getStringExtra("difficulty").toString()
        selectedType = intent.getStringExtra("questionType").toString()
        questions = intent.getParcelableArrayExtra("questions")?.map { it as Question }?.toTypedArray() ?: arrayOf()

        // Zobrazit první otázku, pokud existují
        if (questions.isNotEmpty()) {
            questionTextView.text = questions[currentQuestionIndex].questionText
            displayAnswers(selectedType)
        }

        actionButton.setOnClickListener {
            if (actionButton.text == "Check Answer") {
                val selectedOption = when {
                    option1.isChecked -> option1.text.toString()
                    option2.isChecked -> option2.text.toString()
                    option3.isChecked -> option3.text.toString()
                    option4.isChecked -> option4.text.toString()
                    else -> null // No option selected
                }

                checkAnswer(selectedOption)
                highlightAnswers(selectedOption ?: "")

                // Změň text tlačítka na "Next Question"
                actionButton.text = "Next Question"
            } else if (actionButton.text == "Next Question") {
                if (currentQuestionIndex < questions.size - 1) {
                    currentQuestionIndex++
                    questionTextView.text = questions[currentQuestionIndex].questionText
                    displayAnswers(selectedType)

                    // Reset colors for the next question
                    resetAnswerColors()

                    // Změň text tlačítka zpět na "Check Answer"
                    actionButton.text = "Check Answer"
                } else {
                    // Konec kvízu, zobrazení skóre
                    showFinalScore()
                }
            }
        }

        // Tlačítko pro návrat na hlavní obrazovku
        returnButton.setOnClickListener {
            // Můžete změnit tuto logiku pro návrat na hlavní aktivitu
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish() // Volitelné: Zavírá aktuální aktivitu
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
            val options = currentQuestion.options.shuffled() // Shuffle the options for randomness
            option1.text = options[0]
            option2.text = options[1]
            option3.text = options[2]
            option4.text = options[3]

            // Clear previous selections
            option1.isChecked = false
            option2.isChecked = false
            option3.isChecked = false
            option4.isChecked = false
        }
    }

    private fun highlightAnswers(selectedOption: String) {
        val correctAnswer = questions[currentQuestionIndex].correctAnswer

        // Reset the colors of options
        resetAnswerColors()

        // Highlight the selected option
        when (selectedOption) {
            option1.text.toString() -> option1.setTextColor(if (selectedOption == correctAnswer) Color.GREEN else Color.RED)
            option2.text.toString() -> option2.setTextColor(if (selectedOption == correctAnswer) Color.GREEN else Color.RED)
            option3.text.toString() -> option3.setTextColor(if (selectedOption == correctAnswer) Color.GREEN else Color.RED)
            option4.text.toString() -> option4.setTextColor(if (selectedOption == correctAnswer) Color.GREEN else Color.RED)
        }

        // Highlight the correct answer if the selected answer is incorrect
        if (selectedOption != correctAnswer) {
            when (correctAnswer) {
                option1.text.toString() -> option1.setTextColor(Color.GREEN)
                option2.text.toString() -> option2.setTextColor(Color.GREEN)
                option3.text.toString() -> option3.setTextColor(Color.GREEN)
                option4.text.toString() -> option4.setTextColor(Color.GREEN)
            }
        }
    }

    private fun resetAnswerColors() {
        // Reset the text color of all options to black
        option1.setTextColor(Color.BLACK)
        option2.setTextColor(Color.BLACK)
        option3.setTextColor(Color.BLACK)
        option4.setTextColor(Color.BLACK)
    }

    private fun checkAnswer(selectedOption: String?) {
        val correctAnswer = questions[currentQuestionIndex].correctAnswer
        if (selectedOption.isNullOrEmpty()) {
            // Pokud uživatel nevybral žádnou odpověď
            Toast.makeText(this, "No answer selected! The correct answer is: $correctAnswer", Toast.LENGTH_LONG).show()
        } else if (selectedOption == correctAnswer) {
            score++
            Toast.makeText(this, "Correct!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Incorrect! The correct answer was: $correctAnswer", Toast.LENGTH_SHORT).show()
        }
    }

    // Funkce pro zobrazení skóre po skončení kvízu
    // Funkce pro zobrazení skóre po skončení kvízu
    private fun showFinalScore() {
        //TODO dodělat tam nějaké vtipné hodnocení
        AlertDialog.Builder(this)
            .setTitle("Konec kvízu")
            .setMessage("Vaše skóre je: $score/${questions.size}")
            .setPositiveButton("OK") { _, _ ->
                // Návrat na hlavní obrazovku
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
            .show()
    }
}

