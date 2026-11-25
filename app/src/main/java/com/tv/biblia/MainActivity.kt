package com.tv.biblia

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.tv.biblia.theme.BibliaTVTheme
import com.tv.biblia.ui.AppBiblia

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {

            BibliaTVTheme {

                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    AppBiblia()
                }
            }
        }
    }
}
