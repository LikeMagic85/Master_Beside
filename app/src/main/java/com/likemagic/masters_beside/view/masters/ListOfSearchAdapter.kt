package com.likemagic.masters_beside.view.masters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.likemagic.masters_beside.databinding.ItemListSearchBinding
import com.likemagic.masters_beside.repository.Profession

class ListOfSearchAdapter(private var list: List<Profession> = listOf()): RecyclerView.Adapter<ListOfSearchAdapter.SearchViewHolder>(){

    var onItemClick: ((Profession) -> Unit)? = null
    fun setList(newList: List<Profession>) {
        this.list = newList
        notifyDataSetChanged()
    }

    inner class SearchViewHolder(view: View):RecyclerView.ViewHolder(view) {
        val binding = ItemListSearchBinding.bind(itemView)
        fun myBind(profession: Profession) {
            binding.apply {
                searchChoose.text = profession.name
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        val v = ItemListSearchBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SearchViewHolder(v.root)
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        holder.apply{
            myBind(list[position])
            binding.searchChoose.setOnClickListener {
                onItemClick!!.invoke(list[position])
            }
        }
    }

    override fun getItemCount() = list.size
}