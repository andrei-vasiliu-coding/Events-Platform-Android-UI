package com.jveventsplatform.eventsplatformandroidui.ui.admin

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.jveventsplatform.eventsplatformandroidui.databinding.FragmentAddEventBinding
import com.jveventsplatform.eventsplatformandroidui.ui.model.Event
import com.jveventsplatform.eventsplatformandroidui.ui.model.Location
import com.jveventsplatform.eventsplatformandroidui.ui.model.Organiser
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
            val title = binding.editTextTitle.text.toString().trim()
            val description = binding.editTextDescription.text.toString().trim()
            val type = binding.editTextType.text.toString().trim()
            val date = binding.editTextDate.text.toString().trim()
            val startTime = binding.editTextStartTime.text.toString().trim()
            val endTime = binding.editTextEndTime.text.toString().trim()
            val price = binding.editTextPrice.text.toString().trim()

            val locationName = binding.editTextLocationName.text.toString().trim()
            val locationAddress = binding.editTextLocationAddress.text.toString().trim()
            val locationCity = binding.editTextLocationCity.text.toString().trim()
            val locationPostcode = binding.editTextLocationPostcode.text.toString().trim()

            val organiserName = binding.editTextOrganiserName.text.toString().trim()
            val organiserEmail = binding.editTextOrganiserEmail.text.toString().trim()
            val organiserPhone = binding.editTextOrganiserPhone.text.toString().trim()

            val newEvent = Event(
                id = null,
                title = title,
                description = description,
                type = type,
                eventDate = date,
                startTime = startTime,
                endTime = endTime,
                price = price,
                location = Location(
                    id = null,
                    name = locationName,
                    address = locationAddress,
                    city = locationCity,
                    postcode = locationPostcode
                ),
                organiser = Organiser(
                    id = null,
                    name = organiserName,
                    email = organiserEmail,
                    phoneNumber = organiserPhone
                )
            )

            RetrofitClient.apiService.postEvent(newEvent)
                .enqueue(object : Callback<Event> {
                    override fun onResponse(call: Call<Event>, response: Response<Event>) {
                        if (response.isSuccessful) {
                            Toast.makeText(requireContext(), "Event created!", Toast.LENGTH_SHORT).show()
                            findNavController().popBackStack()
                        } else {
                            val errorResponse = response.errorBody()?.string()
                            Toast.makeText(requireContext(), "Failed to create event", Toast.LENGTH_SHORT).show()
                            Log.e("AddEventFragment", "Error creating event: $errorResponse")                        }
                    }
                    override fun onFailure(call: Call<Event>, t: Throwable) {
                        Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                        Log.e("AddEventFragment", "Error: ", t)                    }
                })
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
