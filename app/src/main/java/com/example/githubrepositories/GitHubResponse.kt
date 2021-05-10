package com.example.githubrepositories

data class GitHubResponse (
    val url: String?,
    val title: String?,
    val user: User?
)

data class User(
    val id: String?
)