package com.likemagic.masters_beside.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.likemagic.masters_beside.database.DialogsDB
import com.likemagic.masters_beside.repository.Dialog
import com.likemagic.masters_beside.repository.DialogMessage
import com.likemagic.masters_beside.repository.Master


class DialogsViewModel: ViewModel() {
    private val liveData: MutableLiveData<DialogState> by lazy { MutableLiveData<DialogState>() }
    private val dataBase = DialogsDB()

    fun getLiveData(): LiveData<DialogState> {
        return liveData
    }

    fun findDialog(members:String, sender:Master, recipient:Master){
        dataBase.findDialog(members){list->
            if(list.isEmpty()){
                val reverse = "${members.substring(28..55)}${members.substring(0..27)}"
                dataBase.findDialog(reverse){rList ->
                    if(rList.isEmpty()){
                        val dialog = Dialog(members, sender.name, recipient.name,
                            sender.profession.name, recipient.profession.name, sender.uriImage, recipient.uriImage,
                            sender.uid!!, recipient.uid!!)
                        createDialog(dialog)
                    }
                    else{
                        liveData.postValue(DialogState.DialogPage(rList[0]))
                    }
                }
            }else{
                liveData.postValue(DialogState.DialogPage(list[0]))
            }
        }
    }

    private fun createDialog(dialog: Dialog){
        dialog.key = dataBase.dialogsBranch.push().key!!
        dataBase.createDialog(dialog){result ->
            liveData.postValue(DialogState.DialogPage(result))
        }
    }

    fun getMyDialogs(uid:String){
        liveData.postValue(DialogState.Loading)
        dataBase.getAllDialogs(){list->
            val tempList = arrayListOf<Dialog>()
            for(item in list){
                if(item.members.contains(uid)){
                    tempList.add(item)
                }
            }
            liveData.postValue(DialogState.ListOfDialogs(tempList))
        }
    }

    fun addMessageToDialog(message: DialogMessage, dialog: Dialog){
        dialog.listOfMessage.add(message)
        dataBase.createDialog(dialog){
            liveData.postValue(DialogState.DialogPage(it))
        }
    }

    fun deleteDialog(dialog: Dialog){

    }
}