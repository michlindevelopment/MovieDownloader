package com.michlindev.moviedownloader.database.ref

import androidx.room.*


@Dao
interface LogDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(item: LogEntity): Long

    @Delete
    fun delete(item: LogEntity)

    @Update
    fun update(item: LogEntity)

    @Query("DELETE FROM logentity")
    fun nukeTable()

    @Query("SELECT * FROM logentity WHERE syncedToS3= :syncToS3")
    fun getLogsToSync(syncToS3: Boolean): List<LogEntity>

    @Query("SELECT * FROM logentity WHERE rideName= :rideName")
    fun getLog(rideName: String): LogEntity?

    @Query("SELECT * FROM logentity")
    fun getAllLogs(): List<LogEntity>
}