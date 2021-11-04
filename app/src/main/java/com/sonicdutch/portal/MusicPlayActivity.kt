package com.sonicdutch.portal

import android.content.Context
import android.content.Intent
import android.graphics.PointF
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewTreeObserver
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
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


            rv!!.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener{
                override fun onGlobalLayout() {

                    start_music = choice_number!!.toInt()

                    layout_bottom_btn.visibility = View.VISIBLE
                    btn_start.visibility = View.INVISIBLE
                    play_song(song_url!!)


                    if (start_music == 1) {
                        rv!!.smoothSnapToPosition(start_music-1)
                    }
                    else {
                        rv!!.smoothSnapToPosition(start_music-2)
                    }


                    try
                    {
                        adapter!!.choiceClick( rv!!,start_music-1)
                    }
                    catch (e: Exception) {
                        Log.e("notice","$e")
                    }

                    rv!!.viewTreeObserver.removeOnGlobalLayoutListener(this)
                }
            })


            adapter!!.setItemClickListener(object : Music_Adapter.ItemClickListener{

                override fun onClick(view: View, position: Int) {

                    if (position == 0) {
                        rv!!.smoothSnapToPosition(position)
                    }
                    else {
                        rv!!.smoothSnapToPosition(position-1)
                    }


                    if (mediaPlayer.isPlaying) {
                        mediaPlayer.stop()
                        mediaPlayer.reset()
                    }

                    else {
                        mediaPlayer.reset()
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
               //Log.e("check restart", "$start_music")
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
                mediaPlayer.reset()

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

            if (start_music >= list!!.size) {
                start_music = 1
                mediaPlayer.stop()
                rv!!.smoothSnapToPosition(start_music-1)
            }

            else {

                if (mediaPlayer.isPlaying) {
                    mediaPlayer.stop()
                    mediaPlayer.reset()
                }

                else
                {
                    layout_bottom_btn.visibility = View.VISIBLE
                    btn_start.visibility = View.INVISIBLE
                    mediaPlayer.reset()
                }

                rv!!.smoothSnapToPosition(start_music-2)

                adapter!!.choiceClick(rv!!, start_music-1)

                mediaPlayer.reset()
                CoroutineScope(Dispatchers.Main).launch {
                    var url = SongDatabase.songDatabase.getInstance(this@MusicPlayActivity).songDao().getUrl(start_music).url
                    play_song(url)
                }
            }
        }



        btn_back.setOnClickListener {
            --start_music

            if (start_music == 0) {
                start_music = 1
                mediaPlayer.stop()
                rv!!.smoothSnapToPosition(start_music-1)
            }

            else if (start_music == 1) {
                mediaPlayer.reset()
                rv!!.smoothSnapToPosition(start_music-1)
                adapter!!.choiceClick(rv!!, start_music-1)

                CoroutineScope(Dispatchers.Main).launch {
                    var url = SongDatabase.songDatabase.getInstance(this@MusicPlayActivity).songDao().getUrl(start_music).url
                    play_song(url)
                }
            }

            else {

                if (mediaPlayer.isPlaying) {
                    mediaPlayer.stop()
                    mediaPlayer.reset()
                }

                else
                {
                    layout_bottom_btn.visibility = View.VISIBLE
                    btn_start.visibility = View.INVISIBLE
                    mediaPlayer.reset()
                }

                rv!!.smoothSnapToPosition(start_music-2)

                adapter!!.choiceClick(rv!!, start_music-1)

                mediaPlayer.reset()
                CoroutineScope(Dispatchers.Main).launch {
                    var url = SongDatabase.songDatabase.getInstance(this@MusicPlayActivity).songDao().getUrl(start_music).url
                    play_song(url)
                }
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
            Log.e("error song","$e")
        }
    }



    fun next_song()
    {

        if (start_music > list!!.size)
        {
            start_music = 1
            mediaPlayer.stop()
            rv!!.smoothSnapToPosition(start_music-1)
        }

        else
        {
            mediaPlayer.reset()                                             //재시작을 원할 시 reset()필수

            adapter!!.choiceClick(rv!!, start_music -1)
            rv!!.smoothSnapToPosition(start_music)

            CoroutineScope(Dispatchers.Main).launch {
                var url = SongDatabase.songDatabase.getInstance(this@MusicPlayActivity).songDao().getUrl(start_music).url
                play_song(url)
            }
            Log.e("check", "$start_music")
        }
    }




    fun RecyclerView.smoothSnapToPosition(position: Int, snapMode: Int = LinearSmoothScroller.SNAP_TO_START) {
        val smoothScroller = object : LinearSmoothScroller(this.context) {
            override fun getVerticalSnapPreference(): Int = snapMode
            override fun getHorizontalSnapPreference(): Int = snapMode
        }
        smoothScroller.targetPosition = position
        layoutManager?.startSmoothScroll(smoothScroller)
    }




    override fun onDestroy() {

        if (mediaPlayer.isPlaying) {
            mediaPlayer.stop()
            mediaPlayer.release()
        }
        super.onDestroy()
    }



    override fun onResume() {
        super.onResume()
        mediaPlayer = MediaPlayer().apply {
            setAudioStreamType(AudioManager.STREAM_MUSIC)
        }
    }






}