package com.sonicdutch.portal

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.Executors



class SongDatabase {
    @Database(entities = arrayOf(Songentitiy.Song::class),version = 1)
    abstract class songDatabase : RoomDatabase()
    {
        abstract fun songDao() : Songdao.SongDao

        companion object {
            private var INSTANCE: songDatabase? = null
            fun getInstance(context: Context): songDatabase {

               if(INSTANCE==null)
               {
                   //synchronized(this){
                       //INSTANCE=Room.databaseBuilder(context.applicationContext,songDatabase::class.java,"song.db").build() }
                   INSTANCE = buildDatabase(context)
               }

                //buildDatabase(context)
                return INSTANCE!!
        }

           private fun buildDatabase(context: Context): songDatabase {
                return Room.databaseBuilder(context, songDatabase::class.java, "song_data")
                    .addCallback(object : RoomDatabase.Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)

                            Log.e("api erroer", "songdb create error")

                            CoroutineScope(Dispatchers.Main).launch {
                                getInstance(context).songDao().insert(Songentitiy.Song(0,0, 0, "00"))
                                getInstance(context).songDao().insert(Songentitiy.Song(1,1, 0, "10"))
                                getInstance(context).songDao().insert(Songentitiy.Song(2,2, 0, "20"))
                                getInstance(context).songDao().insert(Songentitiy.Song(3,3, 0, "30"))
                                getInstance(context).songDao().insert(Songentitiy.Song(4,0, 1, "01"))
                                getInstance(context).songDao().insert(Songentitiy.Song(5,1, 1, "11"))
                                getInstance(context).songDao().insert(Songentitiy.Song(6,2, 1, "21"))
                                getInstance(context).songDao().insert(Songentitiy.Song(7,3, 1, "31"))
                                getInstance(context).songDao().insert(Songentitiy.Song(8,0, 2, "02"))
                                getInstance(context).songDao().insert(Songentitiy.Song(9,1, 2, "12"))
                                getInstance(context).songDao().insert(Songentitiy.Song(10,2, 2, "22"))
                                getInstance(context).songDao().insert(Songentitiy.Song(11, 3, 2, "32"))

                            }

                        }


                    }).build()
            }
        }

    }

}