package com.example.login_menu

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.login_menu.database.User
import com.example.login_menu.databinding.FragmentRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.FirebaseDatabase
import java.security.MessageDigest

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class RegisterFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding: FragmentRegisterBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

        auth = FirebaseAuth.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_register, container, false)

        val emailEditText = rootView.findViewById<EditText>(R.id.emailEditText)
        val passwordEditText = rootView.findViewById<EditText>(R.id.passwordEditText)
        val registerButton = rootView.findViewById<Button>(R.id.registerButton)

        registerButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(requireActivity()) { task ->
                    if (task.isSuccessful) {
                        val user = auth.currentUser

                        // Update user profile with hashed email as the display name
                        val profileUpdates = UserProfileChangeRequest.Builder()
                            .setDisplayName(hashEmail(email))
                            .build()

                        user?.updateProfile(profileUpdates)
                            ?.addOnCompleteListener { profileUpdateTask ->
                                if (profileUpdateTask.isSuccessful) {
                                    val isUserAdmin = email.endsWith("@mail.org")

                                    if (isUserAdmin) {
                                        grantAdminRole(user)
                                    } else {
                                        grantUserRole(user)
                                    }

                                    Toast.makeText(
                                        requireContext(),
                                        "Registration successful!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    // Navigate to another screen or handle the success scenario
                                } else {
                                    showToast("Failed to update user profile: ${profileUpdateTask.exception?.message}")
                                }
                            }
                    } else {
                        showToast("Registration failed: ${task.exception?.message}")
                    }
                }
        }

        return rootView
    }

    private fun grantAdminRole(user: FirebaseUser?) {
        val userId = user?.uid ?: ""
        val userEmail = user?.email ?: ""

        // Hash the password
        val hashedPassword = hashPassword(userEmail ?: "")

        val adminUser = User(userId, userEmail, "Admin", hashedPassword)

        // Store the user data in Firebase Realtime Database
        // Replace "users" with your desired database reference
        FirebaseDatabase.getInstance().getReference("users").child(userId)
            .setValue(adminUser)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    showToast("Admin role granted!")
                } else {
                    showToast("Failed to grant admin role: ${task.exception?.message}")
                }
            }
    }

    private fun grantUserRole(user: FirebaseUser?) {
        val userId = user?.uid ?: ""
        val userEmail = user?.email ?: ""

        // Hash the password
        val hashedPassword = hashPassword(userEmail ?: "")

        val regularUser = User(userId, userEmail, "User", hashedPassword)

        // Store the user data in Firebase Realtime Database
        // Replace "users" with your desired database reference
        FirebaseDatabase.getInstance().getReference("users").child(userId)
            .setValue(regularUser)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    showToast("User role granted!")
                } else {
                    showToast("Failed to grant user role: ${task.exception?.message}")
                }
            }
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
