package com.example.moodmate

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.moodmate.utils.model.Vent
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class VentingActivity : AppCompatActivity() {

    private lateinit var ventInput: EditText
    private lateinit var postVentBtn: Button
    private lateinit var repliesRecyclerView: RecyclerView
    private lateinit var database: DatabaseReference
    private lateinit var adapter: RepliesAdapter
    private lateinit var progressBar: ProgressBar
    private val ventList = mutableListOf<Pair<String, Vent>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_venting)

        ventInput = findViewById(R.id.ventInput)
        postVentBtn = findViewById(R.id.postVentBtn)
        repliesRecyclerView = findViewById(R.id.repliesRecyclerView)

        database = FirebaseDatabase.getInstance().reference.child("vents")

        adapter = RepliesAdapter(this, ventList)
        repliesRecyclerView.layoutManager = LinearLayoutManager(this)
        repliesRecyclerView.adapter = adapter
        progressBar = findViewById(R.id.progressBar2)

        postVentBtn.setOnClickListener {
            val ventText = ventInput.text.toString().trim()
            if (ventText.isNotEmpty()) {
                postVent(ventText)
                ventInput.text.clear()
            } else {
                Toast.makeText(this, "Please enter something to vent", Toast.LENGTH_SHORT).show()
            }
        }

        listenForVents()
    }

    private fun postVent(vent: String) {
        val key = database.push().key ?: return
        val ventData = mapOf(
            "text" to vent,
            "timestamp" to System.currentTimeMillis(),
            "userId" to FirebaseAuth.getInstance().currentUser?.uid
        )
        database.child(key).setValue(ventData)
        Toast.makeText(this, "Vent posted anonymously!", Toast.LENGTH_SHORT).show()
    }

    private fun listenForVents() {
        progressBar.visibility = View.VISIBLE
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                ventList.clear()
                for (ventSnapshot in snapshot.children) {
                    val key = ventSnapshot.key ?: continue
                    val vent = ventSnapshot.getValue(Vent::class.java)
                    if (vent != null) {
                        ventList.add(Pair(key, vent))
                    }
                }
                adapter.notifyDataSetChanged()
                progressBar.visibility = ProgressBar.GONE
            }

            override fun onCancelled(error: DatabaseError) {
                progressBar.visibility = ProgressBar.GONE
                Toast.makeText(this@VentingActivity, "Failed to load vents.", Toast.LENGTH_SHORT)
                    .show()
            }
        })
    }
}