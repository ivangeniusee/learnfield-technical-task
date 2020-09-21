package com.geniusee.learnfield.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.geniusee.learnfield.R
import com.geniusee.learnfield.model.playlist.item.AudioPlayItem
import kotlinx.android.synthetic.main.view_item_play_item.view.*

class PlayItemsAdapter : RecyclerView.Adapter<PlayItemsAdapter.ViewHolder>() {

    var items = emptyList<AudioPlayItem>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.view_item_play_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(item: AudioPlayItem) {
            itemView.nameTextView.text = item.name
        }
    }
}