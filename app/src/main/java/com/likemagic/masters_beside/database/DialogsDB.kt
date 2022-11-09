package com.likemagic.masters_beside.database

import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.likemagic.masters_beside.repository.Dialog
import com.likemagic.masters_beside.repository.Job
import com.likemagic.masters_beside.utils.DIALOGS_BRANCH

class DialogsDB {
    val dialogsBranch = Firebase.database.getReference(DIALOGS_BRANCH)
    private val accountBase = Firebase.auth

    fun createDialog(dialog: Dialog, listener: CreateDialogListener){
        if (accountBase.uid != null) dialogsBranch.child(dialog.key ?: "empty").setValue(dialog).addOnCompleteListener {
            listener.finishCreate(dialog)
        }
    }

    private fun readDialogsFromDB(query: Query, readDialogsCallBack: ReadDialogsCallBack) {
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val tempList = ArrayList<Dialog>()
                for (item in snapshot.children) {
                    val dialog =
                        item.getValue(Dialog::class.java)
                    if(dialog != null)tempList.add(dialog)
                }
                readDialogsCallBack.readData(tempList)
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    fun findDialog(members:String, readDialogsCallBack: ReadDialogsCallBack){
        val query = dialogsBranch.orderByChild("/members").equalTo(members)
        readDialogsFromDB(query, readDialogsCallBack)
    }

    fun getAllDialogs(readDialogsCallBack: ReadDialogsCallBack){
        val query = dialogsBranch
        readDialogsFromDB(query, readDialogsCallBack)
    }

    fun interface ReadDialogsCallBack {
        fun readData(list: ArrayList<Dialog>)
    }

    fun interface CreateDialogListener{
        fun finishCreate(dialog: Dialog)
    }


}