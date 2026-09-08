package com.example.playlistmaker

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.data.Track
import com.example.playlistmaker.data.Tracks

class SearchActivity:AppCompatActivity() {
    lateinit var et:EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val btnBack = findViewById<View>(R.id.lsr_back)
        btnBack.setOnClickListener {
            finish()
        }

        initEditText()
        initTracks()
    }

    private fun initTracks() {
        val recycler = findViewById<RecyclerView>(R.id.lsr_tracks)
        recycler.adapter = TracksAdapter(Tracks)
    }

    private fun initEditText() {
        et = findViewById<EditText>(R.id.lsr_edittext)

        val tw = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {twOnTextChanged(s)}
            override fun afterTextChanged(s: Editable?) {}
        }

        et.setOnTouchListener { _, event -> etOnTouch(event) }
        et.addTextChangedListener(tw)

        twOnTextChanged(et.text)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putString(ET_ID,et.text.toString())
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)

        val etText = savedInstanceState.getString(ET_ID,ET_DEF)
        et.setText(etText)
        twOnTextChanged(et.text)
    }

    private fun twOnTextChanged(s: CharSequence?) {
        val startDrawable = getDrawable(R.drawable.ic_search14)
        val endDrawable = if (s.isNullOrEmpty()) null else getDrawable(R.drawable.ic_clear)
        et.setCompoundDrawablesWithIntrinsicBounds(startDrawable,null,endDrawable,null)
    }

    private fun isTouchInDrawableEnd(event: MotionEvent): Boolean {
        val drawableEnd = et.compoundDrawables[2] ?: return false
        val iconX = et.right - et.compoundPaddingEnd - drawableEnd.intrinsicWidth
        return event.x >= iconX
    }

    private fun etOnTouch(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_UP -> {
                if (isTouchInDrawableEnd(event)) {
                    et.setText("")
                    val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(et.windowToken,0)
                    return true
                } else return false
            }
            else -> return false
        }
    }

    companion object {
        const val ET_ID = "ET_ID"
        const val ET_DEF = ""
    }
}

class TracksViewHolder(itemView:View) : RecyclerView.ViewHolder(itemView) {
    private val trackName: TextView = itemView.findViewById(R.id.tw_trackName)
    private val artistName: TextView = itemView.findViewById(R.id.tw_ArtistName)
    private val trackTime: TextView = itemView.findViewById(R.id.tw_trackTime)
    private val artworkUrl100: ImageView = itemView.findViewById(R.id.tw_artworkUrl100)

    fun bind(track: Track) {
        trackName.text = track.trackName
        artistName.text = track.artistName
        trackTime.text = track.trackTime
        Glide.with(itemView).
            load(track.artworkUrl100).
            fitCenter().
            placeholder(R.drawable.ic_placeholder).
            transform(RoundedCorners(10)).
            into(artworkUrl100)
    }
}

class TracksAdapter(private val tracks: Tracks) : RecyclerView.Adapter<TracksViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TracksViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.track_view, parent, false)
        return TracksViewHolder(view)
    }

    override fun onBindViewHolder(holder: TracksViewHolder, position: Int) {
        holder.bind(tracks[position])
    }

    override fun getItemCount(): Int {
        return tracks.size
    }
}

