package com.example.playlistmaker.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.example.playlistmaker.App
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivityMainBinding
import com.example.playlistmaker.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root)
        {
                view, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.updatePadding(top = bars.top,bottom = bars.bottom)
            insets
        }

        binding.lstLayoutBack.setOnClickListener {
            finish()
        }

        binding.lstOpShare.setOnClickListener {
            val intent = Intent(Intent.ACTION_SEND)
            intent.putExtra(Intent.EXTRA_TEXT,getString(R.string.lst_op_share_weblink))
            intent.type = "text/plain"
            startActivity(Intent.createChooser(intent, getString(R.string.lst_op_share)))
        }

        binding.laySettingsOpSupport.setOnClickListener {
            val intent = Intent(Intent.ACTION_SENDTO)
            intent.data = Uri.parse("mailto:")
            intent.putExtra(Intent.EXTRA_EMAIL,arrayOf(getString(R.string.lst_op_support_email_sender)))
            intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.lst_op_support_email_subject))
            intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.lst_op_support_email_text))
            startActivity(intent)
        }

        binding.lstOpUserAgreement.setOnClickListener {
            val url = getString(R.string.lst_op_user_agreement_weblink)
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