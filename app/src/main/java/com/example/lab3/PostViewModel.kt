package com.example.lab3

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class PostViewModel : ViewModel() {

    private lateinit var postRepository: PostRepository

    val posts = MutableLiveData<List<Post>>()
    val loading = MutableLiveData<Boolean>()
    val errorMessage = MutableLiveData<String>()

    fun setRepository(repository: PostRepository) {
        this.postRepository = repository
    }

    fun fetchPosts() {
        if (!::postRepository.isInitialized) {
            errorMessage.value = "Репозиторій не ініціалізований"
            return
        }

        viewModelScope.launch {
            loading.value = true
            try {

                posts.value = postRepository.getPosts()
            } catch (e: Exception) {
                errorMessage.value = "Помилка при завантаженні даних: ${e.message}"
            } finally {
                loading.value = false
            }
        }
    }
}
