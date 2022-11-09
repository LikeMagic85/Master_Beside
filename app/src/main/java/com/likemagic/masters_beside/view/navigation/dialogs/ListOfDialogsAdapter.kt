package com.likemagic.masters_beside.view.navigation.dialogs

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import coil.dispose
import coil.load
import com.likemagic.masters_beside.R
import com.likemagic.masters_beside.databinding.ItemListDialogsBinding
import com.likemagic.masters_beside.databinding.ItemListSearchBinding
import com.likemagic.masters_beside.repository.Dialog
import com.likemagic.masters_beside.repository.Master
import com.likemagic.masters_beside.repository.Profession
import com.likemagic.masters_beside.utils.NOT_MASTER

class ListOfDialogsAdapter(private var list: List<Dialog> = listOf(),var myData:Master, private val context:Context): RecyclerView.Adapter<ListOfDialogsAdapter.DialogViewHolder>(){

    var onItemClick: ((Dialog) -> Unit)? = null
    var onBtnClick: ((Dialog, v:View) -> Unit)? = null
    fun setList(newList: List<Dialog>) {
        this.list = newList
        notifyDataSetChanged()
    }

    inner class DialogViewHolder(view: View):RecyclerView.ViewHolder(view) {
        val binding = ItemListDialogsBinding.bind(itemView)
        fun myBind(dialog: Dialog) {
            binding.apply {
                if(myData.uid == dialog.senderUid){
                    binding.masterNameListItem.text = dialog.recipientName
                }else{
                    binding.masterNameListItem.text = dialog.senderName
                }
                if(myData.uid == dialog.senderUid){
                    if(dialog.recipientProfession == NOT_MASTER){
                        binding.masterProfessionListItem.text = "Заказчик"
                    }else{
                        binding.masterProfessionListItem.text = dialog.recipientProfession
                    }
                }else{
                    if(dialog.senderProfession == NOT_MASTER){
                        binding.masterProfessionListItem.text = "Заказчик"
                    }else{
                        binding.masterProfessionListItem.text = dialog.senderProfession
                    }
                }
                if(myData.uid == dialog.senderUid){
                    if(dialog.recipientUri.isNotEmpty()){
                        dialogRecipientPhoto.load(dialog.recipientUri)
                    }else{
                        dialogRecipientPhoto.dispose()
                        dialogRecipientPhoto.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_account))
                    }
                }else{
                    if(dialog.senderUri.isNotEmpty()){
                        dialogRecipientPhoto.load(dialog.senderUri)
                    }else{
                        dialogRecipientPhoto.dispose()
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DialogViewHolder {
        val v = ItemListDialogsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DialogViewHolder(v.root)
    }

    override fun onBindViewHolder(holder: DialogViewHolder, position: Int) {
        holder.apply{
            myBind(list[position])
            binding.itemDialogContainer.setOnClickListener {
                onItemClick!!.invoke(list[position])
            }
            binding.pushMenuBtn.setOnClickListener {
                onBtnClick!!.invoke(list[position], binding.pushMenuBtn)
            }
        }
    }

    override fun getItemCount() = list.size
}