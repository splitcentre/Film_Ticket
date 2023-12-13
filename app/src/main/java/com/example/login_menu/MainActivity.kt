package com.example.login_menu

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.login_menu.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        replaceFragment(LoginFragment())

        binding.botnav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.loginmenu -> replaceFragment(LoginFragment())
                R.id.registermenu -> replaceFragment(RegisterFragment())
                else -> false
            }
        }
    }

    private fun replaceFragment(fragment: Fragment): Boolean {
        supportFragmentManager.beginTransaction().replace(R.id.frameLayout, fragment).commit()
        return true
    }
}
