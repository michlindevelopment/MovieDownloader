package com.michlindev.moviedownloader.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [TorrentEntity::class], version = 1,exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun torrentDao(): TorrentDao


    companion object {
        @Volatile
        private var instance: AppDatabase? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            instance ?: buildDatabase(context).also { instance = it }
        }

       /* private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("CREATE TABLE IF NOT EXISTS LogEntity (localPath TEXT NOT NULL,shaCode TEXT,rideName TEXT NOT NULL,syncedToS3 INTEGER NOT NULL, PRIMARY KEY(`rideName`))")
            }
        }*/


        private fun buildDatabase(context: Context): AppDatabase {
            val builder = Room.databaseBuilder(context, AppDatabase::class.java, "Movies.db")
                //.addMigrations(MIGRATION_1_2)
            return builder.build()
        }
    }
}