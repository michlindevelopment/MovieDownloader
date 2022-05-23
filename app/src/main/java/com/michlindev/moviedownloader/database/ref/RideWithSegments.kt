package com.michlindev.moviedownloader.database.ref

import androidx.room.Embedded
import androidx.room.Relation


data class RideWithSegments(

    @Embedded val ride: RideEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "rideId"
    )
    val segments: List<SegmentEntity>
)
