package com.example.wordalert

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.work.*
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    private val url = "https://sexbam58.top/index.php?mid=sschkiss&category=12782286"
    private lateinit var keyword: EditText
    private lateinit var interval: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createChannel()
        if (Build.VERSION.SDK_INT >= 33) ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), 10)

        val root = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setPadding(40,40,40,40) }
        root.addView(TextView(this).apply { text = "페이지 단어 알림"; textSize = 24f })
        root.addView(TextView(this).apply { text = "감시 URL\n$url"; setPadding(0,20,0,20) })
        keyword = EditText(this).apply { hint = "감시할 단어 (예: 김이슬)" }
        root.addView(keyword)
        interval = Spinner(this).apply { adapter = ArrayAdapter(this@MainActivity, android.R.layout.simple_spinner_dropdown_item, arrayOf("15분", "30분", "1시간", "3시간")) }
        root.addView(interval)
        val start = Button(this).apply { text = "감시 시작" }
        val stop = Button(this).apply { text = "감시 중지" }
        val test = Button(this).apply { text = "지금 확인" }
        root.addView(start); root.addView(stop); root.addView(test)
        root.addView(TextView(this).apply { text = "\n※ Android 백그라운드 정책상 주기 작업은 정확한 시각에 실행되지 않을 수 있습니다. WorkManager 최소 주기는 15분입니다." })
        setContentView(root)

        start.setOnClickListener {
            val word = keyword.text.toString().trim()
            if (word.isEmpty()) { Toast.makeText(this, "단어를 입력해 주세요.", Toast.LENGTH_SHORT).show(); return@setOnClickListener }
            getSharedPreferences("settings", Context.MODE_PRIVATE).edit().putString("keyword", word).apply()
            val minutes = when(interval.selectedItemPosition) { 0 -> 15L; 1 -> 30L; 2 -> 60L; else -> 180L }
            val req = PeriodicWorkRequestBuilder<PageCheckWorker>(minutes, TimeUnit.MINUTES).build()
            WorkManager.getInstance(this).enqueueUniquePeriodicWork("page-check", ExistingPeriodicWorkPolicy.UPDATE, req)
            Toast.makeText(this, "감시를 시작했습니다.", Toast.LENGTH_SHORT).show()
        }
        stop.setOnClickListener { WorkManager.getInstance(this).cancelUniqueWork("page-check"); Toast.makeText(this,"감시를 중지했습니다.",Toast.LENGTH_SHORT).show() }
        test.setOnClickListener { PageCheckWorker.runNow(this); Toast.makeText(this,"확인 요청을 보냈습니다.",Toast.LENGTH_SHORT).show() }
    }

    private fun createChannel() {
        if (Build.VERSION.SDK_INT >= 26) getSystemService(NotificationManager::class.java).createNotificationChannel(NotificationChannel("word", "단어 알림", NotificationManager.IMPORTANCE_HIGH))
    }
}
