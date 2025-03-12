package com.jveventsplatform.eventsplatformandroidui.ui.eventdetails

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.jveventsplatform.eventsplatformandroidui.databinding.FragmentEventDetailsBinding
import com.jveventsplatform.eventsplatformandroidui.ui.model.Event

class EventDetailFragment : Fragment() {

    private var _binding: FragmentEventDetailsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEventDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.backButton.setOnClickListener {
            Log.d("EventDetailFragment", "Back button clicked")
            findNavController().popBackStack()
        }
        // Handle back button press explicitly
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().popBackStack() // Ensure it pops back to the previous fragment
            }
        })

        // Get event details from arguments
        val event = arguments?.getParcelable<Event>("event")

        event?.let {
            binding.eventTitle.text = it.title
            binding.eventDate.text = "Date: ${it.eventDate}"
            binding.eventDescription.text = it.description
            binding.eventType.text = "Type: ${it.type}"
            binding.eventPrice.text = "Price: ${it.price}"
            binding.eventLocation.text = "Location: ${it.location.name}, ${it.location.city}"
            binding.eventOrganiser.text = "Organised by: ${it.organiser.name}"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
