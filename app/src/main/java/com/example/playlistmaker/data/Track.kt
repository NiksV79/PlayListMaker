package com.example.playlistmaker.data

data class Track (
    val trackId: String, //ИД композиции
    val trackName: String, // Название композиции
    val artistName: String, // Артист
    val trackTimeMillis: Long, // Длительность
    val artworkUrl100: String // url изображения
) {}