package com.ycngmn.notubetv.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Text
import com.ycngmn.notubetv.ui.UpdateViewModel

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun UpdateAppScreen(tagName: String, downloadUrl: String, vm: UpdateViewModel = viewModel()) {
    val progress = vm.downloadProgress.collectAsState()
    val context = LocalContext.current
    val isShowDialog = remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        vm.downloadAndInstall(context, downloadUrl, tagName, isShowDialog)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0B0B0B)),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Downloading NoTubeTV $tagName...",
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(24.dp))
            CircularProgressIndicator(
                progress = { progress.value / 100f },
                modifier = Modifier.size(80.dp),
                color = Color(0xFFFF0000),
                trackColor = Color.DarkGray
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "${progress.value}%",
                color = Color.White,
                fontSize = 20.sp
            )
        }
    }
}
