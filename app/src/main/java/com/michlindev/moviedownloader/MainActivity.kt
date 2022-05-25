package com.michlindev.moviedownloader

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.michlindev.moviedownloader.database.DataBaseHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
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
}