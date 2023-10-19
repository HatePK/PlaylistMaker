package com.practicum.playlistmaker.settings.ui.activity

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.FrameLayout
import com.google.android.material.switchmaterial.SwitchMaterial
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.settings.ui.view_model.SettingsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

@Suppress("DEPRECATION")
class SettingsActivity : AppCompatActivity() {

    private lateinit var themeSwitcher: SwitchMaterial
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val viewModel by viewModel<SettingsViewModel>()

        val back = findViewById<Button>(R.id.back_button)
        back.setOnClickListener {
            super.onBackPressed()
        }

        val share = findViewById<FrameLayout>(R.id.share_button)
        share.setOnClickListener {
            viewModel.share()
        }

        val support = findViewById<FrameLayout>(R.id.support_button)
        support.setOnClickListener {
            viewModel.support()
        }

        val agreement = findViewById<FrameLayout>(R.id.agreement_button)
        agreement.setOnClickListener {
            viewModel.agreement()
        }

        themeSwitcher = findViewById(R.id.themeSwitcher)
        themeSwitcher.isChecked = viewModel.currentThemeStatus

        themeSwitcher.setOnCheckedChangeListener { _, isChecked ->
            viewModel.switchTheme(isChecked)
        }
    }
}