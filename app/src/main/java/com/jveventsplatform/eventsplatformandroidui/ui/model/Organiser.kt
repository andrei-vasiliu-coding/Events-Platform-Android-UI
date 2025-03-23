package com.jveventsplatform.eventsplatformandroidui.ui.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Organiser(
    val id: Long? = null, // set as nullable; default is null
    val name: String = "",
    val email: String = "",
    val phoneNumber: String = ""
) : Parcelable