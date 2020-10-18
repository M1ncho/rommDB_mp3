package com.sonicdutch.portal
import androidx.room.*

class Songentitiy {

    @Entity(tableName = "tb_song")
    data class Song(
        @PrimaryKey(autoGenerate = true) val id: Int,
        @ColumnInfo(name = "time")val time: Int,
        @ColumnInfo(name = "weather")val weather: Int,
        @ColumnInfo(name = "url")val url: String
    )







}