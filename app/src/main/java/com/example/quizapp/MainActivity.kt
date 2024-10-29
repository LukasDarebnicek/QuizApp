package com.example.quizapp

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.quizapp.viewmodel.QuizViewModel

class MainActivity : AppCompatActivity() {

    private val quizViewModel: QuizViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val categorySpinner: Spinner = findViewById(R.id.categorySpinner)

        // Pozoruj změny v kategoriích a naplň Spinner
        quizViewModel.categories.observe(this, Observer { categories ->
            val categoryNames = categories.map { it.name }
            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categoryNames)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            categorySpinner.adapter = adapter
        })

        // Načti kategorie při spuštění
        quizViewModel.loadCategories()
    }
}
