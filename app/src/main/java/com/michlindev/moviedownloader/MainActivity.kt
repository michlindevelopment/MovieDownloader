package com.michlindev.moviedownloader

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.work.*
import com.michlindev.moviedownloader.database.DataBaseHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity() {

    companion object {
        private var DEBUG = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        scheduleWork()
    }

    override fun onPause() {
        super.onPause()

        DLog.d("Pausing")
        //TODO move this to view model
        DLog.d("Writing")
        CoroutineScope(Dispatchers.IO).launch {
            //Get list from DB
            val torrents = DataBaseHelper.getAllTorrents()
            FileManager.writeToRssFile(torrents)
            FileManager.uploadFile()
        }
    }

    private fun scheduleWork() {

        var perioud = TimeUnit.HOURS
        var duration: Long = 3

        if (DEBUG) {
            perioud = TimeUnit.SECONDS
            duration = 5
        }


        val myConstraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val refreshCpnWork = PeriodicWorkRequest.Builder(SyncWorker::class.java, 3, TimeUnit.HOURS)
            .setInitialDelay(duration, perioud)
            .setConstraints(myConstraints)
            .addTag(SyncWorker.TAG)
            .build()


        //TODO set to keep
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            SyncWorker.TAG,
            ExistingPeriodicWorkPolicy.REPLACE, refreshCpnWork
        )
    }
}