package com.example.login_menu

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.login_menu.database.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [LoginFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class LoginFragment : Fragment() {
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_login, container, false)
        val emailEditText = rootView.findViewById<EditText>(R.id.EmailEditText)
        val passwordEditText = rootView.findViewById<EditText>(R.id.passwordEditText)
        val loginButton = rootView.findViewById<Button>(R.id.loginButton)

        loginButton.setOnClickListener {
            Log.d("LoginFragment", "Button Clicked")
            val email = emailEditText.text.toString().trim() // Remove leading and trailing spaces
            val password = passwordEditText.text.toString()

            // Validate email format
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                showToast("Invalid email address format")
                Log.w("LoginFragment", "Invalid email address format")
                return@setOnClickListener
            }

            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(requireActivity()) { task ->
                    // Inside the if (task.isSuccessful) block
                    if (task.isSuccessful) {
                        Log.d("LoginFragment", "Authentication success")

                        // Fetch user data from Firestore to determine role
                        fetchUserDataAndRedirect(email)

                        // Log the username to check if it's correct
                        Log.d("LoginFragment", "Username: $email")
                    } else {
                        Log.w("LoginFragment", "Authentication failed", task.exception)
                        showToast("Authentication failed. Please check your credentials.")
                    }
                }
        }

        return rootView
    }

    private fun fetchUserDataAndRedirect(email: String) {
        // Query Firestore to get user data
        db.collection("User")
            .whereEqualTo("email", email)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    // User found in Firestore
                    val user = documents.documents[0].toObject(User::class.java)

                    // Determine user role (admin or user)
                    val role = user?.role ?: ""

                    Log.d("LoginFragment", "Role: $role")
                    val intent = if (role == "admin") {
                        Log.d("LoginFragment", "Redirecting to AdminActivity")
                        Intent(activity, AdminActivity::class.java)
                    } else {
                        Log.d("LoginFragment", "Redirecting to Homepage")
                        Intent(activity, Homepage::class.java)
                    }

                    // Pass the email and user role to the next activity
                    intent.putExtra("USERNAME", email)
                    intent.putExtra("ROLE", role)
                    startActivity(intent)
                } else {
                    Log.w("LoginFragment", "User data not found in Firestore")
                    showToast("User data not found. Please try again.")
                }
            }
            .addOnFailureListener { e ->
                Log.e("LoginFragment", "Error fetching user data", e)
                showToast("Error fetching user data. Please try again.")
            }
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            RegisterFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
