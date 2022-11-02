package com.likemagic.masters_beside.view.masters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import coil.dispose
import coil.load
import com.likemagic.masters_beside.R
import com.likemagic.masters_beside.databinding.ItemListMasterBinding
import com.likemagic.masters_beside.repository.Master
import com.likemagic.masters_beside.utils.NOT_MASTER
import com.likemagic.masters_beside.utils.OnlineTextHelper

class ListOfMastersAdapter(private var list: ArrayList<Master> = arrayListOf() , var myData: Master, private val context:Context): RecyclerView.Adapter<ListOfMastersAdapter.MasterViewHolder>(){

    var onItemClick: ((Master) -> Unit)? = null

    fun setList(newList: ArrayList<Master>) {
        list = newList
        notifyDataSetChanged()
    }

    inner class MasterViewHolder(view: View):RecyclerView.ViewHolder(view) {
        val binding = ItemListMasterBinding.bind(itemView)
        fun myBind(master: Master) {
            binding.apply {
                masterNameListItem.text = master.name
                cityItemList.text = master.city.name
                professionItemList.text = master.profession.name
                if(master.uriImage.isEmpty()){
                    masterPhotoListItem.dispose()
                    masterPhotoListItem.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.ic_account))

                }else{
                    masterPhotoListItem.setImageDrawable (null)
                    masterPhotoListItem.load(master.uriImage)
                }
                countReviewsListItem.text = master.reviews.size.toString()
                gradleListItem.text = master.rating.toString()
                itemCost.text = "от ${master.cost} BYN"
                onlineListItem.text = OnlineTextHelper().repairText(((System.currentTimeMillis() - master.lastVisit)/ 60000).toInt())
                if(onlineListItem.text == "Онлайн"){
                    onlineListItem.setTextColor(ContextCompat.getColor(context, R.color.green))
                }else{
                    onlineListItem.setTextColor(ContextCompat.getColor(context, R.color.black))
                }
                if(master.vipStatus){
                    itemMasterContainer.setCardBackgroundColor(ContextCompat.getColor(context, R.color.vip_color))
                    vipImageListItem.visibility = VISIBLE
                }else{
                    itemMasterContainer.setCardBackgroundColor(ContextCompat.getColor(context, R.color.white))
                    vipImageListItem.visibility = GONE
                }
                if(master.uid in myData.favorite){
                    binding.favoriteItemList.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_favorite))
                }else{binding.favoriteItemList.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_not_favorite))}
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