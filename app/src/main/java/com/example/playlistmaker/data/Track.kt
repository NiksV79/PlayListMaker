package com.example.playlistmaker.data
import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.text.SimpleDateFormat
import java.util.Locale

@Parcelize
data class Track (
    val trackId: String, //ИД аудио трека
    val trackName: String, // Название  аудио трека
    val artistName: String, // Артист
    val trackTimeMillis: Long, // Длительность
    val artworkUrl100: String, // url изображения
    val collectionName: String?, //Альбом
    val primaryGenreName: String?, //Жанр
    val releaseDate:String?, //дата трека
    val country: String? //Страна
) : Parcelable {
    val artworkUrl512: String
        get() = artworkUrl100.replaceAfterLast('/',"512x512bb.jpg")
    val year: String?
        get() = releaseDate?.take(4)
}

fun Long.toTrackTime(): String {
    return SimpleDateFormat("mm:ss", Locale.getDefault()).format(this)
}
