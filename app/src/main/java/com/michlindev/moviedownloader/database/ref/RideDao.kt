package com.michlindev.moviedownloader.database.ref

import androidx.lifecycle.LiveData
import androidx.room.*


@Dao
interface RideDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(item: RideEntity): Long

    @Delete
    fun delete(item: RideEntity)

    @Update
    fun update(item: RideEntity)

    @Query("DELETE FROM rideentity")
    fun nukeTable()

    @Transaction
    @Query("SELECT * FROM rideentity")
    fun getAllRidesWithSegments(): List<RideWithSegments>

    @Transaction
    @Query("SELECT * FROM rideentity WHERE id =  :id")
    fun getRideWithSegments(id: Long): RideWithSegments

    @Query("SELECT * FROM segmententity WHERE rideId =  :id")
    fun getSegments(id: Long): List<SegmentEntity>

    @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
    @Query("SELECT lon,lat FROM segmententity WHERE rideId =  :id")
    fun getCoordinates(id: Long): List<SegmentEntity>

    @Query("SELECT * FROM rideentity ORDER BY time DESC")
    fun getAllRidesLive(): LiveData<List<RideEntity>>

    @Query("SELECT * FROM rideentity ORDER BY time DESC")
    fun getAllRidesStatic(): List<RideEntity>

    @Query("SELECT * FROM rideentity where state=:state")
    fun getAllRidesWithState(state: Int): List<RideEntity>

    @Query("DELETE FROM rideentity where state=:state and name = :rideName")
    fun deleteRideWithState(rideName: String, state: Int)

    @Query("SELECT * FROM rideentity where name = :rideName LIMIT 1")
    fun getRideByName(rideName: String): RideEntity?

    @Query("SELECT * FROM rideentity where id = :id LIMIT 1")
    fun getRideById(id: Long): RideEntity?

    @Query("SELECT * FROM rideentity where time = (select MAX(time) from rideentity)")
    fun getLastRide(): RideEntity?

}