package com.sonicdutch.portal

import androidx.room.Dao
import androidx.room.Query

class Songdao {

    @Dao
    interface SongDao
    {

        @Query("SELECT url FROM tb_song WHERE time = :time_now and weather = :weather_now")
        fun findUrl(time_now: Int, weather_now: Int): String

    }

}