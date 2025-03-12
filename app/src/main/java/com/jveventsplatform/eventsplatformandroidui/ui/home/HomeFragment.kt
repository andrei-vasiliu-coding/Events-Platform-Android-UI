package com.jveventsplatform.eventsplatformandroidui.ui.home

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.jveventsplatform.eventsplatformandroidui.databinding.FragmentHomeBinding
import com.jveventsplatform.eventsplatformandroidui.ui.model.Event
import com.jveventsplatform.eventsplatformandroidui.ui.network.RetrofitClient
import com.jveventsplatform.eventsplatformandroidui.ui.adapters.EventAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import androidx.navigation.fragment.findNavController
import com.jveventsplatform.eventsplatformandroidui.R

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var eventAdapter: EventAdapter
    private var eventList: MutableList<Event> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        setupRecyclerView()
        fetchEvents()
        setupSearchBar()

        return root
    }

    private fun setupRecyclerView() {
        eventAdapter = EventAdapter(eventList) { event ->
            // Handle the event click
            val bundle = Bundle().apply {
                putParcelable("event", event)
            }
            findNavController().navigate(R.id.action_home_to_eventDetails, bundle)
        }
        binding.recyclerViewEvents.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewEvents.adapter = eventAdapter
    }


    private fun fetchEvents() {
        RetrofitClient.apiService.getEvents().enqueue(object : Callback<List<Event>> {
            override fun onResponse(call: Call<List<Event>>, response: Response<List<Event>>) {
                if (response.isSuccessful) {
                    val events = response.body()
                    if (events != null) {
                        eventList.clear()
                        eventList.addAll(events)
                        eventAdapter.notifyDataSetChanged()
                        println("API Response: $events") // Debugging log
                    } else {
                        Toast.makeText(requireContext(), "No events found", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(requireContext(), "Failed to fetch events: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Event>>, t: Throwable) {
                Toast.makeText(requireContext(), "API Error: ${t.message}", Toast.LENGTH_LONG).show()
                println("API Call Failure: ${t.message}") // Debugging log
            }
        })
    }


    private fun setupSearchBar() {
        binding.searchBar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val filteredEvents = eventList.filter { event ->
                    event.title.contains(s.toString(), ignoreCase = true)
                }
                eventAdapter.updateList(filteredEvents)
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}