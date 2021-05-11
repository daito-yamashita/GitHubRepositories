package com.example.githubrepositories

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

//class MainAdapter internal constructor(private var rowDataList: List<MainActivity.RowData>) : RecyclerView.Adapter<MainViewHolder>() {
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
//        val view = LayoutInflater.from(parent.context).inflate(R.layout.view_holder, parent, false)
//        return MainViewHolder(view)
//    }
//
//    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
//        val rowData = rowDataList[position]
//        holder.title.text = rowData.title
//    }
//
//    override fun getItemCount(): Int {
//        return rowDataList.size
//    }
//}

class MainAdapter internal constructor(private var modelList: List<Model>) : RecyclerView.Adapter<MainViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.view_holder, parent, false)
        return MainViewHolder(view)
    }

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        val model = modelList[position]
        holder.title.text = model.name
    }

    override fun getItemCount(): Int {
        return modelList.size
    }
}
