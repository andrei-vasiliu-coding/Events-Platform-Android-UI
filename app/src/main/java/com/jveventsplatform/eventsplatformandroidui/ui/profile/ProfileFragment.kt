package com.jveventsplatform.eventsplatformandroidui.ui.profile

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.Scope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.jveventsplatform.eventsplatformandroidui.R
import com.jveventsplatform.eventsplatformandroidui.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth
    private val RC_SIGN_IN = 1001

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val profileViewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Set click listener for the sign-in button
        binding.signInButton.setOnClickListener {
            signInWithGoogle()
        }

        // Update the UI based on the current sign-in state
        updateUI()
    }

    override fun onStart() {
        super.onStart()
        updateUI()
    }

    private fun updateUI() {
        val user = auth.currentUser
        if (user != null) {
            // User is signed in, hide the sign-in button and display user's info
            binding.signInButton.visibility = View.GONE
            binding.textProfile.text = "Welcome, ${user.displayName}\nEmail: ${user.email}"
        } else {
            // No user is signed in, show the sign-in button and prompt the user
            binding.signInButton.visibility = View.VISIBLE
            binding.textProfile.text = "Please sign in"
        }
    }

    private fun signInWithGoogle() {
        // Configure Google Sign-In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .requestScopes(Scope("https://www.googleapis.com/auth/calendar"))
            .build()
        val googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(Exception::class.java)
                firebaseAuthWithGoogle(account?.idToken ?: "")
            } catch (e: Exception) {
                Log.w("Google Sign-In", "Google sign in failed", e)
                Toast.makeText(requireContext(), "Google sign in failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    Log.d("Firebase Auth", "signInWithCredential:success, user: ${user?.email}")
                    Toast.makeText(requireContext(), "Welcome ${user?.displayName}", Toast.LENGTH_SHORT).show()
                } else {
                    Log.w("Firebase Auth", "signInWithCredential:failure", task.exception)
                    Toast.makeText(requireContext(), "Firebase Authentication failed.", Toast.LENGTH_SHORT).show()
                }
                updateUI() // Update the UI after authentication
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
