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
import com.example.login_menu.databinding.FragmentLoginBinding
import com.google.firebase.auth.FirebaseAuth

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [LoginFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class LoginFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding: FragmentLoginBinding
    private lateinit var auth: FirebaseAuth // Firebase Authentication instance

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

        // Initialize Firebase Authentication
        auth = FirebaseAuth.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_login, container, false)

        val usernameEditText = rootView.findViewById<EditText>(R.id.usernameEditText)
        val passwordEditText = rootView.findViewById<EditText>(R.id.passwordEditText)
        val loginButton = rootView.findViewById<Button>(R.id.loginButton)

        loginButton.setOnClickListener {
            Log.d("LoginFragment", "Button Clicked")
            val username = usernameEditText.text.toString()
            val password = passwordEditText.text.toString()

            // Authenticate using Firebase Authentication
            auth.signInWithEmailAndPassword(username, password)
                .addOnCompleteListener(requireActivity()) { task ->
                    if (task.isSuccessful) {
                        Log.d("LoginFragment", "Authentication success")

                        // Determine user role (admin or user) based on username
                        val isUserAdmin = username.endsWith("@mail.org")

                        // Create an intent based on user role
                        val intent = if (isUserAdmin) {
                            Intent(activity, AdminActivity::class.java)
                        } else {
                            Intent(activity, MainActivity::class.java)
                        }

                        startActivity(intent)
                    } else {
                        Log.w("LoginFragment", "Authentication failed", task.exception)
                        // Handle authentication failure, e.g., display an error message
                    }
                }
        }

        return rootView
    }
}
