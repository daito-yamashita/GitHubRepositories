package com.example.githubrepositories

//import java.util.*http
import io.reactivex.rxjava3.core.Observable
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {
    @GET("users/{user}/repos")

    fun getGitHub(@Path("user") user: String): Observable<List<GitHubResponse>>
}
