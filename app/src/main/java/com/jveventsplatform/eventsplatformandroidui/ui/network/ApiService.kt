package com.jveventsplatform.eventsplatformandroidui.ui.network

import com.jveventsplatform.eventsplatformandroidui.ui.model.Event
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {

    @GET("api/v1/events")
    fun getEvents(): Call<List<Event>>

    @POST("api/v1/events")
    fun postEvent(@Body event: Event): Call<Event>
}
