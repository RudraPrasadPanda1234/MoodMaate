package com.example.moodmate

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.example.moodmate.utils.model.Vent
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class RepliesAdapter(private val context: Context, private val ventList: List<Pair<String, Vent>>) :
    RecyclerView.Adapter<RepliesAdapter.VentViewHolder>() {

    class VentViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ventText: TextView = view.findViewById(R.id.ventText)
        val replyList: TextView = view.findViewById(R.id.replyList)
        val replyButton: Button = view.findViewById(R.id.replyButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VentViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_vent, parent, false)
        return VentViewHolder(view)
    }

    override fun onBindViewHolder(holder: VentViewHolder, position: Int) {
        val (key, vent) = ventList[position]
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
        val isMine = vent.userId == currentUserId

        holder.ventText.text = if (isMine) "Your Feeling: ${vent.text}" else vent.text

        val replies = vent.replies?.values?.joinToString("\n\n") { "Reply: ${it.text}" } ?: "No replies yet."
        holder.replyList.text = replies

        if (isMine) {
            holder.replyButton.visibility = View.GONE
        } else {
            holder.replyButton.visibility = View.VISIBLE
            holder.replyButton.setOnClickListener {
                val input = EditText(context)
                AlertDialog.Builder(context)
                    .setTitle("Reply Anonymously")
                    .setView(input)
                    .setPositiveButton("Send") { _, _ ->
                        val replyText = input.text.toString().trim()
                        if (replyText.isNotEmpty()) {
                            val reply = mapOf("text" to replyText)
                            val ref: DatabaseReference = FirebaseDatabase.getInstance()
                                .getReference("vents").child(key).child("replies")
                            val replyKey = ref.push().key ?: return@setPositiveButton
                            ref.child(replyKey).setValue(reply)
                        }
                    }
                    .setNegativeButton("Cancel", null)
                    .show()
            }
        }
    }

    override fun getItemCount(): Int = ventList.size
}
