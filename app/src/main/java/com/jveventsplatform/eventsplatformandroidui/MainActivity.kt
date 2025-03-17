package com.jveventsplatform.eventsplatformandroidui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.jveventsplatform.eventsplatformandroidui.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var signInClient: SignInClient

    // Supabase client variable
    private lateinit var supabase: SupabaseClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Supabase client without DSL block
        initializeSupabase()

        // Initialize Google Sign-In Client
        signInClient = Identity.getSignInClient(this)

        // Setup Bottom Navigation
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        val appBarConfiguration = AppBarConfiguration(
            setOf(R.id.navigation_home, R.id.navigation_my_events, R.id.navigation_profile)
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        binding.navView.setupWithNavController(navController)

        // Handle Google Sign-In
        binding.signInButton.setOnClickListener {
            signInWithGoogle()
        }
    }

    // Initialize Supabase without DSL block
    private fun initializeSupabase() {
        supabase = createSupabaseClient(
            supabaseUrl = "https://wsgjvqakercgynenblbv.supabase.co", // Replace with your Supabase URL
            supabaseKey = "your_public_anon_key" // Replace with your Supabase public anon key
        ) {
            install(Auth, {})
            }
    }

    private fun signInWithGoogle() {
        val signInRequest = BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setServerClientId(getString(R.string.default_web_client_id)) // Use client ID from Google API
                    .setFilterByAuthorizedAccounts(false)
                    .build()
            )
            .build()

        signInClient.beginSignIn(signInRequest)
            .addOnSuccessListener { result ->
                startIntentSenderForResult(
                    result.pendingIntent.intentSender,
                    1001,
                    null, 0, 0, 0, null
                )
            }
            .addOnFailureListener { e ->
                Log.e("Google Sign-In", "Sign-in failed", e)
                Toast.makeText(this, "Google Sign-In Failed", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1001) {
            val credential = signInClient.getSignInCredentialFromIntent(data)
            val idToken = credential.googleIdToken
            if (idToken != null) {
                authenticateWithSupabase(idToken)
            }
        }
    }

    private fun authenticateWithSupabase(idToken: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = supabase.auth.signInWithIdToken(
                    provider = "google",
                    idToken = idToken
                )

                if (response.isSuccess) {
                    val user = response.getOrThrow()
                    Log.d("Supabase Auth", "User: ${user.id}, Email: ${user.email}")
                    runOnUiThread {
                        Toast.makeText(this@MainActivity, "Welcome ${user.email}", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Log.e("Supabase Auth", "Error: ${response.exceptionOrNull()?.message}")
                }
            } catch (e: Exception) {
                Log.e("Supabase Auth", "Exception: ${e.message}")
            }
        }
    }
}
