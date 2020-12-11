package com.sonicdutch.portal

import android.content.Intent
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_music_play.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class MusicPlayActivity : AppCompatActivity() {

    var song_url: String? = null
    var choice_number: Int? = null
    var mode = 0
    var playnow = true
    var list : Array<Songentitiy.Song>? = null

    lateinit var mediaPlayer: MediaPlayer

    var start_music = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_music_play)



        mediaPlayer = MediaPlayer().apply {
            setAudioStreamType(AudioManager.STREAM_MUSIC)
        }



        CoroutineScope(Dispatchers.Main).launch {

            list = SongDatabase.songDatabase.getInstance(this@MusicPlayActivity).songDao().getAll()
            //song_url = SongDatabase.songDatabase.getInstance(this@MusicPlayActivity).songDao().getUrl(start_music).url


            var adapter = Music_Adapter(list!!.toList())

            layout_list.adapter = adapter
            layout_list.setHasFixedSize(true)
            layout_list.layoutManager = LinearLayoutManager(this@MusicPlayActivity,RecyclerView.VERTICAL,false)

            adapter.setItemClickListener(object : Music_Adapter.ItemClickListener{

                override fun onClick(view: View, position: Int) {

                    if (mediaPlayer.isPlaying) {
                        mediaPlayer.stop()
                        mediaPlayer.reset()
                    }

                    mediaPlayer.reset()

                    /*mediaPlayer = MediaPlayer().apply {
                        setAudioStreamType(AudioManager.STREAM_MUSIC)
                    }*/


                    start_music = position+1

                    layout_bottom_btn.visibility = View.VISIBLE
                    btn_start.visibility = View.INVISIBLE


                    CoroutineScope(Dispatchers.Main).launch {
                        var choice_url = SongDatabase.songDatabase.getInstance(this@MusicPlayActivity).songDao().getUrl(start_music).url

                        play_song(choice_url)
                    }


                }

            })


        }.start()



        imv_music_home.setOnClickListener {


            if(mediaPlayer.isPlaying) {
                mediaPlayer.stop()
                mediaPlayer.reset()
                mediaPlayer.release()
            }

            var in_home = Intent(Intent.ACTION_VIEW, Uri.parse("https://sonicdutch.modoo.at/"))
            startActivity(in_home)
        }



        btn_start.setOnClickListener {

            layout_bottom_btn.visibility = View.VISIBLE
            btn_start.visibility = View.INVISIBLE



            if (start_music != 0 && mode == 1)
            {
               Log.e("check restart","$start_music")
                mode = 0


                CoroutineScope(Dispatchers.Main).launch {
                    var restart_url = SongDatabase.songDatabase.getInstance(this@MusicPlayActivity).songDao().getUrl(start_music).url

                    play_song(restart_url)
                }
            }


            else if (start_music != 0 && playnow == false)
            {
                mediaPlayer.start()
                playnow = true
            }

        }



        btn_pause.setOnClickListener {

            layout_bottom_btn.visibility = View.INVISIBLE
            btn_start.visibility = View.VISIBLE


            if(mediaPlayer.isPlaying) {
                mediaPlayer.pause()
                playnow = false
            }

        }


        btn_stop.setOnClickListener {

            layout_bottom_btn.visibility = View.INVISIBLE
            btn_start.visibility = View.VISIBLE


            if(mediaPlayer.isPlaying) {
                mediaPlayer.stop()
                mediaPlayer.reset()
                mode = 1
                Log.e("check","$start_music")
            }

        }



        btn_foward.setOnClickListener {

            ++start_music


            if (start_music >= list!!.size)
            {
                start_music = 1
                mediaPlayer.stop()
            }


            else
            {
                if (mediaPlayer.isPlaying) {
                    mediaPlayer.stop()
                    mediaPlayer.reset()
                }

                CoroutineScope(Dispatchers.Main).launch {

                    var url = SongDatabase.songDatabase.getInstance(this@MusicPlayActivity).songDao().getUrl(start_music).url
                    Log.e("check","$url")

                    play_song(url)
                }

                Log.e("check","$start_music")

            }



        }



        btn_back.setOnClickListener {

            --start_music

            if (start_music == 0)
            {
                start_music = 1
                mediaPlayer.stop()
            }


            else
            {
                if (mediaPlayer.isPlaying) {
                    mediaPlayer.stop()
                    mediaPlayer.reset()
                }

                CoroutineScope(Dispatchers.Main).launch {

                    var url = SongDatabase.songDatabase.getInstance(this@MusicPlayActivity).songDao().getUrl(start_music).url
                    Log.e("check","$url")

                    play_song(url)
                }

                Log.e("check","$start_music")

            }


        }


    }




    fun play_song(song_play: String)
    {

        try {

            //처음 기본 음악 재생.
            mediaPlayer.setDataSource(song_play)
            mediaPlayer.prepare()
            mediaPlayer.start()


            //한 곡의 재생이 끝났을 때
            mediaPlayer.setOnCompletionListener {
                ++start_music
                next_song()
            }

        }

        catch (e: Exception)
        {
            Toast.makeText(this@MusicPlayActivity,"음악을 재생할 수 없습니다. 관리자에게 문의 해주세요. ",Toast.LENGTH_SHORT).show()
        }

    }



    //다음곡 재생
    fun next_song()
    {

        if (start_music > list!!.size)
        {
            start_music = 1
            mediaPlayer.stop()
        }


        else
        {
            mediaPlayer.reset()

            CoroutineScope(Dispatchers.Main).launch {
                var url = SongDatabase.songDatabase.getInstance(this@MusicPlayActivity).songDao().getUrl(start_music).url
                Log.e("check","$url")

                play_song(url)
            }

            Log.e("check","$start_music")

        }

    }




}