package com.jveventsplatform.eventsplatformandroidui.ui.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Location(
    val id: Long = 0,
    val name: String = "",
    val address: String = "",
    val city: String = "",
    val postcode: String = ""
) : Parcelable
