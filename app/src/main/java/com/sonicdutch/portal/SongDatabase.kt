package com.sonicdutch.portal

import android.content.Context
import android.os.Bundle
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase


class SongDatabase {

    @Database(entities = arrayOf(Songentitiy.Song::class),version = 1)
    abstract class songDatabase : RoomDatabase()
    {
        abstract fun songDao() : Songdao.SongDao
        companion object
        {
            private var INSTANCE: songDatabase? = null
            fun getInstance(context: Context): songDatabase {
                return buildDatabase(context)
            }

            private fun buildDatabase(context: Context): songDatabase{
                return Room.databaseBuilder(context,songDatabase::class.java,"song_data").addCallback(object : RoomDatabase.Callback()
                {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)

                        Songentitiy.Song(1,0,0,"00")
                        Songentitiy.Song(2,0,1,"01")
                        Songentitiy.Song(3,0,2,"02")
                        Songentitiy.Song(4,1,0,"10")
                        Songentitiy.Song(5,1,1,"11")
                        Songentitiy.Song(6,1,3,"12")
                        Songentitiy.Song(7,2,0,"20")
                        Songentitiy.Song(8,2,1,"21")
                        Songentitiy.Song(9,2,2,"22")
                        Songentitiy.Song(10,3,0,"30")
                        Songentitiy.Song(11,3,1,"31")
                        Songentitiy.Song(12,3,2,"32")

                    }
                })
                    .build()
            }
        }

    }



}