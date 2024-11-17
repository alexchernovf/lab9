package com.example.lab3

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class AddPostActivity : AppCompatActivity() {

    private lateinit var titleEditText: EditText
    private lateinit var bodyEditText: EditText
    private lateinit var saveButton: Button
    private var postId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_post)

        titleEditText = findViewById(R.id.titleEditText)
        bodyEditText = findViewById(R.id.bodyEditText)
        saveButton = findViewById(R.id.saveButton)

        // Установка данных для редактирования
        postId = intent.getIntExtra("postId", -1).takeIf { it != -1 }
        titleEditText.setText(intent.getStringExtra("postTitle"))
        bodyEditText.setText(intent.getStringExtra("postBody"))

        saveButton.setOnClickListener {
            val title = titleEditText.text.toString()
            val body = bodyEditText.text.toString()

            if (title.isNotBlank() && body.isNotBlank()) {
                val resultIntent = intent.apply {
                    putExtra("postId", postId)
                    putExtra("title", title)
                    putExtra("body", body)
                }
                setResult(RESULT_OK, resultIntent)
                finish()
            } else {
                Toast.makeText(this, "Усі поля повинні бути заповнені", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
