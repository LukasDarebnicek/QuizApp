package com.example.quizapp

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.quizapp.model.Question
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class QuestionActivity : AppCompatActivity() {

    private lateinit var questionTextView: TextView
    private lateinit var trueFalseContainer: LinearLayout
    private lateinit var multipleChoiceContainer: LinearLayout
    private lateinit var radioTrue: RadioButton
    private lateinit var radioFalse: RadioButton
    private lateinit var option1: RadioButton
    private lateinit var option2: RadioButton
    private lateinit var option3: RadioButton
    private lateinit var option4: RadioButton
    private lateinit var actionButton: Button
    private lateinit var scoreTextView: TextView
    private lateinit var returnButton: Button
    private lateinit var saveScoreButton: Button
    private lateinit var exitQuizButton: Button

    private var questionList: List<Question> = listOf()
    private var currentQuestionIndex = 0
    private var score = 0  // Počáteční skóre

    private lateinit var selectedCategory: String
    private lateinit var selectedDifficulty: String
    private lateinit var selectedType: String
    private lateinit var questions: Array<Question>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_question)

        // Inicializace UI prvků
        questionTextView = findViewById(R.id.questionTextView)
        trueFalseContainer = findViewById(R.id.trueFalseContainer)
        multipleChoiceContainer = findViewById(R.id.multipleChoiceContainer)
        radioTrue = findViewById(R.id.radioTrue)
        radioFalse = findViewById(R.id.radioFalse)
        option1 = findViewById(R.id.option1)
        option2 = findViewById(R.id.option2)
        option3 = findViewById(R.id.option3)
        option4 = findViewById(R.id.option4)
        actionButton = findViewById(R.id.actionButton)
        scoreTextView = findViewById(R.id.scoreTextView)
        returnButton = findViewById(R.id.returnButton)
        saveScoreButton = findViewById(R.id.saveScoreButton)
        exitQuizButton = findViewById(R.id.btn_exit_quiz)

        // Načtení dat z Intentu
        val selectedCategoryName = intent.getStringExtra("categoryName") ?: "Unknown Category" // Přijměte název kategorie
        selectedCategory = intent.getStringExtra("category").toString()
        selectedDifficulty = intent.getStringExtra("difficulty").toString()
        selectedType = intent.getStringExtra("questionType").toString()
        questions = intent.getParcelableArrayExtra("questions")?.map { it as Question }?.toTypedArray() ?: arrayOf()

        // Zobrazit první otázku, pokud existují
        if (questions.isNotEmpty()) {
            displayQuestion()
        }

        actionButton.setOnClickListener {
            handleActionButton()
        }

        returnButton.setOnClickListener {
            navigateToMainMenu()
        }

        saveScoreButton.setOnClickListener {
            showSaveScoreDialog(selectedCategoryName, selectedDifficulty)
        }

        exitQuizButton.setOnClickListener {
            navigateToMainMenu()
        }
    }

    private fun displayQuestion() {
        val currentQuestion = questions[currentQuestionIndex]
        questionTextView.text = currentQuestion.questionText

        if (selectedType == "boolean") {
            trueFalseContainer.visibility = View.VISIBLE
            multipleChoiceContainer.visibility = View.GONE

            radioTrue.isChecked = false
            radioFalse.isChecked = false
        } else {
            trueFalseContainer.visibility = View.GONE
            multipleChoiceContainer.visibility = View.VISIBLE

            val options = currentQuestion.options.shuffled()
            option1.text = options[0]
            option2.text = options[1]
            option3.text = options[2]
            option4.text = options[3]

            option1.isChecked = false
            option2.isChecked = false
            option3.isChecked = false
            option4.isChecked = false
        }

    }

    private fun handleActionButton() {
        if (actionButton.text == "Check Answer") {
            val selectedOption = when {
                selectedType == "boolean" -> {
                    when {
                        radioTrue.isChecked -> "True"
                        radioFalse.isChecked -> "False"
                        else -> null
                    }
                }
                else -> when {
                    option1.isChecked -> option1.text.toString()
                    option2.isChecked -> option2.text.toString()
                    option3.isChecked -> option3.text.toString()
                    option4.isChecked -> option4.text.toString()
                    else -> null
                }
            }
            checkAnswer(selectedOption)
            highlightAnswers(selectedOption ?: "")
            actionButton.text = "Next Question"
        } else {
            if (currentQuestionIndex < questions.size - 1) {
                currentQuestionIndex++
                displayQuestion()
                resetAnswerColors()
                actionButton.text = "Check Answer"
            } else {
                saveScoreButton.visibility = View.VISIBLE
                scoreTextView.text = "Your Score: $score/${questions.size}"
                showFinalScoreDialog()
            }
        }
    }


    private fun checkAnswer(selectedOption: String?) {
        val correctAnswer = questions[currentQuestionIndex].correctAnswer
        if (selectedOption.isNullOrEmpty()) {
            Toast.makeText(this, "No answer selected!", Toast.LENGTH_SHORT).show()
        } else if (selectedOption == correctAnswer) {
            score++
            Toast.makeText(this, "Correct!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Incorrect!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun highlightAnswers(selectedOption: String) {
        val correctAnswer = questions[currentQuestionIndex].correctAnswer

        resetAnswerColors()

        if (selectedOption == correctAnswer) {
            highlightCorrectAnswer(correctAnswer)
        } else {
            highlightIncorrectAnswer(selectedOption)
            highlightCorrectAnswer(correctAnswer)
        }
    }

    private fun highlightCorrectAnswer(correctAnswer: String) {
        if (selectedType == "boolean") {
            when (correctAnswer) {
                "True" -> radioTrue.setTextColor(Color.GREEN)
                "False" -> radioFalse.setTextColor(Color.GREEN)
            }
        } else {
            when (correctAnswer) {
                option1.text -> option1.setTextColor(Color.GREEN)
                option2.text -> option2.setTextColor(Color.GREEN)
                option3.text -> option3.setTextColor(Color.GREEN)
                option4.text -> option4.setTextColor(Color.GREEN)
            }
        }
    }

    private fun highlightIncorrectAnswer(selectedOption: String) {
        if (selectedType == "boolean") {
            when (selectedOption) {
                "True" -> radioTrue.setTextColor(Color.RED)
                "False" -> radioFalse.setTextColor(Color.RED)
            }
        } else {
            when (selectedOption) {
                option1.text -> option1.setTextColor(Color.RED)
                option2.text -> option2.setTextColor(Color.RED)
                option3.text -> option3.setTextColor(Color.RED)
                option4.text -> option4.setTextColor(Color.RED)
            }
        }
    }


    private fun resetAnswerColors() {
        radioTrue.setTextColor(Color.BLACK)
        radioFalse.setTextColor(Color.BLACK)
        option1.setTextColor(Color.BLACK)
        option2.setTextColor(Color.BLACK)
        option3.setTextColor(Color.BLACK)
        option4.setTextColor(Color.BLACK)
    }


    private fun showFinalScoreDialog() {
        // Zobrazit skóre a tlačítka
        actionButton.visibility = View.GONE
        scoreTextView.visibility = View.VISIBLE
        returnButton.visibility = View.VISIBLE
        saveScoreButton.visibility = View.VISIBLE
        exitQuizButton.visibility = View.GONE
        questionTextView.visibility = View.GONE
        trueFalseContainer.visibility = View.GONE
        multipleChoiceContainer.visibility = View.GONE


        // Vytvořit vtipné hodnocení podle dosaženého skóre
        val funnyRating = when {
            score == questions.size -> "Perfect score! You are a quiz master!"
            score >= questions.size * 0.75 -> "Great job! You're almost there!"
            score >= questions.size * 0.5 -> "Good try! You know more than you think!"
            score >= questions.size * 0.25 -> "Not bad! Keep practicing!"
            else -> "Better luck next time! You can do it!"
        }

        // Nastavit text pro skóre a hodnocení
        scoreTextView.text = "Your score is: $score/${questions.size}\n$funnyRating"
    }


    private fun showSaveScoreDialog(categoryName: String, difficulty: String) {
        val builder = AlertDialog.Builder(this)
        val input = EditText(this)

        // Načíst poslední zadané jméno z SharedPreferences, pokud existuje
        val sharedPreferences = getSharedPreferences("quiz_app", MODE_PRIVATE)
        val lastName = sharedPreferences.getString("last_name", "") ?: ""

        // Nastavit poslední zadané jméno do input pole
        input.setText(lastName)

        builder.setTitle("Enter your name")
        builder.setView(input)

        builder.setPositiveButton("Save") { _, _ ->
            val name = input.text.toString()

            // Uložit nové jméno do SharedPreferences, aby bylo použito při dalším zobrazení
            val editor = sharedPreferences.edit()
            editor.putString("last_name", name)
            editor.apply()

            val gson = Gson()
            val existingScores = sharedPreferences.getString("scores", "[]")
            val type = object : TypeToken<MutableList<Map<String, String>>>() {}.type
            val scoreList: MutableList<Map<String, String>> =
                gson.fromJson(existingScores, type) ?: mutableListOf()

            val scoreEntry = mapOf(
                "name" to name,
                "score" to score.toString(),
                "category" to categoryName, // Použijte název kategorie
                "difficulty" to difficulty
            )
            scoreList.add(scoreEntry)

            val editorScores = sharedPreferences.edit()
            editorScores.putString("scores", gson.toJson(scoreList))
            editorScores.apply()

            Toast.makeText(this, "Score saved!", Toast.LENGTH_SHORT).show()
            navigateToMainMenu()
        }

        builder.setNegativeButton("Cancel", null)
        builder.show()
    }


    private fun navigateToMainMenu() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}
