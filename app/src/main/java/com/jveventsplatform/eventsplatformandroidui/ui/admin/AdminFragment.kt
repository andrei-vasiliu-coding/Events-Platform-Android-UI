package com.jveventsplatform.eventsplatformandroidui.ui.admin

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.FirebaseFirestore
import com.jveventsplatform.eventsplatformandroidui.R

class AdminFragment : Fragment() {

    private val firestore: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_admin, container, false)
        val emailEditText: EditText = view.findViewById(R.id.editTextEmail)
        val updateButton: Button = view.findViewById(R.id.buttonUpdateRole)

        updateButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            if (email.isNotEmpty()) {
                updateUserRole(email, "admin")
            } else {
                Toast.makeText(requireContext(), "Enter an email address", Toast.LENGTH_SHORT).show()
            }
        }
        return view
    }

    private fun updateUserRole(email: String, role: String) {
        // First, get the user document using the email.
        firestore.collection("users")
            .whereEqualTo("email", email)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    val doc = querySnapshot.documents[0]
                    firestore.collection("users").document(doc.id)
                        .update("role", role)
                        .addOnSuccessListener {
                            Toast.makeText(requireContext(), "User role updated", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(requireContext(), "Failed to update role", Toast.LENGTH_SHORT).show()
                            Log.e("AdminFragment", "Error updating role", e)
                        }
                } else {
                    Toast.makeText(requireContext(), "User not found", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Error fetching user", Toast.LENGTH_SHORT).show()
                Log.e("AdminFragment", "Error fetching user", e)
            }
    }
}
