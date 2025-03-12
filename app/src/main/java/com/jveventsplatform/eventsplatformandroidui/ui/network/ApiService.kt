package com.jveventsplatform.eventsplatformandroidui.ui.network

import com.jveventsplatform.eventsplatformandroidui.ui.model.Event
import retrofit2.Call
import retrofit2.http.GET

interface ApiService {
    @GET("api/v1/events") // Adjust if needed
    fun getEvents(): Call<List<Event>>
}
