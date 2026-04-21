// FILE: app/src/main/java/com/eduquiz/pro/auth/LoginActivity.kt
package com.eduquiz.pro.auth

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.lifecycleScope
import com.eduquiz.pro.MainActivity
import com.eduquiz.pro.databinding.ActivityLoginBinding
import com.eduquiz.pro.util.LanguageManager
import com.eduquiz.pro.util.ThemeManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class LoginActivity : androidx.appcompat.app.AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val viewModel: LoginViewModel by viewModels()

    @Inject
    lateinit var themeManager: ThemeManager

    @Inject
    lateinit var languageManager: LanguageManager

    override fun attachBaseContext(newBase: Context) {
        val manager = LanguageManager(newBase.applicationContext)
        super.attachBaseContext(manager.wrapContext(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        val splash = installSplashScreen()
        super.onCreate(savedInstanceState)
        themeManager.applyTheme(themeManager.getSavedTheme())

        splash.setKeepOnScreenCondition { false }

        if (viewModel.isLoggedIn()) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            return
        }

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.etEmail.doAfterTextChanged {
            binding.tilEmail.error = null
            binding.tvError.visibility = View.GONE
            viewModel.clearErrors()
        }
        binding.etPassword.doAfterTextChanged {
            binding.tilPassword.error = null
            binding.tvError.visibility = View.GONE
            viewModel.clearErrors()
        }

        binding.btnLogin.setOnClickListener {
            viewModel.login(binding.etEmail.text?.toString().orEmpty(), binding.etPassword.text?.toString().orEmpty())
        }

        binding.btnGuest.setOnClickListener {
            viewModel.continueAsGuest()
        }

        viewModel.state.observe(this) { state ->
            binding.progressBar.visibility = if (state.isLoading) View.VISIBLE else View.GONE
            binding.btnLogin.isEnabled = !state.isLoading

            binding.tilEmail.error = state.emailErrorRes?.let(::getString)
            binding.tilPassword.error = state.passwordErrorRes?.let(::getString)
            if (state.authErrorRes != null) {
                binding.tvError.text = getString(state.authErrorRes)
                binding.tvError.visibility = View.VISIBLE
            } else {
                binding.tvError.visibility = View.GONE
            }

            if (state.loginSuccess) {
                lifecycleScope.launch {
                    delay(150)
                    startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                    finish()
                }
            }
        }
    }
}
