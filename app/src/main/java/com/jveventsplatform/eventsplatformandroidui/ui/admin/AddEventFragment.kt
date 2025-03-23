package com.jveventsplatform.eventsplatformandroidui.ui.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.jveventsplatform.eventsplatformandroidui.databinding.FragmentAddEventBinding
import com.jveventsplatform.eventsplatformandroidui.ui.model.Event
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.jveventsplatform.eventsplatformandroidui.ui.network.RetrofitClient

class AddEventFragment : Fragment() {

    private var _binding: FragmentAddEventBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddEventBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonSubmitEvent.setOnClickListener {
            // Collect data from form
            val title = binding.editTextTitle.text.toString().trim()
            val description = binding.editTextDescription.text.toString().trim()
            val date = binding.editTextDate.text.toString().trim()
            val startTime = binding.editTextStartTime.text.toString().trim()
            val endTime = binding.editTextEndTime.text.toString().trim()
            val locationName = binding.editTextLocation.text.toString().trim()

            // You need to construct your Event object.
            // Here, we assume Event has an appropriate constructor and that you provide default values for other fields.
            val newEvent = Event(
                id = 0,  // If your backend generates an ID, you can leave this as 0 or null.
                title = title,
                description = description,
                type = "",  // Set type if needed.
                eventDate = date,
                startTime = startTime,
                endTime = endTime,
                price = "", // Set price if needed.
                location = com.jveventsplatform.eventsplatformandroidui.ui.model.Location(
                    id = 0,
                    name = locationName,
                    address = "",
                    city = "",
                    postcode = ""
                ),
                organiser = com.jveventsplatform.eventsplatformandroidui.ui.model.Organiser(
                    id = 0,
                    name = "",
                    email = "",
                    phoneNumber = ""
                )
            )

            // Send the event to your backend via Retrofit POST
            RetrofitClient.apiService.postEvent(newEvent)
                .enqueue(object : Callback<Event> {
                    override fun onResponse(call: Call<Event>, response: Response<Event>) {
                        if (response.isSuccessful) {
                            Toast.makeText(requireContext(), "Event created!", Toast.LENGTH_SHORT).show()
                            // Navigate back or clear the form as needed
                        } else {
                            Toast.makeText(requireContext(), "Failed to create event", Toast.LENGTH_SHORT).show()
                        }
                    }
                    override fun onFailure(call: Call<Event>, t: Throwable) {
                        Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                    }
                })
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
