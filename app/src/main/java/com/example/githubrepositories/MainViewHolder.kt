package com.example.githubrepositories

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MainViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var icon: ImageView = itemView.findViewById(R.id.icon_image_view)
    var title: TextView = itemView.findViewById(R.id.title_text_view)
    var language: TextView = itemView.findViewById(R.id.language_text_view)
    var updated: TextView = itemView.findViewById(R.id.updated_text_view)
}