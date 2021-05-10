package com.example.githubrepositories

import retrofit2.http.*

interface ApiService {
    @GET("users/daito-yamashita/repos")

    fun getGitHub(): retrofit2.Call<Model>
}

