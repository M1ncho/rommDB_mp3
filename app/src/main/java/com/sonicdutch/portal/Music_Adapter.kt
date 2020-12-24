package com.sonicdutch.portal

import android.content.Context
import android.graphics.Color
import android.media.AudioManager
import android.media.MediaPlayer
import android.text.Selection
import android.view.*
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_music_play.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.collections.ArrayList


class Music_Adapter(private var songs: List<Songentitiy.Song>) : RecyclerView.Adapter<Music_Adapter.MyViewHolder>() {

    var list = ArrayList<Selection>()
    var mPosition = -1

    var start_music = 0
    lateinit var mediaPlayer: MediaPlayer


    class MyViewHolder(itemview: View) : RecyclerView.ViewHolder(itemview)
    {
        var songlist: TextView = itemview.findViewById(R.id.tv_songlist)

        fun bind(music: Songentitiy.Song) {
            songlist.text = music.name
        }

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        var context = parent.context
        var inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        var view = inflater.inflate(R.layout.playlist_item, parent, false)

        return MyViewHolder(view)
    }


    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(songs[position])

        if (mPosition == position)
        {
            holder.itemView.setBackgroundColor(Color.GRAY)
        }
        else
        {
            holder.itemView.setBackgroundColor(Color.WHITE)
        }


        holder.itemView.setOnClickListener {
            mPosition = position
            itemClickListner.onClick(it, position)
            notifyDataSetChanged()
        }

    }



    override fun getItemCount(): Int {
        return songs.size
    }



    //임의로 포지션 바꾸기?
    fun fowardClick(view: RecyclerView, position: Int) {
        var foward = position

        mPosition = foward

        if (mPosition == position)
        {
            var nextview= view.layoutManager?.findViewByPosition(foward)
            nextview!!.setBackgroundColor(Color.GRAY)
        }

        else
        {
            var views = view.layoutManager?.findViewByPosition(position)
            views!!.setBackgroundColor(Color.WHITE)
        }

        notifyDataSetChanged()

    }



    fun backClick(view: RecyclerView, position: Int) {
        var back = position

        mPosition = back

        if (mPosition == position)
        {
            var backviews = view.layoutManager?.findViewByPosition(back)
            backviews!!.setBackgroundColor(Color.GRAY)
        }

        else
        {
            var views = view.layoutManager?.findViewByPosition(position)
            views!!.setBackgroundColor(Color.WHITE)
        }

        notifyDataSetChanged()
    }




    fun nowclick(view: RecyclerView, position: Int) {

        mPosition = position

        if (mPosition == position)
        {
            var backviews = view.layoutManager?.findViewByPosition(mPosition)
            backviews!!.setBackgroundColor(Color.GRAY)
        }

        else
        {
            var views = view.layoutManager?.findViewByPosition(position)
            views!!.setBackgroundColor(Color.WHITE)
        }

        notifyDataSetChanged()

    }






    interface ItemClickListener {
        fun onClick(view: View, position: Int)
    }


    //클릭리스너 선언
    private lateinit var itemClickListner: ItemClickListener



    //클릭리스너 등록 매소드
    fun setItemClickListener(itemClickListener: ItemClickListener) {
        this.itemClickListner = itemClickListener
    }


    
}

