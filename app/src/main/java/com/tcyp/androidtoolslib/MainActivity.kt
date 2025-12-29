package com.tcyp.androidtoolslib

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat.startActivity
import com.tcyp.androidtoolslib.ui.theme.AndroidToolsLibTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AndroidToolsLibTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding),
                        onclick = {
                            startActivity(
                                this@MainActivity,
                                Intent(this@MainActivity, TestActivity::class.java),
                                null
                            )
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String,
             modifier: Modifier = Modifier,
             onclick: () -> Unit = {}) {
    Text(
        text = "Hello $name!",
        modifier = modifier.clickable(onClick = onclick)
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    AndroidToolsLibTheme {
        Greeting("Android")
    }
}