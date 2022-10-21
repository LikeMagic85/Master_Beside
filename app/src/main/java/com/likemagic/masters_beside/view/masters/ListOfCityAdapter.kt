package com.likemagic.masters_beside.view.masters;

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.likemagic.masters_beside.databinding.ItemListCityBinding
import com.likemagic.masters_beside.repository.City

class ListOfCityAdapter(private var list: List<City> = listOf()): RecyclerView.Adapter<ListOfCityAdapter.CityViewHolder>(){

    var onItemClick: ((City) -> Unit)? = null
    fun setList(newList: List<City>) {
        this.list = newList
        notifyDataSetChanged()
    }

    inner class CityViewHolder(view: View):RecyclerView.ViewHolder(view) {
        val binding = ItemListCityBinding.bind(itemView)
        fun myBind(city: City) {
            binding.apply {
                cityChoose.text = city.name
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CityViewHolder {
        val v = ItemListCityBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CityViewHolder(v.root)
    }

    override fun onBindViewHolder(holder: CityViewHolder, position: Int) {
        holder.apply{
            myBind(list[position])
            binding.cityChoose.setOnClickListener {
                onItemClick!!.invoke(list[position])
            }
        }
    }

    override fun getItemCount() = list.size
}