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
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.Scope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.jveventsplatform.eventsplatformandroidui.R
import com.jveventsplatform.eventsplatformandroidui.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth
    private val firestore by lazy { FirebaseFirestore.getInstance() }
    private val RC_SIGN_IN = 1001

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val profileViewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = FirebaseAuth.getInstance()

        binding.signInButton.setOnClickListener {
            signInWithGoogle()
        }

        binding.signOutButton.setOnClickListener {
            signOut()
        }

        // Set click listener for the admin panel button.
        binding.adminPanelButton.setOnClickListener {
            // Check the user's role when the button is clicked.
            checkUserRole { role ->
                if (role == "admin") {
                    // Navigate to AdminFragment (ensure it's defined in your navigation graph with ID adminFragment)
                    findNavController().navigate(R.id.adminFragment)
                } else {
                    Toast.makeText(requireContext(), "You are not an admin", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Check the user's role and update the Admin Panel button visibility
        checkUserRole { role ->
            binding.adminPanelButton.visibility =
                if (role == "admin") View.VISIBLE else View.GONE
        }

        updateUI()
    }

    override fun onStart() {
        super.onStart()
        updateUI()
        // Ensure the user document exists even if the user was already signed in
        ensureUserDocumentExists()
        // Also check the role to update admin UI
        checkUserRole { role ->
            binding.adminPanelButton.visibility = if (role == "admin") View.VISIBLE else View.GONE
        }
    }

    private fun updateUI() {
        val user = auth.currentUser
        if (user != null) {
            binding.signInButton.visibility = View.GONE
            binding.textProfile.text = "Welcome, ${user.displayName}\nEmail: ${user.email}"
        } else {
            binding.signInButton.visibility = View.VISIBLE
            binding.textProfile.text = "Please sign in"
            binding.adminPanelButton.visibility = View.GONE
        }
    }

    private fun signInWithGoogle() {
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
                    Log.d("Firebase Auth", "signInWithCredential: success, user: ${user?.email}")
                    Toast.makeText(requireContext(), "Welcome ${user?.displayName}", Toast.LENGTH_SHORT).show()
                    ensureUserDocumentExists()
                    // Re-check user role after sign-in
                    checkUserRole { role ->
                        binding.adminPanelButton.visibility =
                            if (role == "admin") View.VISIBLE else View.GONE
                    }
                } else {
                    Log.w("Firebase Auth", "signInWithCredential: failure", task.exception)
                    Toast.makeText(requireContext(), "Firebase Authentication failed.", Toast.LENGTH_SHORT).show()
                }
                updateUI()
            }
    }

    private fun ensureUserDocumentExists() {
        val user = auth.currentUser ?: run {
            Log.d("ProfileFragment", "No user is currently signed in")
            return
        }
        Log.d("ProfileFragment", "Ensuring document exists for UID: ${user.uid}")
        val userDocRef = firestore.collection("users").document(user.uid)
        userDocRef.get().addOnSuccessListener { document ->
            if (!document.exists()) {
                val userData = hashMapOf(
                    "role" to "user",
                    "email" to user.email,
                    "displayName" to user.displayName
                )
                userDocRef.set(userData)
                    .addOnSuccessListener {
                        Log.d("ProfileFragment", "User document created for ${user.uid}")
                    }
                    .addOnFailureListener { e ->
                        Log.e("ProfileFragment", "Error creating user document", e)
                    }
            }
        }
    }

    // Helper function to check the user's role
    private fun checkUserRole(onRoleFetched: (String) -> Unit) {
        val user = auth.currentUser
        if (user != null) {
            firestore.collection("users").document(user.uid)
                .get()
                .addOnSuccessListener { document ->
                    val role = document.getString("role") ?: "user"
                    Log.d("ProfileFragment", "User role: $role")
                    onRoleFetched(role)
                }
                .addOnFailureListener { e ->
                    Log.e("ProfileFragment", "Error checking user role", e)
                    onRoleFetched("user")
                }
        } else {
            onRoleFetched("user")
        }
    }

    // Sign out function to log out the user
    private fun signOut() {
        // Sign out from Firebase Auth
        auth.signOut()

        // Optionally, sign out from Google as well:
        val googleSignInClient = GoogleSignIn.getClient(requireActivity(),
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build())
        googleSignInClient.signOut()

        // Update the UI after sign-out
        updateUI()
        Toast.makeText(requireContext(), "Signed out", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
