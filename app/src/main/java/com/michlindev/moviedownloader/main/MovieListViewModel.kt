package com.michlindev.moviedownloader.main

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.storage.FirebaseStorage
import com.michlindev.moviedownloader.DLog
import com.michlindev.moviedownloader.FileManager
import com.michlindev.moviedownloader.MovieDownloader
import com.michlindev.moviedownloader.SingleLiveEvent
import java.io.File
import java.io.FileInputStream

class MovieListViewModel : ViewModel() {
    //Check if file exist on sd
    //Create if not with content
    //Upload it

    //Add fixed data
    //Upload it to firebase

    var createFile = SingleLiveEvent<Any>()
    var uploadFile = SingleLiveEvent<Any>()

    fun createFile() {
        //createFile.call()
        FileManager.createFile()
    }
    fun getMovies() {
        MovieListRepo.getMovies()
    }

    fun uploadFile() {

        val storageReference = FirebaseStorage.getInstance().reference

        val path = MovieDownloader.applicationContext().filesDir.path
        val directory = File("$path/MoviesSync")
        //val file = File(directory, "my1.rss")


        val file1 = Uri.fromFile(File("$path/MoviesSync/my1.rss"))
        val mountainsRef = storageReference.child("my2.rss")
        val uploadTask = mountainsRef.putFile(file1)
        uploadTask.addOnFailureListener {
            // Handle unsuccessful uploads
            DLog.d("Fail $it")
        }.addOnSuccessListener { taskSnapshot ->
            // taskSnapshot.metadata contains file metadata such as size, content-type, etc.
            // ...
            DLog.d("Success")

        }.addOnProgressListener {
            DLog.d("Prog")
        }

        /*val mountainsRef = storageReference.child("my2.rss")
        val stream = FileInputStream(file)
        val uploadTask = mountainsRef.putStream(stream)
        uploadTask.addOnFailureListener {
            // Handle unsuccessful uploads
            DLog.d("Fail $it")
        }.addOnSuccessListener { taskSnapshot ->
            // taskSnapshot.metadata contains file metadata such as size, content-type, etc.
            // ...
            DLog.d("Success")
        }.addOnProgressListener {
            DLog.d("Prog")
        }*/

    }
}