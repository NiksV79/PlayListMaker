package com.example.playlistmaker

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val btnBack = findViewById<View>(R.id.lay_settings_layout_back)
        btnBack.setOnClickListener {
            finish()
        }

        val btnShare = findViewById<View>(R.id.lay_settings_op_share)
        btnShare.setOnClickListener {
            val intent = Intent(Intent.ACTION_SEND)
            intent.putExtra(Intent.EXTRA_TEXT,getString(R.string.lay_settings_op_share_weblink))
            intent.type = "text/plain"
            startActivity(Intent.createChooser(intent, getString(R.string.lay_settings_op_share)))
        }

        val btnSupport = findViewById<View>(R.id.lay_settings_op_support)
        btnSupport.setOnClickListener {
            val intent = Intent(Intent.ACTION_SENDTO)
            intent.data = Uri.parse("mailto:")
            intent.putExtra(Intent.EXTRA_EMAIL,arrayOf(getString(R.string.lay_settings_op_support_email_sender)))
            intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.lay_settings_op_support_email_subject))
            intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.lay_settings_op_support_email_text))
            startActivity(intent)
        }

        val btnUserAgreement = findViewById<View>(R.id.lay_settings_op_user_agreement)
        btnUserAgreement.setOnClickListener {
            val url = getString(R.string.lay_settings_op_user_agreement_weblink)
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(intent)
        }
    }
}