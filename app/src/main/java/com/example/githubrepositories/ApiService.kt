package com.example.githubrepositories

//import java.util.*http
import io.reactivex.rxjava3.core.Single
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {
    @GET("users/{user}/repos?sort=updated&per_page=100")
    fun getRepository(@Path("user") user: String): Single<List<GitHubRepository>>

    @GET("users/{user}")
    fun getProfile(@Path("user") user: String): Single<GitHubProfile>
}
