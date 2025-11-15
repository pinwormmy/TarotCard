package com.pinwormmy.tarotcard

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.pinwormmy.tarotcard.data.TarotRepository
import com.pinwormmy.tarotcard.navigation.TarotNavGraph
import com.pinwormmy.tarotcard.ui.theme.TarotcardTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val repository = TarotRepository(applicationContext)
        Log.d("TEST", "Codex working!")
        setContent {
            TarotcardTheme {
                TarotNavGraph(repository = repository)
            }
        }
    }
}

// 커밋테스트용