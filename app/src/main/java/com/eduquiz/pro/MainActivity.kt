// FILE: app/src/main/java/com/eduquiz/pro/MainActivity.kt
package com.eduquiz.pro

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.eduquiz.pro.databinding.ActivityMainBinding
import com.eduquiz.pro.util.LanguageManager
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun attachBaseContext(newBase: Context) {
        val manager = LanguageManager(newBase.applicationContext)
        super.attachBaseContext(manager.wrapContext(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHost = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHost.navController
        binding.bottomNav.setupWithNavController(navController)

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val currentId = navController.currentDestination?.id
                if (currentId == R.id.homeFragment) {
                    AlertDialog.Builder(this@MainActivity)
                        .setTitle(R.string.exit_title)
                        .setMessage(R.string.exit_message)
                        .setPositiveButton(R.string.yes) { _, _ -> finish() }
                        .setNegativeButton(R.string.no, null)
                        .show()
                } else {
                    if (!navController.popBackStack()) finish()
                }
            }
        })
    }
}
