package com.example.githubrepositories

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.DividerItemDecoration
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

        fetchMyData()
    }

    private fun fetchMyData() {
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
                                it.language = item.language
                                it.updated_at = item.updated_at
                            }
                            dataList.add(data)
                        }
                        // ここでRecyclerViewを表示させないと、非同期処理の実行順番の兼ね合いで何も表示されない
                        createRecyclerView(dataList)
                    }
                }
            }
            override fun onFailure(call: Call<List<GitHubResponse>>, t: Throwable) {
                Log.d("TAGres", "onFailure")
            }
        })
    }

    private fun createRecyclerView(dataList: List<Model>) {
        val recyclerView: RecyclerView = findViewById(R.id.main_recycler_view)

        // recyclerViewのレイアウトサイズを変更しない設定をONにする
        recyclerView.setHasFixedSize(true)

        // recyclerViewに区切り線を追加
        val itemDecoration = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        recyclerView.addItemDecoration(itemDecoration)

        // recyclerViewにlayoutManagerをセットする
        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager

        // Adapterを生成してRecyclerViewにセット
        mainAdapter = MainAdapter(dataList)
        recyclerView.adapter = mainAdapter
    }
}

