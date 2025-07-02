package com.example.moodmate

import android.os.Bundle
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.moodmate.utils.MoodHistoryAdapter
import com.example.moodmate.utils.model.MoodEntry
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class MoodHistoryActivity : AppCompatActivity() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MoodHistoryAdapter
    private val moodList = mutableListOf<MoodEntry>()
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_moodhistory)

        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        progressBar = findViewById(R.id.progressBar1)
        recyclerView = findViewById(R.id.moodHistoryRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = MoodHistoryAdapter(moodList)
        recyclerView.adapter = adapter

        loadMoodHistory()
    }

    private fun loadMoodHistory() {
        val userId = auth.currentUser?.uid ?: return
        progressBar.visibility = ProgressBar.VISIBLE

        firestore.collection("users")
            .document(userId)
            .collection("moods")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { result ->
                moodList.clear()
                if (result.isEmpty) {
                    progressBar.visibility = ProgressBar.GONE
                    Toast.makeText(this, "No history!! Enter a mood", Toast.LENGTH_SHORT).show()
                    return@addOnSuccessListener
                }
                for ((index, doc) in result.withIndex()) {
                    val mood = doc.getString("mood") ?: ""
                    val timestamp = doc.getLong("timestamp") ?: 0L
                    moodList.add(MoodEntry(mood, "Loading quote...", timestamp))
                    adapter.notifyItemInserted(index)
                    fetchQuoteForMood(mood, index, result.size())
                }
            }
            .addOnFailureListener {
                progressBar.visibility = ProgressBar.GONE
                Toast.makeText(this, "Failed to load mood history", Toast.LENGTH_SHORT).show()
            }
    }

    private var fetchedCount = 0
    private fun fetchQuoteForMood(mood: String, index: Int, total: Int) {
        val userId = auth.currentUser?.uid ?: return
        firestore.collection("quotes")
            .document(mood.lowercase())
            .get().addOnSuccessListener { document ->
                val quote = if (document.exists()) {
                    document.getString("quote") ?: "Stay strong!"
                } else {
                    "No quote found for this mood."
                }
                moodList[index] = moodList[index].copy(quote = quote)
                adapter.notifyItemChanged(index)

                fetchedCount++
                if (fetchedCount == total) {
                    progressBar.visibility = ProgressBar.GONE
                    fetchedCount = 0
                }
            }
            .addOnFailureListener {
                moodList[index] = moodList[index].copy(quote = "Error loading quote.")
                adapter.notifyItemChanged(index)

                fetchedCount++
                if (fetchedCount == total) {
                    progressBar.visibility = ProgressBar.GONE
                    fetchedCount = 0
                }
            }
    }
}