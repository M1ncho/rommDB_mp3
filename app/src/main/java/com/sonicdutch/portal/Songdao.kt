package com.sonicdutch.portal

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

class Songdao {

    @Dao
    interface SongDao
    {
        // 해당 노래 url 가져오기
        @Query("SELECT * FROM tb_song WHERE time = :time_now and weather = :weather_now")
        suspend fun findUrl(time_now: Int, weather_now: Int): Songentitiy.Song

        // 전부 가져오기
        @Query("SELECT * FROM tb_song")
        suspend fun getAll():Array<Songentitiy.Song>

        // 해당 id의 노래 rul 가져오기
        @Query("SELECT * FROM tb_song WHERE id = :id")
        suspend fun getUrl(id: Int): Songentitiy.Song

        // 해당 노래 id 찾기
        @Query("SELECT * FROM tb_song WHERE time = :time_now and weather = :weather_now")
        suspend fun findid(time_now: Int, weather_now: Int): Songentitiy.Song


        @Insert
        suspend fun insert(song : Songentitiy.Song)

    }

}