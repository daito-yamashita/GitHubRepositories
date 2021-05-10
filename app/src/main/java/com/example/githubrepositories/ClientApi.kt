package com.example.githubrepositories

import retrofit2.Response
import retrofit2.http.*

interface ClientApi {
    @GET("users/daito-yamashita/repos")

    fun getGitHub(): retrofit2.Call<Result>
}

