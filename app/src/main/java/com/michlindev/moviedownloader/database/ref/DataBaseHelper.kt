package com.michlindev.moviedownloader.database.ref

import com.michlindev.moviedownloader.MovieDownloader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


object DataBaseHelper {

    var db: AppDatabase = AppDatabase(MovieDownloader.appContext)

    suspend fun addSegments(items: List<SegmentEntity>) = withContext(Dispatchers.IO) {
        db.segmentDao().insertMany(items)
    }

    /*suspend fun addRide(ride: RideEntity) = withContext(Dispatchers.IO) {

        val id: Long

        if (ride.name.contains(INCIDENT)) {
            val parentRide = db.rideDao().getRideByName(ride.name.replace(INCIDENT, ""))
            if (parentRide == null) {
                ride.incidentId = -1
                id = db.rideDao().insert(ride)
            } else {
                ride.incidentId = parentRide.id!!
                id = db.rideDao().insert(ride)
                parentRide.incidentId = id
                db.rideDao().update(parentRide)
            }
        } else {
            id = db.rideDao().insert(ride)
        }
        id
    }*/

    suspend fun getRide(rideName: String) = withContext(Dispatchers.IO) {
        db.rideDao().getRideByName(rideName)
    }

    /*suspend fun deleteRide(rideName: String,state: RideState) = withContext(Dispatchers.IO) {
        db.rideDao().deleteRideWithState(rideName,state.value)
    }*/

    suspend fun getRide(id: Long) = withContext(Dispatchers.IO) {
        db.rideDao().getRideById(id)
    }
    suspend fun updateRide(ride: RideEntity) = withContext(Dispatchers.IO) {
        db.rideDao().update(ride)
    }

    suspend fun getLastRide() = withContext(Dispatchers.IO) {
        db.rideDao().getLastRide()
    }

    /*suspend fun getRideFromDBWithSegments(id: Long) = withContext(Dispatchers.IO) {
        val rides = db.rideDao().getRideWithSegments(id)
        rides
    }*/

    suspend fun getSegments(id: Long) = withContext(Dispatchers.IO) {
        val rides = db.rideDao().getSegments(id)
        rides
    }

    suspend fun getCoordinates(id: Long) = withContext(Dispatchers.IO) {
        val rides = db.rideDao().getCoordinates(id)
        rides
    }

    suspend fun getAllRidesFromDB() = withContext(Dispatchers.IO) {
        val rides = db.rideDao().getAllRidesStatic()
        rides
    }

    /*suspend fun getAllRidesWithState(state: RideState) = withContext(Dispatchers.IO) {
        val rides = db.rideDao().getAllRidesWithState(state.value)
        rides
    }*/


    suspend fun addLog(log: LogEntity) = withContext(Dispatchers.IO) {
        db.logDao().insert(log)
    }

    suspend fun updateLog(log: LogEntity) = withContext(Dispatchers.IO) {
        db.logDao().update(log)
    }

    suspend fun getLogsToSync() = withContext(Dispatchers.IO) {
        return@withContext db.logDao().getLogsToSync(false)
    }

    suspend fun getLog(rideName: String) = withContext(Dispatchers.IO) {
        return@withContext db.logDao().getLog(rideName)
    }

    /*suspend fun getAllLogs() = withContext(Dispatchers.IO) {
        db.logDao().getAllLogs()
    }*/

    suspend fun getVersion() = withContext(Dispatchers.IO) {
        db.versionDao().getVersionStatic()
    }

    suspend fun updateVersion(version: VersionEntity) = withContext(Dispatchers.IO) {
        db.versionDao().insert(version)
    }

    suspend fun clearVersion() = withContext(Dispatchers.IO) {
        db.versionDao().nukeTable()
    }

    suspend fun deleteRideData(id: Long) = withContext(Dispatchers.IO) {
        db.segmentDao().removeSegmentsForRideId(id)
    }


}



