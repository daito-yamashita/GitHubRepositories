package com.example.githubrepositories

data class GitHubRepository(
    val html_url : String,
    val name : String,
    val language : String?,
    val pushed_at : String,
    val owner : Map<String, Any>,
)

class Model(
    val html_url : String,
    val name : String,
    val language : String? = null,
    val pushed_at : String,
    val avatar_url : String,
)