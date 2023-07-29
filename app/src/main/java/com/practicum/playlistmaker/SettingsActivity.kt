package com.practicum.playlistmaker

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.FrameLayout
import com.google.android.material.switchmaterial.SwitchMaterial

const val SHARED_PREFERENCES = "playlist_maker_preferences"
const val DARK_THEME_ENABLED = "false"
@Suppress("DEPRECATION")
class SettingsActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val back = findViewById<Button>(R.id.back_button)
        back.setOnClickListener {
            super.onBackPressed()
        }
        val themeSwitcher = findViewById<SwitchMaterial>(R.id.themeSwitcher)
        val sharedPrefs = getSharedPreferences(SHARED_PREFERENCES, MODE_PRIVATE)

        themeSwitcher.isChecked = sharedPrefs.getBoolean(DARK_THEME_ENABLED, false)

        themeSwitcher.setOnCheckedChangeListener { switcher, checked ->
            (applicationContext as App).switchTheme(checked)
            sharedPrefs.edit()
                .putBoolean(DARK_THEME_ENABLED, checked)
                .apply()
        }

        val share = findViewById<FrameLayout>(R.id.share_button)
        share.setOnClickListener {
            val shareIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, getString(R.string.share_content))
                type = "text/plain"
            }
            startActivity(shareIntent)
        }

        val support = findViewById<FrameLayout>(R.id.support_button)
        support.setOnClickListener {
            val supportIntent = Intent(Intent.ACTION_SENDTO)

            supportIntent.data = Uri.parse(
                getString(R.string.support_extra_email) +
                    "?subject=${getString(R.string.support_extra_topic)}" +
                    "&body=${getString(R.string.support_extra_text)}"
            )

            startActivity(Intent.createChooser(supportIntent, getString(R.string.support_extra_topic)))
        }

        val agreement = findViewById<FrameLayout>(R.id.agreement_button)
        agreement.setOnClickListener {
            val agreementIntent = Intent(Intent.ACTION_VIEW)
            agreementIntent.data = Uri.parse(getString(R.string.agreement_link))
            startActivity(agreementIntent)
        }
    }
}