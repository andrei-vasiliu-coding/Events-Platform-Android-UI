package com.jveventsplatform.eventsplatformandroidui.ui.myevents

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.jveventsplatform.eventsplatformandroidui.R
import com.jveventsplatform.eventsplatformandroidui.databinding.FragmentMyeventsBinding
import com.jveventsplatform.eventsplatformandroidui.ui.adapters.EventAdapter
import com.jveventsplatform.eventsplatformandroidui.ui.model.Event

class MyEventsFragment : Fragment() {

    private var _binding: FragmentMyeventsBinding? = null
    private val binding get() = _binding!!

    private lateinit var eventAdapter: EventAdapter
    private var eventList: MutableList<Event> = mutableListOf()
    private val firestore by lazy { FirebaseFirestore.getInstance() }
    private val auth by lazy { FirebaseAuth.getInstance() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyeventsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        loadUserSignUps()
    }

    private fun setupRecyclerView() {
        // Set up the adapter with a click listener that navigates to EventDetailFragment
        eventAdapter = EventAdapter(eventList) { event ->
            val bundle = Bundle().apply {
                putParcelable("event", event)
            }
            // Navigate to EventDetailFragment using its destination id
            findNavController().navigate(R.id.eventDetailFragment, bundle)
        }
        binding.recyclerViewMyEvents.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewMyEvents.adapter = eventAdapter
    }

    private fun loadUserSignUps() {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            Toast.makeText(requireContext(), "Not logged in", Toast.LENGTH_SHORT).show()
            return
        }

        Log.d("MyEventsFragment", "Current user UID: ${currentUser.uid}")

        firestore.collection("user_signups")
            .whereEqualTo("userId", currentUser.uid)
            .get()
            .addOnSuccessListener { querySnapshot ->
                Log.d("MyEventsFragment", "Documents found: ${querySnapshot.size()}")
                eventList.clear()
                for (document in querySnapshot.documents) {
                    Log.d("MyEventsFragment", "Document ${document.id}: ${document.data}")
                    try {
                        val event = document.toObject(Event::class.java)
                        if (event != null) {
                            eventList.add(event)
                        } else {
                            Log.e("MyEventsFragment", "Conversion returned null for document: ${document.id}")
                        }
                    } catch (e: Exception) {
                        Log.e("MyEventsFragment", "Error converting document ${document.id} to Event: ${e.message}")
                    }
                }
                if (eventList.isEmpty()) {
                    Toast.makeText(requireContext(), "No events found for your account", Toast.LENGTH_SHORT).show()
                }
                eventAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                Log.e("MyEventsFragment", "Error loading sign-ups: ${e.message}", e)
                Toast.makeText(requireContext(), "Failed to load events", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
