package com.michlindev.moviedownloader

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.ktx.BuildConfig
import androidx.lifecycle.lifecycleScope
import androidx.work.*
import com.michlindev.moviedownloader.database.DataBaseHelper
import com.michlindev.moviedownloader.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity() {

    companion object {
        private var DEBUG = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setContentView(binding.root)

        if (!BuildConfig.DEBUG) {
            scheduleWork()
        }

    }

    override fun onPause() {
        super.onPause()

        if (SharedPreferenceHelper.uploadRequired) {
            SharedPreferenceHelper.uploadRequired = false

            lifecycleScope.launch(Dispatchers.IO) {
                val torrents = DataBaseHelper.getAllTorrents()
                FileManager.writeToRssFile(torrents)
                FileManager.uploadFile()
            }
        }
    }

    private fun scheduleWork() {

        var period = TimeUnit.HOURS
        var duration: Long = 3

        if (DEBUG) {
            period = TimeUnit.SECONDS
            duration = 5
        }


        val myConstraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val refreshCpnWork = PeriodicWorkRequest.Builder(SyncWorker::class.java, 3, TimeUnit.HOURS)
            .setInitialDelay(duration, period)
            .setConstraints(myConstraints)
            .addTag(SyncWorker.TAG)
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            SyncWorker.TAG,
            ExistingPeriodicWorkPolicy.REPLACE, refreshCpnWork
        )
    }
}