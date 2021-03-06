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

class MainActivity : AppCompatActivity() {

    private var modelList: MutableList<Model> = mutableListOf()
    private lateinit var recyclerView: RecyclerView
    private lateinit var mainAdapter: MainAdapter
    private lateinit var mainLayoutManager: RecyclerView.LayoutManager
    private lateinit var itemTouchHelper: ItemTouchHelper

    private var nowPage: Int = 1
    private var isNextButtonPress: Boolean = false

    private lateinit var previousButton: Button
    private lateinit var nextButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fetchMyData()

        setupNextButton()

        setupPreviousButton()

    }

    private fun fetchMyData() {
        getRepositoryList()
                .map {
                    it.map { gitHubRepository ->
                        Model(
                                html_url = gitHubRepository.html_url,
                                name = gitHubRepository.name,
                                language = gitHubRepository.language,
                                pushed_at = gitHubRepository.pushed_at,
                                avatar_url = gitHubRepository.owner["avatar_url"] as String
                        )
                    }
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    // ?????????????????????????????????????????????????????????????????????????????????
                    modelList.clear()
                    modelList.addAll(it.toMutableList())

                    // ?????????????????????????????????????????????????????????????????????
                    updatePreviousButton()
                    updateNextButton()

                    if (isNextButtonPress) {
                        // ????????????????????????
                        // ??????????????????notifyDataSetChanged()???List?????????????????????

                        mainAdapter.notifyDataSetChanged()
                    } else {
                        // ????????????????????????

                        // RecyclerView???????????????????????????
                        createRecyclerView(modelList)

                        // RecyclerView??????????????????????????????????????????????????????
                        itemTouchHelper = ItemTouchHelper(getRecyclerViewSimpleCallBack())
                        itemTouchHelper.attachToRecyclerView(recyclerView)

                        // RecyclerView?????????????????????????????????
                        mainAdapter.setOnCellClickListener(getOnCellClickListener())

                        // ????????????????????????
                        isNextButtonPress = true
                    }

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

    private fun createRecyclerView(modelList: List<Model>) {
        recyclerView = findViewById(R.id.main_recycler_view)

        // recyclerView??????????????????????????????????????????????????????ON?????????
        recyclerView.setHasFixedSize(true)

        // recyclerView????????????????????????
        val itemDecoration = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        recyclerView.addItemDecoration(itemDecoration)

        // recyclerView???layoutManager??????????????????
        mainLayoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = mainLayoutManager

        // Adapter???????????????RecyclerView????????????
        mainAdapter = MainAdapter(modelList)
        recyclerView.adapter = mainAdapter
    }

    private fun getRecyclerViewSimpleCallBack() =
            object : ItemTouchHelper.SimpleCallback(
                    ItemTouchHelper.UP or ItemTouchHelper.DOWN,
                    ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT,
            ) {
                // ?????????????????????
                override fun onMove(
                        recyclerView: RecyclerView,
                        viewHolder: RecyclerView.ViewHolder,
                        target: RecyclerView.ViewHolder
                ): Boolean {
                    val fromPosition = viewHolder.absoluteAdapterPosition
                    val toPosition = target.absoluteAdapterPosition

                    // modelList???????????????????????????????????????????????????
                    modelList.add(toPosition, modelList.removeAt(fromPosition))
                    mainAdapter.notifyItemMoved(fromPosition, toPosition)

                    return true
                }

                // ?????????????????????
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

    private fun setupPreviousButton() {
        previousButton = findViewById(R.id.previous_button)
        previousButton.setOnClickListener {
            nowPage -= 1
            fetchMyData()
        }
    }

    private fun updatePreviousButton() {
        previousButton.isEnabled = nowPage != 1
    }

    private fun setupNextButton() {
        nextButton = findViewById(R.id.next_button)
        nextButton.setOnClickListener {
            nowPage += 1
            fetchMyData()
        }
    }

    private fun updateNextButton() {
        // TODO: ???????????????API????????????????????????????????????
        nextButton.isEnabled = nowPage != 4
    }
}
