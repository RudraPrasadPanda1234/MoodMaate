package com.example.moodmate

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.moodBtn).setOnClickListener {
            startActivity(Intent(this, MoodActivity::class.java))
        }
        findViewById<Button>(R.id.ventBtn).setOnClickListener {
            startActivity(Intent(this, VentingActivity::class.java))
        }
        findViewById<Button>(R.id.historyBtn).setOnClickListener {
            startActivity(Intent(this, MoodHistoryActivity::class.java))
        }
        findViewById<Button>(R.id.logoutBtn).setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}
