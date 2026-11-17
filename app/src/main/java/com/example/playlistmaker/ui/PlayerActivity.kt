package com.example.playlistmaker.ui

import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.data.Entities
import com.example.playlistmaker.data.Track
import com.example.playlistmaker.data.toTrackTime
import com.example.playlistmaker.databinding.ActivityPlayerBinding
import com.example.playlistmaker.misc.Utils

class PlayerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPlayerBinding
    private var _track: Track? = null
    private var track: Track
        get() = _track?: throw IllegalStateException("Track не установлен")
        set(value) {
            _track = value
            updateTrack(value)
        }

    var artworkUrl512CornerRadius:Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.lplBack.setOnClickListener { finish() }

        artworkUrl512CornerRadius = Utils.dpToPx(this,16F)

        track = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(Entities.TRACK, Track::class.java)!!
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(Entities.TRACK)!!
        }

        binding.lplBtnAdd.setImageResource(R.drawable.btn_add)
        binding.lplBtnPlay.setImageResource(R.drawable.btn_play)
        binding.lplBtnLike.setImageResource(R.drawable.btn_like)
        binding.lplPlaytime.text = 0L.toTrackTime()

    }

    private fun updateTrack(track: Track) {
        binding.lplTrack.text = track.trackName
        binding.lplArtist.text = track.artistName
        binding.lplTrackTime.text = track.trackTimeMillis.toTrackTime()

        binding.lplCollectionName.setOrHide(track.collectionName,binding.lplCollectionNameCaption)
        binding.lplYear.setOrHide(track.year,binding.lplYearCaption)
        binding.lplPrimaryGenreName.setOrHide(track.primaryGenreName,binding.lplPrimaryGenreNameCaption)
        binding.lplCountry.setOrHide(track.country,binding.lplCountryCaption)

        Glide.with(this).
        load(track.artworkUrl512).
        fitCenter().
        placeholder(R.drawable.ic_placeholder_312).
        transform(RoundedCorners(artworkUrl512CornerRadius)).
        into(binding.lplTrackImg)
    }

}

fun TextView.setOrHide(text:String?,captionTextView:TextView) {
    if(text.isNullOrEmpty()) {
        captionTextView.visibility = View.GONE
        this.visibility = View.GONE
        this.text = ""
    } else {
        captionTextView.visibility = View.VISIBLE
        this.visibility = View.VISIBLE
        this.text = text
    }
}