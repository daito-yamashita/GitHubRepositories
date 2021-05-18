package com.example.githubrepositories

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.reactivex.rxjava3.schedulers.Schedulers

class MainActivity : AppCompatActivity() {

    private lateinit var mainAdapter: MainAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fetchMyData()

    }

    private fun fetchMyData() {
        val dataList = mutableListOf<Model>()
        createService()
            .getGitHub("daito-yamashita")
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.computation())
            .subscribe({
                Log.d("TAG", "Success")
            }, {
                Log.d("TAG", "Failure")

            })


//    createService().getGitHub("daito-yamashita").enqueue(object: Callback<List<GitHubResponse>> {
//        // 非同期処理
//        override fun onResponse(call: Call<List<GitHubResponse>>, response: Response<List<GitHubResponse>>) {
//            Log.d("TAGres", "onResponse")
//            if(response.isSuccessful) {
//                response.body()?.let { it ->
//                    for(item in it) {
//                        val data: Model = Model().also {
//                            it.html_url =item.html_url
//                            it.name = item.name
//                            it.language = item.language
//                            it.pushed_at = item.pushed_at
//                        }
//                        dataList.add(data)
//                    }
//                    dataList.sortByDescending { it.pushed_at }
//
//                    // ここでRecyclerViewを表示させないと、非同期処理の実行順番の兼ね合いで何も表示されない
//                    createRecyclerView(dataList)
//
//                    // この処理も同様に、このタイミングで実行しないとクリックしたときに何も反応しない
//                    mainAdapter.setOnCellClickListener(
//                            object : MainAdapter.OnCellClickListener {
//                                override fun onItemClick(model: Model) {
//                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(model.html_url))
//                                    startActivity(intent)
//                                }
//                            }
//                    )
//                }
//            }
//        }
//        override fun onFailure(call: Call<List<GitHubResponse>>, t: Throwable) {
//            Log.d("TAGres", "onFailure")
//        }
//    })
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

