package com.michlindev.moviedownloader.debug

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.google.firebase.storage.FirebaseStorage
import com.michlindev.moviedownloader.DLog
import com.michlindev.moviedownloader.MovieDownloader
import com.michlindev.moviedownloader.SingleLiveEvent
import java.io.File

class DebugViewModel : ViewModel() {

    enum class NotificationType {
        SINGLE, MULTIPLE
    }

    var notification = SingleLiveEvent<NotificationType>()


    fun createFile() {
        //FileManager.createFile()
    }

    fun getMovies() {
        DebugRepo.getMovies()
    }

    fun notificationSingle() {
            notification.postValue(NotificationType.SINGLE)
    }

    fun notificationMultiple() {
        notification.postValue(NotificationType.MULTIPLE)
    }

    fun crashTest() {
        throw RuntimeException("Test Crash")
    }


    fun uploadFile() {

        val storageReference = FirebaseStorage.getInstance().reference

        val path = MovieDownloader.appContext.filesDir.path
        //val directory = File("$path/MoviesSync")
        //val file = File(directory, "my1.rss")


        val file1 = Uri.fromFile(File("$path/MoviesSync/my1.rss"))
        val mountainsRef = storageReference.child("my2.rss")
        val uploadTask = mountainsRef.putFile(file1)
        uploadTask.addOnFailureListener {
            // Handle unsuccessful uploads
            DLog.d("Fail $it")
        }.addOnSuccessListener {
            // taskSnapshot.metadata contains file metadata such as size, content-type, etc.
            // ...
            DLog.d("Success")

        }.addOnProgressListener {
            DLog.d("Prog")
        }

    }
}