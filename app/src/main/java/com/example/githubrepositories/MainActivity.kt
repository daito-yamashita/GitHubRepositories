package com.example.githubrepositories

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers

const val USER_NAME: String = "daito-yamashita"
const val NOW_PAGE: Int = 1

class MainActivity : AppCompatActivity() {

    private var modelList: MutableList<Model> = mutableListOf()
    private lateinit var recyclerView: RecyclerView
    private lateinit var mainAdapter: MainAdapter
    private lateinit var mainLayoutManager: RecyclerView.LayoutManager
    private lateinit var itemTouchHelper: ItemTouchHelper

    private var nowPage: Int = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fetchMyData()

        setupNextButton()

    }

    private fun fetchMyData() {
        Single.zip(
            getRepositoryList(),
            getProfileList(),
            { repositoryList, profile ->
                repositoryList.map {
                    Model(
                        html_url = it.html_url,
                        name = it.name,
                        language = it.language,
                        pushed_at = it.pushed_at,
                        avatar_url = profile.avatar_url,
                    )
                }
            })
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({

                modelList = it.toMutableList()

                // RecyclerViewの作成、更新を行う
                createRecyclerView(modelList)

                // RecyclerViewのドラッグ、スワイプ操作に関する設定
                itemTouchHelper = ItemTouchHelper(getRecyclerViewSimpleCallBack())
                itemTouchHelper.attachToRecyclerView(recyclerView)

                // RecyclerViewのクリックに関する設定
                mainAdapter.setOnCellClickListener(getOnCellClickListener())

                Log.d("TAG", "subscribe = $it")
            }, {
                Log.d("TAG", "failure = $it")
            })
    }

    private fun getRepositoryList(): Single<List<GitHubRepository>> {
        return createService()
            .getGitHubRepositoryList(USER_NAME, nowPage)
            .map { it ->
                val comparator =
                    compareByDescending<GitHubRepository> { it.pushed_at }.thenBy { it.name }
                it.sortedWith(comparator)
            }
    }

    private fun getProfileList(): Single<GitHubProfile> {
        return createService()
            .getGitHubProfile(USER_NAME)
    }

    private fun createRecyclerView(modelList: List<Model>) {
        recyclerView = findViewById(R.id.main_recycler_view)

        // recyclerViewのレイアウトサイズを変更しない設定をONにする
        recyclerView.setHasFixedSize(true)

        // recyclerViewに区切り線を追加
        val itemDecoration = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        recyclerView.addItemDecoration(itemDecoration)

        // recyclerViewにlayoutManagerをセットする
        mainLayoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = mainLayoutManager

        // Adapterを生成してRecyclerViewにセット
        mainAdapter = MainAdapter(modelList)
        recyclerView.adapter = mainAdapter
    }

    private fun getRecyclerViewSimpleCallBack() =
        object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT,
        ) {
            // ドラッグした時
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                val fromPosition = viewHolder.absoluteAdapterPosition
                val toPosition = target.absoluteAdapterPosition

                // modelListのデータを削除してから追加している
                modelList.add(toPosition, modelList.removeAt(fromPosition))
                mainAdapter.notifyItemMoved(fromPosition, toPosition)
                
                return true
            }

            // スワイプした時
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                viewHolder.let {
                    modelList.removeAt(viewHolder.absoluteAdapterPosition)
                    mainAdapter.notifyItemRemoved(viewHolder.absoluteAdapterPosition)
                }
            }
        }

    private fun getOnCellClickListener() =
        object : MainAdapter.OnCellClickListener {
            override fun onItemClick(model: Model) {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(model.html_url))
                startActivity(intent)
            }
        }

    private fun setupNextButton() {
        val nextButton = findViewById<Button>(R.id.next_button)
        nextButton.setOnClickListener{
            nowPage += 1
            fetchMyData()
        }
    }
}
