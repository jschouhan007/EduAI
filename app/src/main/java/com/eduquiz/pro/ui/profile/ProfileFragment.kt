// FILE: app/src/main/java/com/eduquiz/pro/ui/profile/ProfileFragment.kt
package com.eduquiz.pro.ui.profile

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.eduquiz.pro.auth.LoginActivity
import com.eduquiz.pro.databinding.FragmentProfileBinding
import com.eduquiz.pro.util.LanguageManager
import com.eduquiz.pro.util.ThemeManager
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ProfileViewModel by viewModels()

    @Inject lateinit var languageManager: LanguageManager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val guest = viewModel.isGuest()
        binding.tvAvatar.text = viewModel.getInitial()
        binding.tvName.text = viewModel.getDisplayName()
        binding.tvEmail.text = if (guest) getString(com.eduquiz.pro.R.string.signed_in_guest) else viewModel.getEmail()

        if (guest) {
            binding.btnEditProfile.text = getString(com.eduquiz.pro.R.string.login_to_unlock)
            binding.btnEditProfile.setOnClickListener {
                Snackbar.make(binding.root, com.eduquiz.pro.R.string.sign_in_feature, Snackbar.LENGTH_SHORT).show()
            }
        }

        when (viewModel.getSavedTheme()) {
            ThemeManager.ThemeMode.LIGHT -> binding.rbThemeLight.isChecked = true
            ThemeManager.ThemeMode.DARK -> binding.rbThemeDark.isChecked = true
            ThemeManager.ThemeMode.SYSTEM -> binding.rbThemeSystem.isChecked = true
        }

        binding.rgTheme.setOnCheckedChangeListener { _, checkedId ->
            val mode = when (checkedId) {
                com.eduquiz.pro.R.id.rbThemeLight -> ThemeManager.ThemeMode.LIGHT
                com.eduquiz.pro.R.id.rbThemeDark -> ThemeManager.ThemeMode.DARK
                else -> ThemeManager.ThemeMode.SYSTEM
            }
            viewModel.applyTheme(mode)
            requireActivity().recreate()
        }

        when (languageManager.getSavedLocale()) {
            "hi" -> binding.rbLangHindi.isChecked = true
            "es" -> binding.rbLangSpanish.isChecked = true
            else -> binding.rbLangEnglish.isChecked = true
        }

        binding.rgLanguage.setOnCheckedChangeListener { _, checkedId ->
            val code = when (checkedId) {
                com.eduquiz.pro.R.id.rbLangHindi -> "hi"
                com.eduquiz.pro.R.id.rbLangSpanish -> "es"
                else -> "en"
            }
            languageManager.setLocale(code)
            requireActivity().recreate()
        }

        binding.rowResetProgress.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle(com.eduquiz.pro.R.string.reset_progress)
                .setMessage(com.eduquiz.pro.R.string.reset_progress_confirm)
                .setPositiveButton(com.eduquiz.pro.R.string.yes) { _, _ -> viewModel.resetAllProgress() }
                .setNegativeButton(com.eduquiz.pro.R.string.no, null)
                .show()
        }

        binding.rowLogout.setOnClickListener {
            viewModel.logout()
            val intent = Intent(requireContext(), LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
