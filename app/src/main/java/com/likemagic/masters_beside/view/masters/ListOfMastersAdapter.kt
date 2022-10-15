package com.likemagic.masters_beside.view.masters;

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.likemagic.masters_beside.databinding.ItemListMasterBinding
import com.likemagic.masters_beside.repository.Master

class ListOfMastersAdapter(private var list: List<Master> = listOf()): RecyclerView.Adapter<ListOfMastersAdapter.MasterViewHolder>(){

    fun setList(newList: List<Master>) {
        this.list = newList
    }

    inner class MasterViewHolder(view: View):RecyclerView.ViewHolder(view) {
        fun myBind(master: Master) {
            ItemListMasterBinding.bind(itemView).apply {
                masterNameListItem.text = master.name
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MasterViewHolder {
        val v = ItemListMasterBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MasterViewHolder(v.root)
    }

    override fun onBindViewHolder(holder: MasterViewHolder, position: Int) {
        holder.myBind(list[position])
    }

    override fun getItemCount() = list.size
}