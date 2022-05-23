package com.michlindev.moviedownloader.database.ref

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query


@Dao
interface SegmentDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(item: SegmentEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMany(items: List<SegmentEntity>)

    @Query("DELETE FROM segmententity")
    fun nukeTable()

    @Query("SELECT * FROM  segmententity WHERE rideId =  :rideId")
    fun getSegmentsForRideId(rideId: Long): List<SegmentEntity>

    @Query("DELETE FROM segmententity WHERE rideId =  :rideId")
    fun removeSegmentsForRideId(rideId: Long)
}