package com.sonicdutch.portal

import android.content.Intent
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_music_play.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MusicPlayActivity : AppCompatActivity() {

    var song_url: String? = null
    var choice_number: String? = null
    var mode = 0
    var playnow = true
    var list : Array<Songentitiy.Song>? = null
    var adapter: Music_Adapter? = null

    var rv: RecyclerView? = null

    lateinit var mediaPlayer: MediaPlayer

    var start_music = 0



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_music_play)

        song_url = intent.getStringExtra("url")
        choice_number = intent.getStringExtra("id")


        mediaPlayer = MediaPlayer().apply {
            setAudioStreamType(AudioManager.STREAM_MUSIC)
        }


        CoroutineScope(Dispatchers.Main).launch {
            list = SongDatabase.songDatabase.getInstance(this@MusicPlayActivity).songDao().getAll()

            adapter = Music_Adapter(list!!.toList())

            rv = layout_list

            rv!!.adapter = adapter
            rv!!.setHasFixedSize(true)
            rv!!.layoutManager = LinearLayoutManager(this@MusicPlayActivity, RecyclerView.VERTICAL, false)

            adapter!!.setItemClickListener(object : Music_Adapter.ItemClickListener{

                override fun onClick(view: View, position: Int) {

                    //rv!!.scrollToPosition(position)

                    if (mediaPlayer.isPlaying) {
                        mediaPlayer.stop()
                        mediaPlayer.reset()
                    }

                    else {
                        mediaPlayer = MediaPlayer().apply {
                            setAudioStreamType(AudioManager.STREAM_MUSIC)
                        }
                    }

                    mediaPlayer.reset()
                    start_music = position + 1

                    layout_bottom_btn.visibility = View.VISIBLE
                    btn_start.visibility = View.INVISIBLE


                    CoroutineScope(Dispatchers.Main).launch {
                        var choice_url = SongDatabase.songDatabase.getInstance(this@MusicPlayActivity).songDao().getUrl(start_music).url
                        play_song(choice_url)
                    }
                }
            })

        }.start()





        if (song_url != null && choice_number != null)
        {
            start_music = choice_number!!.toInt()

            layout_bottom_btn.visibility = View.VISIBLE
            btn_start.visibility = View.INVISIBLE

            play_song(song_url!!)


            Log.e("check", "$start_music")
        }




        imv_music_home.setOnClickListener {

            if (mediaPlayer.isPlaying) {
                mediaPlayer.stop()
                mediaPlayer.release()
                mode = 2
                layout_bottom_btn.visibility = View.INVISIBLE
                btn_start.visibility = View.VISIBLE
            }

            var in_home = Intent(Intent.ACTION_VIEW, Uri.parse("https://sonicdutch.modoo.at/"))
            startActivity(in_home)
        }



        btn_start.setOnClickListener {

            layout_bottom_btn.visibility = View.VISIBLE
            btn_start.visibility = View.INVISIBLE

            if (start_music != 0 && mode == 1)
            {
               Log.e("check restart", "$start_music")
                mode = 0

                CoroutineScope(Dispatchers.Main).launch {
                    var restart_url = SongDatabase.songDatabase.getInstance(this@MusicPlayActivity).songDao().getUrl(
                        start_music
                    ).url
                    play_song(restart_url)
                }
            }


            if (start_music != 0 && mode == 2)
            {
                mediaPlayer = MediaPlayer().apply {
                    setAudioStreamType(AudioManager.STREAM_MUSIC)
                }

                mode = 0

                CoroutineScope(Dispatchers.Main).launch {
                    var restart_url = SongDatabase.songDatabase.getInstance(this@MusicPlayActivity).songDao().getUrl(
                        start_music
                    ).url
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
                Log.e("check", "$start_music")
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

                else
                {
                    mediaPlayer.reset()
                }

                adapter!!.fowardClick(rv!!, start_music -1)


                mediaPlayer.reset()
                CoroutineScope(Dispatchers.Main).launch {
                    var url = SongDatabase.songDatabase.getInstance(this@MusicPlayActivity).songDao().getUrl(start_music).url
                    play_song(url)
                }

                Log.e("check", "$start_music")
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

                else
                {
                    mediaPlayer.reset()
                }

                adapter!!.backClick(rv!!, start_music -1)


                mediaPlayer.reset()
                CoroutineScope(Dispatchers.Main).launch {
                    var url = SongDatabase.songDatabase.getInstance(this@MusicPlayActivity).songDao().getUrl(start_music).url
                    play_song(url)
                }

                Log.e("check", "$start_music")
            }
        }
    }




    fun play_song(song_play: String)
    {

        try {
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
            Toast.makeText(this@MusicPlayActivity, "음악을 재생할 수 없습니다. 관리자에게 문의 해주세요. ", Toast.LENGTH_SHORT).show()
        }
    }



    fun next_song()
    {

        if (start_music > list!!.size)
        {
            start_music = 1
            mediaPlayer.stop()
        }

        else
        {
            mediaPlayer.reset()                                             //재시작을 원할 시 reset()필수

            adapter!!.fowardClick(rv!!, start_music -1)


            CoroutineScope(Dispatchers.Main).launch {
                var url = SongDatabase.songDatabase.getInstance(this@MusicPlayActivity).songDao().getUrl(start_music).url
                play_song(url)
            }
            Log.e("check", "$start_music")
        }
    }



}