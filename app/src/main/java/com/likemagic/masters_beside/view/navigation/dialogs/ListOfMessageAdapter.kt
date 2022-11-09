package com.likemagic.masters_beside.view.navigation.dialogs

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout.*
import androidx.recyclerview.widget.RecyclerView
import com.likemagic.masters_beside.databinding.ItemListDialogMessageBinding
import com.likemagic.masters_beside.repository.DialogMessage
import com.likemagic.masters_beside.repository.Master
import com.likemagic.masters_beside.utils.convertLongToTime


class ListOfMessageAdapter(private var list: List<DialogMessage> = listOf(),
                           var myData:Master, private val context:Context)
    : RecyclerView.Adapter<ListOfMessageAdapter.DialogMessageViewHolder>(){

    fun setList(newList: List<DialogMessage>) {
        this.list = newList
        notifyDataSetChanged()
    }

    inner class DialogMessageViewHolder(view: View):RecyclerView.ViewHolder(view) {
        val binding = ItemListDialogMessageBinding.bind(itemView)
        fun myBind(dialogMessage: DialogMessage) {
            binding.apply {
                if(dialogMessage.owner == myData.uid){
                    messageRecipientContainer.visibility = GONE
                    messageSenderContainer.visibility = VISIBLE
                    messageSender.text = dialogMessage.message
                    messageSenderTime.text = convertLongToTime(dialogMessage.time)
                    messageRecipientTime.visibility = GONE
                }else{
                    messageSenderContainer.visibility = GONE
                    messageRecipientContainer.visibility = VISIBLE
                    messageRecipient.text = dialogMessage.message
                    messageRecipientTime.text = convertLongToTime(dialogMessage.time)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DialogMessageViewHolder {
        val v = ItemListDialogMessageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DialogMessageViewHolder(v.root)
    }

    override fun onBindViewHolder(holder: DialogMessageViewHolder, position: Int) {
        holder.myBind(list[position])
    }

    override fun getItemCount() = list.size
}