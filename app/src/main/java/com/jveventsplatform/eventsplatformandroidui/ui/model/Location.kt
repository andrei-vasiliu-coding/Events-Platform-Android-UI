package com.jveventsplatform.eventsplatformandroidui.ui.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Location(
    val id: Long? = null, // set as nullable; default is null
    val name: String = "",
    val address: String = "",
    val city: String = "",
    val postcode: String = ""
) : Parcelable
