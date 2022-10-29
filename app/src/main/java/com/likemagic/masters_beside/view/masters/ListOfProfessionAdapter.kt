package com.likemagic.masters_beside.view.masters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.likemagic.masters_beside.databinding.ItemListProfessionBinding
import com.likemagic.masters_beside.repository.Profession

class ListOfProfessionAdapter(private var list: List<Profession> = listOf()): RecyclerView.Adapter<ListOfProfessionAdapter.ProfessionViewHolder>(){

    var onItemClick: ((Profession) -> Unit)? = null
    fun setList(newList: List<Profession>) {
        this.list = newList
        notifyDataSetChanged()
    }

    inner class ProfessionViewHolder(view: View):RecyclerView.ViewHolder(view) {
        val binding = ItemListProfessionBinding.bind(itemView)
        fun myBind(profession: Profession) {
            binding.apply {
                profChoose.text = profession.name
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfessionViewHolder {
        val v = ItemListProfessionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProfessionViewHolder(v.root)
    }

    override fun onBindViewHolder(holder: ProfessionViewHolder, position: Int) {
        holder.apply{
            myBind(list[position])
            binding.profChoose.setOnClickListener {
                onItemClick!!.invoke(list[position])
            }
        }
    }

    override fun getItemCount() = list.size
}