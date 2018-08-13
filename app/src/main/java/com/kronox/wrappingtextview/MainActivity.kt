package com.kronox.wrappingtextview

import android.support.v7.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val spannableTextView = findViewById<WrappingTextView>(R.id.spannableTextView)

        spannableTextView.setText(getString(R.string.dummy_text))
    }
}
