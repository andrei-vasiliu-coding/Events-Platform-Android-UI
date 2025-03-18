package com.jveventsplatform.eventsplatformandroidui.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ProfileViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is the My Events Fragment"
    }
    val text: LiveData<String> = _text
}