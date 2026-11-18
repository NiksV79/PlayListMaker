package com.example.playlistmaker.ui

import android.content.Context
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
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
    private val handler = Handler(Looper.getMainLooper())
    private var _track: Track? = null
    private var track: Track
        get() = _track?: throw IllegalStateException("Track не установлен")
        set(value) {
            _track = value
            updateTrack(value)
        }
    private lateinit var player:Player

    var artworkUrl512CornerRadius:Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root)
        {
                view, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.updatePadding(top = bars.top,bottom = bars.bottom)
            insets
        }

        binding.lplBack.setOnClickListener { finish() }
        artworkUrl512CornerRadius = Utils.dpToPx(this,16F)

        track = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(Entities.TRACK, Track::class.java)!!
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(Entities.TRACK)!!
        }

        binding.lplBtnAdd.setImageResource(R.drawable.btn_add)
        binding.lplBtnLike.setImageResource(R.drawable.btn_like)
    }

    override fun onDestroy() {
        player.release()
        super.onDestroy()
    }

    override fun onPause() {
        player.onPause()
        super.onPause()
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

        player = Player(this,handler,binding.lplBtnPlay,binding.lplPlaytime,track)
    }
}

class Player(val ctx: Context, val handler:Handler,val img: ImageView, val txt: TextView,val track:Track) {
    enum class State {INIT,PREPARED,PLAYING,PAUSED}

    private val mediaPlayer = MediaPlayer()

    private val tick = Runnable { tick() }
    private var state: State = State.INIT
        set(value) {
            field = value
            onNewState(value)
        }

    init {
        init()
    }

    private fun init() {
        img.isEnabled = false
        if (track.previewUrl == null) {
            txt.text = ctx.getString(R.string.lpl_nodata)
            return
        }
        txt.text = ctx.getString(R.string.lpl_loading_text)
        mediaPlayer.setDataSource(track.previewUrl)
        mediaPlayer.setOnPreparedListener { onPrepared() }
        mediaPlayer.setOnCompletionListener { onComplete() }
        mediaPlayer.prepareAsync()
        img.setOnClickListener { toggle() }
    }

    private fun tick() {
        txt.text = mediaPlayer.currentPosition.toTrackTime()
        if (state == State.PLAYING) {
            handler.postDelayed(tick,TICK_INTERVAL)
        }
    }

    private fun onPrepared() {
        img.isEnabled = true
        state = State.PREPARED
    }

    private fun onComplete() {
        state = State.PREPARED
    }

    fun onPause() {
        if (state == State.PLAYING) {
            state = State.PAUSED
        }
    }

    fun release() {
        handler.removeCallbacks(tick)
        mediaPlayer.release()
    }

    fun onNewState(newState:State) {
        when(newState) {
            State.INIT -> {}
            State.PREPARED -> {
                handler.removeCallbacks(tick)
                mediaPlayer.seekTo(0)
                txt.text = 0L.toTrackTime()
                img.setImageResource(R.drawable.btn_play)
            }
            State.PLAYING -> {
                img.setImageResource(R.drawable.btn_pause)
                mediaPlayer.start()
                tick()
            }
            State.PAUSED -> {
                img.setImageResource(R.drawable.btn_play)
                mediaPlayer.pause()
                tick()
            }
        }
    }

    private fun toggle() {
        state = when (state) {
            State.INIT -> State.INIT
            State.PREPARED -> State.PLAYING
            State.PLAYING -> State.PAUSED
            State.PAUSED -> State.PLAYING
        }
    }

    companion object {
        const val TICK_INTERVAL = 250L
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