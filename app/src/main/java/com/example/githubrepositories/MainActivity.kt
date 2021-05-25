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

const val CONSTANT_USER_NAME: String = "daito-yamashita"

class MainActivity : AppCompatActivity() {

    private lateinit var mainAdapter: MainAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fetchMyData()

    }

    private fun fetchMyData() {
        Single.zip(
                getRepositoryList(),
                getProfileList(),
                BiFunction<List<GitHubRepository>, GitHubProfile, List<Model>> { repositoryList, profile ->
                    val modelList = mutableListOf<Model>()
                    repositoryList.map {
                        val model = Model(
                                html_url = it.html_url,
                                name = it.name,
                                language = it.language,
                                pushed_at = it.pushed_at,
                                avatar_url = profile.avatar_url
                        )
                        modelList.add(model)
                    }
                    modelList
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

    private fun getRepositoryList(): Single<List<GitHubRepository>> {
        return createService()
                .getGitHubRepositoryList(CONSTANT_USER_NAME)
                .map {
                    val comparator = compareByDescending<GitHubRepository> { it.pushed_at }.thenBy { it.name }
                    it.sortedWith(comparator)
                }
    }

    private fun getProfileList(): Single<GitHubProfile> {
        return createService()
                .getGitHubProfile(CONSTANT_USER_NAME)
    }

    private fun createRecyclerView(modelList: List<Model>) {
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
        mainAdapter = MainAdapter(modelList)
        recyclerView.adapter = mainAdapter
    }
}
