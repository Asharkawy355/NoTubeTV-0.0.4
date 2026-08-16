package com.ycngmn.notubetv.utils

import android.content.Context
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.client.plugins.HttpTimeout
import kotlinx.coroutines.delay

const val SCRIPTS_URL = "https://raw.githubusercontent.com/ycngmn/NoTubeTV/refs/heads/main/assets/userscripts.js"
const val FALLBACK_SCRIPTS_URL = "https://raw.githubusercontent.com/ycngmn/NoTubeTV/main/assets/userscripts.js"

suspend fun fetchScripts(context: Context): String {
    // ✅ PRIORITY 1: Load from local assets (fastest, works offline)
    try {
        return context.assets.open("userscripts.js").bufferedReader().use { it.readText() }
    } catch (_: Exception) {
        // Local file not found, try network
    }

    // ✅ PRIORITY 2: Fetch from network with retry
    val httpClient = HttpClient(OkHttp) {
        install(HttpTimeout) {
            requestTimeoutMillis = 15000
            connectTimeoutMillis = 10000
        }
    }

    repeat(3) { attempt ->
        try {
            val response: HttpResponse = httpClient.get(SCRIPTS_URL)
            return response.body()
        } catch (e: Exception) {
            delay(1000L * (attempt + 1))
        }
    }

    try {
        val response: HttpResponse = httpClient.get(FALLBACK_SCRIPTS_URL)
        return response.body()
    } catch (_: Exception) { }

    // ✅ PRIORITY 3: Ultimate fallback - minimal adblock
    return """
        (function() {
            const style = document.createElement('style');
            style.textContent = `
                .video-ads, .ytp-ad-module, ytm-promoted-video-renderer,
                [class*="ad-"], [id*="ad-"], .ytp-skip-ad-button,
                .ytp-ad-progress-list, .ytp-ad-text,
                ytd-display-ad-renderer, ytd-ad-slot-renderer { display: none !important; }
            `;
            document.head.appendChild(style);
            setInterval(() => {
                const skipBtn = document.querySelector('.ytp-skip-ad-button, .ytp-ad-skip-button');
                if (skipBtn) skipBtn.click();
                const video = document.querySelector('video');
                const adModule = document.querySelector('.video-ads');
                if (video && adModule && adModule.children.length > 0) {
                    video.currentTime = video.duration || 9999;
                }
            }, 500);
        })();
    """.trimIndent()
}
