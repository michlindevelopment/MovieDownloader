package com.michlindev.moviedownloader.database.ref

import androidx.room.Entity
import androidx.room.PrimaryKey


enum class VideoState(val value: Int) {
    NONE(0),
    PRESENT(1),
    DOWNLOADED(2),
}

@Entity
data class SegmentEntity(
    var rideId: Long?,
    var frameId: Int?,
    var time: Float?,
    var roll: Float?,
    var lat: Double?,
    var lon: Double?,
    var alt: Double?,
    var gpsSts: Int?,
    var speed: Float?,
    var alerts: Int?
) {
    @PrimaryKey(autoGenerate = true)
    var id: Long? = null

}


@Entity
data class RideEntity(
    val name: String,
    val time: Long,
    val rightAlerts: Int,
    val leftAlerts: Int,
    val distanceAlerts: Int,
    val collisionAlerts: Int,

    var distance: Float,
    var avgSpeed: Float,
    var maxAngle: Float,
    var maxSpeed: Float,
    var state: Int,
    var frontVideoState: Int,
    var rearVideoState: Int,
    var incidentId: Long?,
    var unit: String
) {
    @PrimaryKey(autoGenerate = true)
    var id: Long? = null

    /*constructor(name: String, time: Long, state: Int) : this(name, time, 0, 0, 0, 0, 0f, 0f, 0f, 0f, state, VideoState.NONE.value, VideoState.NONE.value, null,"")
    constructor(summary: RideSummary, state: RideState, frontVideoState: VideoState, rearVideoState: VideoState, unit: String) : this(
        summary.name,
        summary.time,
        summary.rightAlerts,
        summary.leftAlerts,
        summary.distanceAlerts,
        summary.collisionAlerts,
        summary.distance,
        summary.avgSpeed,
        summary.maxAngle,
        summary.maxSpeed,
        state.value,
        frontVideoState.value,
        rearVideoState.value,
        null,
        unit,

    )*/

}

@Entity
data class LogEntity(
    @PrimaryKey
    var rideName: String,
    var localPath: String,
    var syncedToS3: Boolean,
    var shaCode: String?,
    var fwdVideoShaCode: String?,
    var rearVideoShaCode: String?
)


@Entity
data class VersionEntity(
    @PrimaryKey
    var id: Int,
    var version: String,
    var osVersion: String,
    var asVersion: String,
    var mtVersion: String,
    var path: String,
    var bucket: String,
    var versionDescription: String,
    var status: String

)



