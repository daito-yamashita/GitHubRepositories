package com.example.githubrepositories

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

class MainAdapter internal constructor(private var modelList: List<Model>) : RecyclerView.Adapter<MainViewHolder>() {
    private lateinit var listener: OnCellClickListener

    interface OnCellClickListener {
        fun onItemClick(model: Model)
    }

    fun setOnCellClickListener(listener: OnCellClickListener) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.view_holder, parent, false)
        return MainViewHolder(view)
    }

    // 各部品に持たせたいデータを割り当てるメソッド
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        val model = modelList[position]

        holder.title.text = model.name
        holder.language.text = model.language

        val updatedTimeText = model.pushed_at
        holder.updated.text = getDateDifference(updatedTimeText)

        // `holder.language.text` だとnullが取ってこれなかったので `model.language` を使う
        if (model.language == null) {
            holder.language.visibility = View.GONE
        }

        // セルのクリックイベントにリスナをセット
        holder.itemView.setOnClickListener {
            listener.onItemClick(model)
        }
    }

    override fun getItemCount(): Int {
        return modelList.size
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getDateDifference(updatedDataTimeText: String?): CharSequence? {
        var updatedText = "updated "
        val nowDateTime = OffsetDateTime.now()
        val updatedDateTime = OffsetDateTime.parse(updatedDataTimeText, DateTimeFormatter.ISO_DATE_TIME)
        val diff = ChronoUnit.SECONDS.between(updatedDateTime, nowDateTime)

        val sec = diff
        val min = sec / 60L
        if (min == 0L) {
            if (sec == 1L) {
                updatedText += "${sec}second ago"
                return updatedText
            }
            updatedText += "${sec}seconds ago"
            return updatedText
        }

        val hour = min / 60L
        if (hour == 0L) {
            if (min == 1L) {
                updatedText += "${min}minute ago"
                return updatedText
            }
            updatedText += "${min}minutes ago"
            return updatedText
        }

        val day = hour / 24L
        if (day == 0L) {
            updatedText += "${hour}h ago"
            return updatedText
        }

        val month = day / 30L
        if (month == 0L) {
            if (day == 1L) {
                updatedText += "yesterday"
                return updatedText
            }
            updatedText += "${day}days ago"
            return updatedText
        }

        updatedText += "on ${updatedDateTime.dayOfMonth} ${updatedDateTime.month}"
        return updatedText
    }
}
