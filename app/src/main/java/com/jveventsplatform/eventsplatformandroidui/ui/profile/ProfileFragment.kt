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
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val profileViewModel =
            ViewModelProvider(this).get(ProfileViewModel::class.java)

        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Setup click listener for the sign-in button
        binding.signInButton.setOnClickListener {
            signInWithGoogle()
        }

        // Optionally, observe view model data for other profile info
        profileViewModel.text.observe(viewLifecycleOwner) {
            binding.textNotifications.text = it
        }

        return root
    }

    private fun signInWithGoogle() {
        // Configure Google Sign-In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id)) // Ensure this string resource is defined
            .requestEmail()
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
                    // Sign in success
                    val user = auth.currentUser
                    Log.d("Firebase Auth", "signInWithCredential:success, user: ${user?.email}")
                    Toast.makeText(requireContext(), "Welcome ${user?.displayName}", Toast.LENGTH_SHORT).show()
                    // Optionally update UI with user's info
                } else {
                    Log.w("Firebase Auth", "signInWithCredential:failure", task.exception)
                    Toast.makeText(requireContext(), "Firebase Authentication failed.", Toast.LENGTH_SHORT).show()
                }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
