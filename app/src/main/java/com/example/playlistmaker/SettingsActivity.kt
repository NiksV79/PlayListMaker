package com.example.playlistmaker

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.playlistmaker.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.laySettingsLayoutBack.setOnClickListener {
            finish()
        }

        binding.laySettingsOpShare.setOnClickListener {
            val intent = Intent(Intent.ACTION_SEND)
            intent.putExtra(Intent.EXTRA_TEXT,getString(R.string.lay_settings_op_share_weblink))
            intent.type = "text/plain"
            startActivity(Intent.createChooser(intent, getString(R.string.lay_settings_op_share)))
        }

        binding.laySettingsOpSupport.setOnClickListener {
            val intent = Intent(Intent.ACTION_SENDTO)
            intent.data = Uri.parse("mailto:")
            intent.putExtra(Intent.EXTRA_EMAIL,arrayOf(getString(R.string.lay_settings_op_support_email_sender)))
            intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.lay_settings_op_support_email_subject))
            intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.lay_settings_op_support_email_text))
            startActivity(intent)
        }

        binding.laySettingsOpUserAgreement.setOnClickListener {
            val url = getString(R.string.lay_settings_op_user_agreement_weblink)
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(intent)
        }

        binding.lstTheme.isChecked = (applicationContext as App).isDarkTheme
        binding.lstTheme.setOnCheckedChangeListener { _,checked -> changeTheme(checked) }
    }

    private fun changeTheme(checked: Boolean) {
        (applicationContext as App).switchTheme(checked)
    }

}