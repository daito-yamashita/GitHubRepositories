package com.example.githubrepositories

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.functions.BiFunction
import io.reactivex.rxjava3.schedulers.Schedulers

class MainActivity : AppCompatActivity() {

    private lateinit var mainAdapter: MainAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fetchMyData()

    }

    private fun fetchMyData() {
        Single
            .zip(
                single1(),
                single2(),
                BiFunction<List<GitHubRepository>, GitHubProfile, List<Model>> { s1, s2 ->
                    val dataList = mutableListOf<Model>()
                    for (item in s1) {
                        val data: Model = Model(
                            html_url = item.html_url,
                            name = item.name,
                            language = item.language,
                            pushed_at = item.pushed_at,
                            avatar_url = s2.avatar_url
                        )
                        dataList.add(data)
                    }
//                    listOf(
//                        Model(
//                            s1.first().html_url,
//                            s1.first().name,
//                            s1.first().language,
//                            s1.first().pushed_at,
//                            s2.avatar_url
//                        )
//                    )
                    dataList
                })
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                // RecyclerViewの作成、更新を行う
                createRecyclerView(it)
                mainAdapter.setOnCellClickListener(
                    object : MainAdapter.OnCellClickListener {
                        override fun onItemClick(model: Model) {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(model.html_url))
                            startActivity(intent)
                        }
                    }
                )
                Log.d("TAG", "subscribe = $it")
            }, {
                Log.d("TAG", "failure = $it")
            })

    }

    private fun single1(): Single<List<GitHubRepository>> {
        return createService()
            .getRepository("daito-yamashita")
            .map {
                it.sortedByDescending { gitHubResponse ->
                    gitHubResponse.pushed_at
                }
            }
    }

    private fun single2(): Single<GitHubProfile> {
        return createService()
            .getProfile("daito-yamashita")
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

