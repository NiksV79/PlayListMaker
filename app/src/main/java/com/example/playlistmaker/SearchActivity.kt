package com.example.playlistmaker

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.api.ITunesApi
import com.example.playlistmaker.api.ITunesApiConfig
import com.example.playlistmaker.api.ITunesResponseSongs
import com.example.playlistmaker.data.Track
import com.example.playlistmaker.databinding.ActivitySearchBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.Locale

class SearchActivity:AppCompatActivity() {
    lateinit var et:EditText
    private lateinit var binding: ActivitySearchBinding

    private val retrofit = Retrofit.Builder()
        .baseUrl(ITunesApiConfig.url)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val iTunesService = retrofit.create(ITunesApi::class.java)

    private val tracks = ArrayList<Track>()
    private val adapter = TracksAdapter()

    private enum class ViewState {
        START,
        LIST,
        NODATA,
        NOWIFI
    }

    private var state: ViewState = ViewState.START

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_search)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val btnBack = findViewById<View>(R.id.lsr_back)
        btnBack.setOnClickListener {
            finish()
        }

        binding.lsrNowifiBtn.setOnClickListener {
            etOnOk(EditorInfo.IME_ACTION_DONE)
        }

        initEditText()
        initTracks()

        changeState(ViewState.START)
    }

    private fun changeState(newState:ViewState) {
        state = newState
        when (state) {
            ViewState.START -> {
                tracks.clear()
                adapter.notifyDataSetChanged()

                binding.lsrList.visibility = View.VISIBLE
                binding.lsrNodata.visibility = View.GONE
                binding.lsrNowifi.visibility = View.GONE
            }
            ViewState.LIST -> {
                binding.lsrList.visibility = View.VISIBLE
                binding.lsrNodata.visibility = View.GONE
                binding.lsrNowifi.visibility = View.GONE
            }
            ViewState.NODATA -> {
                binding.lsrList.visibility = View.GONE
                binding.lsrNodata.visibility = View.VISIBLE
                binding.lsrNowifi.visibility = View.GONE
            }
            ViewState.NOWIFI -> {
                binding.lsrList.visibility = View.GONE
                binding.lsrNodata.visibility = View.GONE
                binding.lsrNowifi.visibility = View.VISIBLE
            }
        }
    }

    private fun initTracks() {
        val recycler = findViewById<RecyclerView>(R.id.lsr_tracks)
        adapter.tracks = tracks
        recycler.adapter = adapter
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
        et.setOnEditorActionListener { _, actionId, _ -> etOnOk(actionId) }

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

    fun twOnTextChanged(s: CharSequence?) {
        val startDrawable = getDrawable(R.drawable.ic_search14)
        val endDrawable = if (s.isNullOrEmpty()) null else getDrawable(R.drawable.ic_clear)
        et.setCompoundDrawablesWithIntrinsicBounds(startDrawable,null,endDrawable,null)
    }

    fun isTouchInDrawableEnd(event: MotionEvent): Boolean {
        val drawableEnd = et.compoundDrawables[2] ?: return false
        val iconX = et.right - et.compoundPaddingEnd - drawableEnd.intrinsicWidth
        return event.x >= iconX
    }

    fun etOnTouch(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_UP -> {
                if (isTouchInDrawableEnd(event)) {
                    et.setText("")
                    changeState(ViewState.START)
                    val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(et.windowToken,0)
                    return true
                } else return false
            }
            else -> return false
        }
    }

    fun etOnOk(actionId:Int): Boolean {
        if (actionId != EditorInfo.IME_ACTION_DONE) return false

        iTunesService.search(et.text.toString()).enqueue(object : Callback<ITunesResponseSongs>
        {
            override fun onResponse(call: Call<ITunesResponseSongs>, response: Response<ITunesResponseSongs>) {
                iTunesOnResponse(call,response)
            }
            override fun onFailure(call: Call<ITunesResponseSongs>, t: Throwable) {
                iTunesOnFailure(call,t)
            }
        })
        return true
    }

    fun iTunesOnResponse(call: Call<ITunesResponseSongs>, response: Response<ITunesResponseSongs>) {
        Log.d("TEST","iTunesOnResponse:" + response.code().toString())
        if (response.code() == 200) {
            tracks.clear()
            if (response.body()?.results?.isNotEmpty() == true) {
                tracks.addAll(response.body()?.results!!)
                adapter.notifyDataSetChanged()
                changeState(ViewState.LIST)
            }
            else {
                changeState(ViewState.NODATA)
            }
        }
        else {
            changeState(ViewState.NOWIFI)
        }
    }

    fun iTunesOnFailure(call: Call<ITunesResponseSongs>, t: Throwable) {
        changeState(ViewState.NOWIFI)
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

    var artworkUrl100CornerRadius:Int = 0

    init {
        artworkUrl100CornerRadius = dpToPx(4F)
    }

    private fun dpToPx(dp: Float): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            itemView.context.resources.displayMetrics
        ).toInt()
    }

    fun bind(track: Track) {
        trackName.text = track.trackName
        artistName.text = track.artistName
        trackTime.text = track.trackTimeMillis.toTrackTime()
        Glide.with(itemView).
            load(track.artworkUrl100).
            fitCenter().
            placeholder(R.drawable.ic_placeholder).
            transform(RoundedCorners(artworkUrl100CornerRadius)).
            into(artworkUrl100)
    }
}

class TracksAdapter() : RecyclerView.Adapter<TracksViewHolder>() {
    var tracks = ArrayList<Track>()

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

fun Long.toTrackTime(): String {
    return SimpleDateFormat("mm:ss", Locale.getDefault()).format(this)
}