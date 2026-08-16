package com.ycngmn.notubetv.ui.screens

import android.app.Activity
import android.view.View
import android.view.WindowManager
import android.webkit.CookieManager
import android.webkit.WebSettings
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.multiplatform.webview.web.LoadingState
import com.multiplatform.webview.web.WebView
import com.multiplatform.webview.web.rememberWebViewNavigator
import com.multiplatform.webview.web.rememberWebViewState
import com.ycngmn.notubetv.R
import com.ycngmn.notubetv.ui.YoutubeVM
import com.ycngmn.notubetv.ui.components.UpdateDialog
import com.ycngmn.notubetv.utils.ExitBridge
import com.ycngmn.notubetv.utils.NetworkBridge
import com.ycngmn.notubetv.utils.fetchScripts
import com.ycngmn.notubetv.utils.getUpdate
import com.ycngmn.notubetv.utils.permHandler
import com.ycngmn.notubetv.utils.readRaw

@Composable
fun YoutubeWV(youtubeVM: YoutubeVM = viewModel()) {

    val context = LocalContext.current
    val activity = context as Activity

    val state = rememberWebViewState("https://www.youtube.com/tv")
    val navigator = rememberWebViewNavigator()

    val jsScript = youtubeVM.scriptData
    val updateData = youtubeVM.updateData

    val loadingState = state.loadingState
    val exitTrigger = remember { mutableStateOf(false) }

    BackHandler {
        if (state.loadingState is LoadingState.Finished)
            navigator.evaluateJavaScript(readRaw(context, R.raw.back_bridge))
        else exitTrigger.value = true
    }

    LaunchedEffect(Unit) {
        // ✅ Pass context to fetchScripts for local assets loading
        youtubeVM.setScript(fetchScripts(context))
        getUpdate(context, navigator) { update ->
            if (update != null) youtubeVM.setUpdate(update)
        }
    }

    if (loadingState == LoadingState.Finished && jsScript != null)
        navigator.evaluateJavaScript(jsScript)

    if (updateData != null) UpdateDialog(updateData, navigator)
    if (exitTrigger.value) activity.finish()

    val loading = state.loadingState as? LoadingState.Loading
    if (loading != null) SplashLoading(loading.progress)

    WebView(
        modifier = Modifier.fillMaxSize(),
        state = state,
        navigator = navigator,
        platformWebViewParams = permHandler(context),
        captureBackPresses = false,
        onCreated = { webView ->

            (activity.window).setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT
            )

            val cookieManager = CookieManager.getInstance()
            cookieManager.setAcceptCookie(true)
            cookieManager.setAcceptThirdPartyCookies(webView, true)
            cookieManager.flush()

            state.webSettings.apply {
                // ✅ FIXED #40: Updated UserAgent for Android TV (not PS4)
                // PS4 UA caused buffering issues and limited 4K support
                customUserAgentString = "Mozilla/5.0 (Linux; Android 14; Android TV Build/UP1A.231005.007) Cobalt/25.0"

                isJavaScriptEnabled = true

                androidWebSettings.apply {
                    useWideViewPort = true
                    domStorageEnabled = true
                    hideDefaultVideoPoster = true
                    mediaPlaybackRequiresUserGesture = false

                    // ✅ FIXED #40: Performance optimizations for 4K playback
                    builtInZoomControls = false
                    displayZoomControls = false
                    cacheMode = WebSettings.LOAD_DEFAULT
                    mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                    setRenderPriority(WebSettings.RenderPriority.HIGH)

                    // Enable database and app cache for Leanback
                    databaseEnabled = true
                    setAppCacheEnabled(true)
                    setAppCachePath(context.cacheDir.absolutePath)

                    // Enable hardware acceleration for video
                    setEnableSmoothTransition(true)
                }
            }

            webView.apply {
                addJavascriptInterface(ExitBridge(exitTrigger), "ExitBridge")
                addJavascriptInterface(NetworkBridge(navigator), "NetworkBridge")

                setLayerType(View.LAYER_TYPE_HARDWARE, null)

                // ✅ FIXED #38: Changed from 25% to 100% to prevent UI shrink
                setInitialScale(100)

                isVerticalScrollBarEnabled = false
                isHorizontalScrollBarEnabled = false

                // TV navigation support
                isFocusable = true
                isFocusableInTouchMode = true
            }
        }
    )
}
