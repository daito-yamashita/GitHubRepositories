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
import io.reactivex.rxjava3.schedulers.Schedulers

class MainActivity : AppCompatActivity() {

    private lateinit var mainAdapter: MainAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fetchMyData()

    }

    private fun fetchMyData() {
        createService()
                .getGitHub("daito-yamashita")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map {
                    it.sortedByDescending { gitHubResponse ->
                        gitHubResponse.pushed_at
                    }
                }
                .subscribe {
                    // RecyclerViewの作成、更新を行う
                    createRecyclerView(it)
                    mainAdapter.setOnCellClickListener(
                            object : MainAdapter.OnCellClickListener {
                                override fun onItemClick(model: GitHubResponse) {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(model.html_url))
                                    startActivity(intent)
                                }
                            }
                    )
                    Log.d("TAG", "subscribe = $it")
                }
    }

    private fun createRecyclerView(dataList: List<GitHubResponse>) {
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

