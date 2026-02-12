package com.example.mypageing

import android.os.Bundle
import android.view.LayoutInflater
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import com.example.mypageing.databinding.MainLayoutBinding

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val binding = MainLayoutBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)
    }
}
