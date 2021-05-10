package com.example.githubrepositories

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fetchMyData()
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
}

