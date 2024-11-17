package com.example.lab3

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    private val ADD_POST_REQUEST_CODE = 1
    private val EDIT_POST_REQUEST_CODE = 2
    private lateinit var postViewModel: PostViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var addPostButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        recyclerView = findViewById(R.id.recyclerView)
        addPostButton = findViewById(R.id.addPostButton)
        recyclerView.layoutManager = LinearLayoutManager(this)


        if (!NetworkUtils.isNetworkAvailable(this)) {
            showNoInternetDialog()
            return
        }

        val retrofit = Retrofit.Builder()
            .baseUrl("https://jsonplaceholder.typicode.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val apiService = retrofit.create(ApiService::class.java)
        val postRepository = PostRepository(apiService)
        postViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(PostViewModel::class.java)
        postViewModel.setRepository(postRepository)

        // Завантаження постів
        postViewModel.posts.observe(this, { posts ->
            recyclerView.adapter = PostAdapter(posts) { post ->
                showPostOptionsDialog(post)
            }
        })

        addPostButton.setOnClickListener {
            val intent = Intent(this, AddPostActivity::class.java)
            startActivityForResult(intent, ADD_POST_REQUEST_CODE)
        }

        postViewModel.fetchPosts()
    }

    private fun showNoInternetDialog() {
        AlertDialog.Builder(this)
            .setTitle("Відсутнє з'єднання")
            .setMessage("Перевірте ваше інтернет-з'єднання і спробуйте знову.")
            .setPositiveButton("ОК") { _, _ -> finish() }
            .setCancelable(false)
            .show()
    }

    private fun showPostOptionsDialog(post: Post) {
        val options = arrayOf("Редагувати", "Видалити")
        AlertDialog.Builder(this)
            .setTitle("Оберіть дію")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> editPost(post)
                    1 -> deletePost(post)
                }
            }
            .show()
    }


    private fun editPost(post: Post) {
        val intent = Intent(this, AddPostActivity::class.java)
        intent.putExtra("postId", post.id)
        intent.putExtra("postTitle", post.title)
        intent.putExtra("postBody", post.body)
        startActivityForResult(intent, EDIT_POST_REQUEST_CODE)
    }


    private fun deletePost(post: Post) {
        val updatedPosts = postViewModel.posts.value?.toMutableList()
        updatedPosts?.remove(post)
        postViewModel.posts.value = updatedPosts
        Toast.makeText(this, "Пост видалений", Toast.LENGTH_SHORT).show()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                ADD_POST_REQUEST_CODE -> {
                    val title = data?.getStringExtra("title") ?: ""
                    val body = data?.getStringExtra("body") ?: ""
                    val newPost = Post(userId = 1, id = (postViewModel.posts.value?.size ?: 0) + 1, title = title, body = body)
                    val updatedPosts = postViewModel.posts.value?.toMutableList() ?: mutableListOf()
                    updatedPosts.add(0, newPost)
                    postViewModel.posts.value = updatedPosts
                }
                EDIT_POST_REQUEST_CODE -> {
                    val postId = data?.getIntExtra("postId", -1) ?: -1
                    val newTitle = data?.getStringExtra("title") ?: ""
                    val newBody = data?.getStringExtra("body") ?: ""
                    val updatedPosts = postViewModel.posts.value?.toMutableList()
                    val postIndex = updatedPosts?.indexOfFirst { it.id == postId }
                    if (postIndex != null && postIndex >= 0) {
                        updatedPosts[postIndex] = updatedPosts[postIndex].copy(title = newTitle, body = newBody)
                        postViewModel.posts.value = updatedPosts
                    }
                }
            }
        }
    }
}
