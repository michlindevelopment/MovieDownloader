package com.michlindev.moviedownloader.database

import androidx.room.*


@Dao
interface TorrentDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(item: TorrentEntity): Long

    @Delete
    fun delete(item: TorrentEntity)

    @Update
    fun update(item: TorrentEntity)

    @Query("DELETE FROM torrententity")
    fun nukeTable()

    @Query("SELECT * FROM torrententity")
    fun getTorrents(): List<TorrentEntity>
}