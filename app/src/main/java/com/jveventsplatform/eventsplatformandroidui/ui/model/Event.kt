package com.jveventsplatform.eventsplatformandroidui.model

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
)
