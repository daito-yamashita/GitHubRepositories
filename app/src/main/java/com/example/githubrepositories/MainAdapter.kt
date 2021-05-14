package com.example.githubrepositories

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class MainAdapter internal constructor(private var modelList: List<Model>) : RecyclerView.Adapter<MainViewHolder>() {
    private lateinit var listener: OnCellClickLitener

    interface OnCellClickLitener {
        fun onItemClick(model: Model)
    }

    fun setOnCellClickListener(listener: OnCellClickLitener) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.view_holder, parent, false)
        return MainViewHolder(view)
    }

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        val model = modelList[position]
        holder.title.text = model.name
        holder.language.text = model.language
        holder.updated.text = model.updated_at

        // `holder.language.text` だとnullが取ってこれなかった
        if (model.language == null) {
            holder.language.setVisibility(View.GONE)
        }

        holder.itemView.setOnClickListener {
            listener.onItemClick(model)
        }
    }

    override fun getItemCount(): Int {
        return modelList.size
    }
}
