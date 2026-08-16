package com.ycngmn.notubetv.utils

import android.webkit.JavascriptInterface
import androidx.compose.runtime.MutableState

class ExitBridge(private val exitTrigger: MutableState<Boolean>) {
    @JavascriptInterface
    fun exit() {
        exitTrigger.value = true
    }
}
