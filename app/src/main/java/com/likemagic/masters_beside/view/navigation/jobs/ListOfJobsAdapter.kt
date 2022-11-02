package com.likemagic.masters_beside.view.navigation.jobs

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.dispose
import coil.load
import com.likemagic.masters_beside.databinding.ItemListJobBinding
import com.likemagic.masters_beside.databinding.ItemListSearchBinding
import com.likemagic.masters_beside.repository.Job
import com.likemagic.masters_beside.repository.Profession
import com.likemagic.masters_beside.utils.JobTextHelper

class ListOfJobsAdapter(private var list: List<Job> = listOf()): RecyclerView.Adapter<ListOfJobsAdapter.JobsViewHolder>(){

    var onItemClick: ((Job) -> Unit)? = null
    fun setList(newList: List<Job>) {
        this.list = newList
        notifyDataSetChanged()
    }

    inner class JobsViewHolder(view: View):RecyclerView.ViewHolder(view) {
        val binding = ItemListJobBinding.bind(itemView)
        fun myBind(job: Job) {
            if(job.uriImage.isNotEmpty()){
                binding.masterPhotoListItem.load(job.uriImage)
            }else{
                binding.masterPhotoListItem.dispose()
            }
            binding.jobNameListItem.text = job.name
            if (job.cost.isNotEmpty()){
                binding.itemCost.text = "Оплата ${job.cost} BYN"
            }else{
                binding.itemCost.text = "Договорная"
            }
            binding.cityItemList.text = job.city.name
            binding.jobCreateTimeListItem. text = JobTextHelper().repairText(((System.currentTimeMillis() - job.date)/60000).toInt())
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JobsViewHolder {
        val v = ItemListJobBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return JobsViewHolder(v.root)
    }

    override fun onBindViewHolder(holder: JobsViewHolder, position: Int) {
        holder.apply{
            myBind(list[position])
            binding.itemJobContainer.setOnClickListener {
                onItemClick!!.invoke(list[position])
            }
        }
    }

    override fun getItemCount() = list.size
}