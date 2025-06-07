package com.example.myapplication

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Lấy view từ layout với tên bạn đã đặt
        val input1: EditText = findViewById(R.id.input1)
        val btnCheck: Button = findViewById(R.id.btnCheck)
        val Range: TextView = findViewById(R.id.Range)

        btnCheck.setOnClickListener {
            val email = input1.text.toString().trim()

            if (email.isEmpty()) {
                Range.text = "Email không hợp lệ"
                Range.setTextColor(resources.getColor(android.R.color.holo_red_dark))
            } else if (!email.contains("@")) {
                Range.text = "Email không đúng định dạng"
                Range.setTextColor(resources.getColor(android.R.color.holo_red_dark))
            } else {
                Range.text = "Bạn đã nhập email hợp lệ"
                Range.setTextColor(resources.getColor(android.R.color.holo_blue_dark))
            }
        }
    }
}
