package com.example.moodmate.utils

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.moodmate.R
import com.example.moodmate.utils.model.MoodEntry
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MoodHistoryAdapter(private val moodList: List<MoodEntry>) :
    RecyclerView.Adapter<MoodHistoryAdapter.MoodViewHolder>() {

    class MoodViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val moodText: TextView = itemView.findViewById(R.id.moodTextView)
        val quoteText: TextView = itemView.findViewById(R.id.quoteTextView)
        val timeText: TextView = itemView.findViewById(R.id.timestampTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MoodViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_mood, parent, false)
        return MoodViewHolder(view)
    }

    override fun onBindViewHolder(holder: MoodViewHolder, position: Int) {
        val entry = moodList[position]
        holder.moodText.text = "Mood: ${entry.mood}"
        holder.quoteText.text = "\"${entry.quote}\""
        holder.timeText.text = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
            .format(Date(entry.timestamp))
    }

    override fun getItemCount(): Int = moodList.size
}
