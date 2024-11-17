package com.example.lab3

class PostRepository(private val apiService: ApiService) {
    suspend fun getPosts() = apiService.getPosts()
}
