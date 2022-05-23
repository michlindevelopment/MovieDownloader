package com.michlindev.moviedownloader.database.ref

import androidx.lifecycle.LiveData
import androidx.room.*


@Dao
interface VersionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(item: VersionEntity)

    @Delete
    fun delete(item: VersionEntity)

    @Update
    fun update(item: VersionEntity)

    @Query("DELETE FROM VersionEntity")
    fun nukeTable()

    @Query("SELECT * FROM VersionEntity where id=1")
    fun getVersionLive(): LiveData<VersionEntity>

    @Query("SELECT * FROM VersionEntity where id=1")
    fun getVersionStatic(): VersionEntity?
}