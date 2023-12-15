package com.example.login_menu

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.login_menu.database.User
import com.example.login_menu.databinding.FragmentRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore  // Import this line
import java.security.MessageDigest

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class RegisterFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding: FragmentRegisterBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore  // Declare db at the class level

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()  // Initialize db
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_register, container, false)
        val emailEditText = rootView.findViewById<EditText>(R.id.emailEditText)
        val usernameEditText = rootView.findViewById<EditText>(R.id.usernameEditText)
        val passwordEditText = rootView.findViewById<EditText>(R.id.passwordEditText)
        val confirmPasswordEditText = rootView.findViewById<EditText>(R.id.confirmpass)
        val roleSpinner = rootView.findViewById<Spinner>(R.id.role)
        val registerButton = rootView.findViewById<Button>(R.id.registerButton)

        // Set up the spinner with the list of roles
        val roles = resources.getStringArray(R.array.role)
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, roles)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        roleSpinner.adapter = adapter

        registerButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val username = usernameEditText.text.toString()
            val password = passwordEditText.text.toString()
            val confirmPassword = confirmPasswordEditText.text.toString()
            val selectedRole = roleSpinner.selectedItem.toString()

            // Check if passwords match
            if (password != confirmPassword) {
                showToast("Passwords do not match.")
                return@setOnClickListener
            }

            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(requireActivity()) { task ->
                    if (task.isSuccessful) {
                        val user = auth.currentUser
                        val profileUpdates = UserProfileChangeRequest.Builder()
                            .setDisplayName(hashEmail(email))
                            .build()

                        user?.updateProfile(profileUpdates)
                            ?.addOnCompleteListener { profileUpdateTask ->
                                if (profileUpdateTask.isSuccessful) {
                                    grantUserRole(user, username, email, selectedRole)
                                    showToast("Registration successful!")
                                    // Navigate to another screen or handle the success scenario
                                } else {
                                    val errorMessage =
                                        "Failed to update user profile: ${profileUpdateTask.exception?.message}"
                                    showToast(errorMessage)
                                    Log.e("ProfileUpdateTask", errorMessage, profileUpdateTask.exception)
                                }
                            }
                    } else {
                        val errorMessage = "Registration failed: ${task.exception?.message}"
                        showToast(errorMessage)
                        Log.e("RegistrationTask", errorMessage, task.exception)
                    }
                }
        }

        return rootView
    }

    private fun grantUserRole(user: FirebaseUser?, username: String, email: String, role: String) {
        val userId = user?.uid ?: ""
        val userEmail = email
        val hashedPassword = hashPassword(email)

        val userObject = User(userId, userEmail, username, role, hashedPassword)

        // Store the user data in Firestore
        db.collection("users").document(userId)
            .set(userObject)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    showToast("$role role granted!")

                    // Check the role and grant additional permissions if it's an admin
                    if (role == "Admin") {
                        grantAdminPermissions(userId)
                    }
                } else {
                    val errorMessage = "Failed to grant $role role: ${task.exception?.message}"
                    showToast(errorMessage)
                    Log.e("GrantUserRole", errorMessage, task.exception)
                }
            }
    }

    private fun grantAdminPermissions(userId: String) {
        // Implement the logic to grant admin-specific permissions here
        // This could include adding the user to an "admins" node in the database, etc.
        // For demonstration purposes, let's log a message.
        Log.d("GrantAdminPermissions", "Admin permissions granted for user: $userId")
    }


    private fun hashEmail(email: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(email.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }

    private fun hashPassword(password: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(password.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
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
