package com.example.githubrepositories

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

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
    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        val model = modelList[position]
        holder.title.text = model.name
        holder.language.text = model.language
        holder.updated.text = model.updated_at

        // `holder.language.text` だとnullが取ってこれなかったので `model.language` を使う
        if (model.language == null) {
            holder.language.setVisibility(View.GONE)
        }

        // セルのクリックイベントにリスナをセット
        holder.itemView.setOnClickListener {
            listener.onItemClick(model)
        }
    }

    override fun getItemCount(): Int {
        return modelList.size
    }
}
