package com.sonicdutch.portal

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class Music_Adapter (var songs: List<Songentitiy.Song>) : RecyclerView.Adapter<Music_Adapter.MyViewHolder>() {


    inner class MyViewHolder(itemview: View) : RecyclerView.ViewHolder(itemview)
    {
        var songlist: TextView = itemview.findViewById(R.id.tv_songlist)

        fun bind(music: Songentitiy.Song)
        {
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

        holder.itemView.setOnClickListener {
            itemClickListner.onClick(it, position)
        }

    }


    override fun getItemCount(): Int {
        return songs.size
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