package com.example.moodmate

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MoodActivity : AppCompatActivity() {

    private lateinit var moodDropdown: AutoCompleteTextView
    private lateinit var saveMoodBtn: Button
    private lateinit var quoteText: TextView

    private val db = FirebaseFirestore.getInstance()
    private val userId = FirebaseAuth.getInstance().currentUser?.uid ?: "anonymous"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mood)

        moodDropdown = findViewById(R.id.moodDropdown)
        saveMoodBtn = findViewById(R.id.saveMoodBtn)
        quoteText = findViewById(R.id.quoteText)
        loadCachedMood()

        // Setup dropdown with search
        val moods = resources.getStringArray(R.array.mood_options)
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, moods)
        moodDropdown.setAdapter(adapter)

        saveMoodBtn.setOnClickListener {
            val selectedMood = moodDropdown.text.toString().trim()
            if (selectedMood.isEmpty()) {
                Toast.makeText(this, "Please select your mood", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            saveMoodToFirestore(selectedMood)
            fetchQuoteForMood(selectedMood)
        }
    }

    private fun saveMoodToFirestore(mood: String) {
        val moodEntry = hashMapOf(
            "mood" to mood,
            "timestamp" to System.currentTimeMillis()
        )
        db.collection("users").document(userId)
            .collection("moods")
            .add(moodEntry)
            .addOnSuccessListener {
                Toast.makeText(this, "Mood saved!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to save mood", Toast.LENGTH_SHORT).show()
            }
    }

    private fun fetchQuoteForMood(mood: String) {
        db.collection("quotes").document(mood.lowercase())
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val quote = document.getString("quote") ?: "Stay positive!"
                    quoteText.text = quote
                    val prefs = getSharedPreferences("MoodMatePrefs", MODE_PRIVATE)
                    prefs.edit()
                        .putString("lastMood", mood)
                        .putString("lastQuote", quote)
                        .apply()
                } else {
                    quoteText.text = "No quote found for this mood. Stay strong!"
                }
            }
            .addOnFailureListener {
                quoteText.text = "Error fetching quote."
            }
    }

    private fun loadCachedMood() {
        val prefs = getSharedPreferences("MoodMatePrefs", MODE_PRIVATE)
        val lastMood = prefs.getString("lastMood", null)
        val lastQuote = prefs.getString("lastQuote", null)

        if (lastMood != null && lastQuote != null) {
            findViewById<TextView>(R.id.lastMoodTextView)?.text = "Last mood: $lastMood\n\nLast Quote: $lastQuote"
        }
    }

}
