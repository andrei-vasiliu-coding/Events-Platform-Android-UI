package com.jveventsplatform.eventsplatformandroidui.ui.eventdetails

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.jveventsplatform.eventsplatformandroidui.databinding.FragmentEventDetailsBinding
import com.jveventsplatform.eventsplatformandroidui.ui.model.Event
import com.jveventsplatform.eventsplatformandroidui.ui.model.toMap

class EventDetailFragment : Fragment() {

    private var _binding: FragmentEventDetailsBinding? = null
    private val binding get() = _binding!!

    // Firebase Firestore and Auth instances
    private val firestore: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }
    private val auth = FirebaseAuth.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEventDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set up back button behavior
        binding.backButton.setOnClickListener {
            Log.d("EventDetailFragment", "Back button clicked")
            findNavController().popBackStack()
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().popBackStack()
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

        // Set up the sign-up button to store full event data
        binding.signUpButton.setOnClickListener {
            if (event != null) {
                signUpForEvent(event)
            } else {
                Toast.makeText(requireContext(), "Event details missing", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun signUpForEvent(event: Event) {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            Toast.makeText(requireContext(), "Please log in to sign up", Toast.LENGTH_SHORT).show()
            return
        }
        // Convert the event to a map and add the current user's UID
        val signUpData = event.toMap().toMutableMap()
        signUpData["userId"] = currentUser.uid

        firestore.collection("user_signups")
            .add(signUpData)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Signed up for event!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Log.e("EventDetailFragment", "Error signing up for event", e)
                Toast.makeText(requireContext(), "Failed to sign up for event", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
