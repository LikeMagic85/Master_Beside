package com.likemagic.masters_beside.view.masters;

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.likemagic.masters_beside.databinding.ItemListMasterBinding
import com.likemagic.masters_beside.repository.Master

class ListOfMastersAdapter(private var list: List<Master> = listOf()): RecyclerView.Adapter<ListOfMastersAdapter.MasterViewHolder>(){

    var onItemClick: ((Master) -> Unit)? = null

    fun setList(newList: List<Master>) {
        this.list = newList
        notifyDataSetChanged()
    }

    inner class MasterViewHolder(view: View):RecyclerView.ViewHolder(view) {
        val binding = ItemListMasterBinding.bind(itemView)
        fun myBind(master: Master) {
            binding.apply {
                masterNameListItem.text = master.name
                cityItemList.text = master.city.name
                professionItemList.text = master.profession.name
                if(!master.uriImage.isNullOrEmpty()){
                    masterPhotoListItem.load(master.uriImage)
                }
                countReviewsListItem.text = master.reviews.size.toString()
                gradleListItem.text = master.rating.toString()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MasterViewHolder {
        val v = ItemListMasterBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MasterViewHolder(v.root)
    }

    override fun onBindViewHolder(holder: MasterViewHolder, position: Int) {
        holder.apply {
            myBind(list[position])
            binding.itemMasterContainer.setOnClickListener {
                onItemClick!!.invoke(list[position])
            }
        }
    }

    override fun getItemCount() = list.size
}