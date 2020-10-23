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

                                Log.e("database start","make")

                                getInstance(context).songDao().insert(Songentitiy.Song(0,0, 0, "http://kccba.net/sonicdutch/mp3/M0001.mp3"))
                                getInstance(context).songDao().insert(Songentitiy.Song(1,1, 0, "http://kccba.net/sonicdutch/mp3/M0002-3.mp3"))
                                getInstance(context).songDao().insert(Songentitiy.Song(2,2, 0, "http://kccba.net/sonicdutch/mp3/M0003-1.mp3"))
                                getInstance(context).songDao().insert(Songentitiy.Song(3,3, 0, "http://kccba.net/sonicdutch/mp3/M0004.mp3"))
                                getInstance(context).songDao().insert(Songentitiy.Song(4,0, 1, "http://kccba.net/sonicdutch/mp3/M0005.mp3"))
                                getInstance(context).songDao().insert(Songentitiy.Song(5,1, 1, "http://kccba.net/sonicdutch/mp3/M0006.mp3"))
                                getInstance(context).songDao().insert(Songentitiy.Song(6,2, 1, "http://kccba.net/sonicdutch/mp3/M0007-2.mp3"))
                                getInstance(context).songDao().insert(Songentitiy.Song(7,3, 1, "http://kccba.net/sonicdutch/mp3/M0008-2.mp3"))
                                getInstance(context).songDao().insert(Songentitiy.Song(8,0, 2, "http://kccba.net/sonicdutch/mp3/M0009.mp3"))
                                getInstance(context).songDao().insert(Songentitiy.Song(9,1, 2, "http://kccba.net/sonicdutch/mp3/M0010.mp3"))
                                getInstance(context).songDao().insert(Songentitiy.Song(10,2, 2, "http://kccba.net/sonicdutch/mp3/M0011.mp3"))
                                getInstance(context).songDao().insert(Songentitiy.Song(11, 3, 2, "http://kccba.net/sonicdutch/mp3/M0012-2.mp3"))

                                Log.e("test data","songdb create end")

                            }


                        }

                        //onopen을 써바?



                    }).build()
            }
        }

    }

}