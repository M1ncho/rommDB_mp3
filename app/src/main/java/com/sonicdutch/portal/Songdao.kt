package com.sonicdutch.portal

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

class Songdao {

    @Dao
    interface SongDao
    {

        @Query("SELECT * FROM tb_song WHERE time = :time_now and weather = :weather_now")
        suspend fun findUrl(time_now: Int, weather_now: Int): Songentitiy.Song

        @Query("SELECT * FROM tb_song")
        suspend fun getAll():Array<Songentitiy.Song>

        @Query("SELECT * FROM tb_song WHERE id = :id")
        suspend fun getUrl(id: Int): Songentitiy.Song


        @Insert
        suspend fun insert(song : Songentitiy.Song)

    }

}