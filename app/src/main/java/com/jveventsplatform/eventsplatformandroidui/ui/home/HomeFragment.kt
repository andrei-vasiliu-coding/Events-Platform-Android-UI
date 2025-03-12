package com.jveventsplatform.eventsplatformandroidui.ui.home

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.jveventsplatform.eventsplatformandroidui.databinding.FragmentHomeBinding
import com.jveventsplatform.eventsplatformandroidui.model.Event
import com.jveventsplatform.eventsplatformandroidui.ui.adapters.EventAdapter

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var eventAdapter: EventAdapter
    private var eventList: List<Event> = listOf( // Sample data, replace with real API data
        Event("Lady Gaga Concert", "01-07-2025"),
        Event("Tech Conference 2025", "15-08-2025"),
        Event("Yoga Meetup", "10-09-2025"),
        Event("Jazz Night", "20-09-2025"),
        Event("Art Festival", "05-10-2025")
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Set up RecyclerView
        setupRecyclerView()

        // Set up search functionality
        setupSearchBar()

        return root
    }

    private fun setupRecyclerView() {
        eventAdapter = EventAdapter(eventList)
        binding.recyclerViewEvents.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewEvents.adapter = eventAdapter
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
