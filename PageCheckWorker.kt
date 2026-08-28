package com.example.wordalert

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.work.*
import java.net.HttpURLConnection
import java.net.URL
import java.security.MessageDigest

class PageCheckWorker(appContext: Context, params: WorkerParameters) : Worker(appContext, params) {
    companion object {
        private const val URL_STRING = "https://sexbam58.top/index.php?mid=sschkiss&category=12782286"
        fun runNow(context: Context) {
            WorkManager.getInstance(context).enqueue(OneTimeWorkRequestBuilder<PageCheckWorker>().build())
        }
        private fun hash(s: String): String = MessageDigest.getInstance("SHA-256").digest(s.toByteArray()).joinToString("") { "%02x".format(it) }
    }

    override fun doWork(): Result {
        val prefs = applicationContext.getSharedPreferences("settings", Context.MODE_PRIVATE)
        val word = prefs.getString("keyword", "") ?: ""
        if (word.isBlank()) return Result.success()
        return try {
            val conn = URL(URL_STRING).openConnection() as HttpURLConnection
            conn.connectTimeout = 15000; conn.readTimeout = 15000
            conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Android) WordAlert")
            val html = conn.inputStream.bufferedReader(Charsets.UTF_8).use { it.readText() }
            conn.disconnect()

            val text = html.replace(Regex("<script[\\s\\S]*?</script>|<style[\\s\\S]*?</style>"), " ")
                .replace(Regex("<[^>]+>"), " ")
                .replace(Regex("\\s+"), " ")
            val matches = Regex(".{0,100}" + Regex.escape(word) + ".{0,100}", RegexOption.IGNORE_CASE).findAll(text).map { it.value.trim() }.toList()
            if (matches.isEmpty()) return Result.success()

            val current = matches.map(::hash).toSet()
            val old = prefs.getStringSet("seen", emptySet()) ?: emptySet()
            val newOnes = current - old
            if (old.isNotEmpty() && newOnes.isNotEmpty()) notifyUser(word, matches.first())
            prefs.edit().putStringSet("seen", current).apply()
            Result.success()
        } catch (_: Exception) { Result.retry() }
    }

    private fun notifyUser(word: String, snippet: String) {
        val intent = Intent(Intent.ACTION_VIEW, android.net.Uri.parse(URL_STRING))
        val pi = PendingIntent.getActivity(applicationContext, 1, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
        val notification = NotificationCompat.Builder(applicationContext, "word")
            .setSmallIcon(android.R.drawable.ic_dialog_info).setContentTitle("단어 발견: $word")
            .setContentText(snippet.take(100)).setStyle(NotificationCompat.BigTextStyle().bigText(snippet))
            .setAutoCancel(true).setContentIntent(pi).setPriority(NotificationCompat.PRIORITY_HIGH).build()
        applicationContext.getSystemService(android.app.NotificationManager::class.java).notify(1001, notification)
    }
}
