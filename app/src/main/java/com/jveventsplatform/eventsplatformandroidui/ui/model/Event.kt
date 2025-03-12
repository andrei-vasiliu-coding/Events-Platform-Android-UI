package com.jveventsplatform.eventsplatformandroidui.ui.model

import com.jveventsplatform.eventsplatformandroidui.ui.model.Location
import com.jveventsplatform.eventsplatformandroidui.ui.model.Organiser
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Event(
    val id: Long,
    val title: String,
    val description: String,
    val type: String,
    val eventDate: String,
    val startTime: String,
    val endTime: String,
    val price: String,
    val location: Location,
    val organiser: Organiser
) : Parcelable
