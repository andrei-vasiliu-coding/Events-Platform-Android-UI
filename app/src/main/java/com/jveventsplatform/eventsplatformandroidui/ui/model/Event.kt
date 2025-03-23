package com.jveventsplatform.eventsplatformandroidui.ui.model

import com.jveventsplatform.eventsplatformandroidui.ui.model.Location
import com.jveventsplatform.eventsplatformandroidui.ui.model.Organiser
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Event(
    val id: Long? = null, // set as nullable; default is null
    val title: String = "",
    val description: String = "",
    val type: String = "",
    val eventDate: String = "",
    val startTime: String = "",
    val endTime: String = "",
    val price: String = "",
    val location: Location = Location(),
    val organiser: Organiser = Organiser()
) : Parcelable

fun Event.toMap(): Map<String, Any?> {
    return mapOf(
        "id" to id,
        "title" to title,
        "description" to description,
        "type" to type,
        "eventDate" to eventDate,
        "startTime" to startTime,
        "endTime" to endTime,
        "price" to price,
        "location" to mapOf(
            "id" to location.id,
            "name" to location.name,
            "address" to location.address,
            "city" to location.city,
            "postcode" to location.postcode
        ),
        "organiser" to mapOf(
            "id" to organiser.id,
            "name" to organiser.name,
            "email" to organiser.email,
            "phoneNumber" to organiser.phoneNumber
        )
    )
}
