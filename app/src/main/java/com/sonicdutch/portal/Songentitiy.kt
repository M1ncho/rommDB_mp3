package com.sonicdutch.portal
import androidx.room.*

class Songentitiy {

    @Entity(tableName = "tb_song")

    data class Song(
        @PrimaryKey val id: Int,
        @ColumnInfo(name = "time")val time: Int,
        @ColumnInfo(name = "weather")val weather: Int,
        @ColumnInfo(name = "url")val url: String,
        @ColumnInfo(name = "name")val name: String
    )



}