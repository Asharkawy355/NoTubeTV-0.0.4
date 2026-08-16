package com.ycngmn.notubetv

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.tv.material3.ExperimentalTvMaterial3Api
import com.ycngmn.notubetv.ui.screens.YoutubeWV
import com.ycngmn.notubetv.ui.theme.NoTubeTVTheme

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalTvMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // ✅ FIXED #38: Dynamic layout instead of hardcoded 4K resolution
        // This prevents UI shrinking after login on non-4K screens
        window.setLayout(
            android.view.WindowManager.LayoutParams.MATCH_PARENT,
            android.view.WindowManager.LayoutParams.MATCH_PARENT
        )

        setContent {
            NoTubeTVTheme {
                Box(modifier = Modifier.fillMaxSize()) { YoutubeWV() }
            }
        }
    }
}
