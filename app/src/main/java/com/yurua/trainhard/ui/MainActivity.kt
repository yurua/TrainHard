package com.yurua.trainhard.ui

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.firebase.auth.FirebaseAuth
import com.yurua.trainhard.R
import com.yurua.trainhard.R.color
import com.yurua.trainhard.R.id
import com.yurua.trainhard.databinding.ActivityMainBinding
import com.yurua.trainhard.util.TitleListener
import dagger.hilt.android.AndroidEntryPoint

var prevDestination = 0
const val GYM_RESULT_OK = Activity.RESULT_FIRST_USER

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), TitleListener {

    lateinit var auth: FirebaseAuth

    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration

    private lateinit var binding: ActivityMainBinding

    override fun onStart() {
        super.onStart()
        authUser()
    }

    fun authUser() {

        val user = auth.currentUser
        if (user != null)
//            Toast.makeText(
//                this@MainActivity,
//                "Пользователь  ${user.email} в системе",
//                Toast.LENGTH_SHORT
//            ).show()
        else {
            val email = getString(R.string.user_email)
            val password = getString(R.string.user_password)
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful)
                        Toast.makeText(
                            this@MainActivity,
                            "Пользователь  ${auth.currentUser?.email} ",
                            Toast.LENGTH_SHORT
                        ).show()
                    else
                        Toast.makeText(this@MainActivity, "Ошибка входа!", Toast.LENGTH_LONG).show()
                }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        val sharedPreferences =
            getSharedPreferences(packageName + "_preferences", Context.MODE_PRIVATE)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment =
            supportFragmentManager.findFragmentById(id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        appBarConfiguration = AppBarConfiguration(
            setOf(
                id.workFragment,
                id.statFragment,
                id.calendarFragment,
                id.moreFragment
            )
        )
        val toolbar = findViewById<Toolbar>(id.toolbar)

        setSupportActionBar(toolbar)
        supportActionBar?.setBackgroundDrawable(
            ColorDrawable(
                resources.getColor(
                    color.darker,
                    null
                )
            )
        )

        toolbar.setTitleTextAppearance(this, R.style.TitleBarTextAppearance)
        toolbar.setSubtitleTextAppearance(this, R.style.SubtitleBarTextAppearance)
        toolbar.setSubtitleTextColor(Color.parseColor("#99FFFFFF"))

        binding.bottomNavigationView.setupWithNavController(navController)
        setupActionBarWithNavController(navController, appBarConfiguration)

        navController.addOnDestinationChangedListener { controller, destination, _ ->
            when (destination.id) {
                id.workFragment, id.statFragment, id.calendarFragment -> {
                    binding.bottomNavigationView.visibility = View.VISIBLE
                    prevDestination = controller.currentDestination!!.id
                }
                else -> {
                    binding.bottomNavigationView.visibility = View.GONE
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        navController.navigate(prevDestination)
        return true
    }

    override fun setToolbarTitle(str: String) {
        supportActionBar?.subtitle = str
    }
}

