package com.example.githubrepositories

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private var mainAdapter: MainAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        fetchMyData()

        createRecyclerView()

    }

    private fun fetchMyData(): List<Model> {
        val dataList = mutableListOf<Model>()
        createService().getGitHub("daito-yamashita").enqueue(object: Callback<List<GitHubResponse>> {
            // 非同期処理
            override fun onResponse(call: Call<List<GitHubResponse>>, response: Response<List<GitHubResponse>>) {
                Log.d("TAGres", "onResponse")
                if(response.isSuccessful) {
                    response.body()?.let {
                        for(item in it) {
                            val data: Model = Model().also {
                                it.id = item.id
                                it.name = item.name
                                it.html_url = item.html_url
                            }
                            dataList.add(data)
                        }
                    }
                }
            }
            override fun onFailure(call: Call<List<GitHubResponse>>, t: Throwable) {
                Log.d("TAGres", "onFailure")
            }
        })
        return dataList
    }

    private fun createRecyclerView() {
        val recyclerView: RecyclerView = findViewById(R.id.main_recycler_view)

        // recyclerViewのレイアウトサイズを変更しない設定をONにする
        recyclerView.setHasFixedSize(true)

        // recyclerViewにlayoutManagerをセットする
        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager

        // Adapterを生成してRecyclerViewにセット
//        mainAdapter = MainAdapter(createRowData(page))
        mainAdapter = MainAdapter(fetchMyData())
        recyclerView.adapter = mainAdapter
    }

    private fun createRowData(page: Int): List<RowData> {
        val dataSet: MutableList<RowData> = ArrayList()
        var i = 1
        while(i < page * 20) {
            val data = RowData()
            data.title = "title" + Integer.toString(i)
            dataSet.add(data)
            i += 1
        }
        return dataSet
    }

    inner class RowData {
        var title: String? = null
    }
}

