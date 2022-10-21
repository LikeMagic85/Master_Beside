package com.likemagic.masters_beside.database

import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.likemagic.masters_beside.repository.City
import com.likemagic.masters_beside.repository.Master
import com.likemagic.masters_beside.repository.Profession
import com.likemagic.masters_beside.repository.User
import com.likemagic.masters_beside.utils.DB_MASTER
import com.likemagic.masters_beside.utils.DB_USER
import com.likemagic.masters_beside.utils.IMAGES

class DBManager() {
    val storage = Firebase.storage.getReference(IMAGES)
    val masterBranch = Firebase.database.getReference(DB_MASTER)
    val userBranch  = Firebase.database.getReference(DB_USER)
    val auth = Firebase.auth

    fun addMaster(master: Master, listener: FinishWorkListener){
        if(auth.uid!=null)masterBranch.child(master.key?: "empty").child(auth.uid!!).child("master").setValue(master).addOnCompleteListener {
            if (it.isSuccessful){
                masterBranch.child(master.key?: "empty").child("master").setValue(master)
                listener.onFinish()
            }
        }
        // TODO: РЕШИТЬ ВОПРОС С ФИЛЬТРАЦИЕЙ
    }

    fun updateMaster(master:Master, updateCallBack: UpdateCallBack){
        if(auth.uid!=null)masterBranch.child(master.key?: "empty").child(auth.uid!!).child("master").setValue(master).addOnCompleteListener { task ->
            if (task.isSuccessful){
                updateCallBack.updateData(master)
            }
        }
    }

    fun addUser(user: User){
        userBranch.setValue(user)
    }

    private fun readMastersFromDB(query: Query, readDataCallBack: ReadDataCallBack){
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val tempList = ArrayList<Master>()
                for(item in snapshot.children) {
                    val master = item.children.iterator().next().child("master").getValue(Master::class.java)
                    if (master != null) tempList.add(master)
                }
                readDataCallBack.readData(tempList)
            }
            override fun onCancelled(error: DatabaseError) {
                // TODO:
            }
        })
    }

    fun getAllMasters(readCallBack:ReadDataCallBack){
        val query = masterBranch
        readMastersFromDB(query, readCallBack)
    }

    fun getMastersByProfession(profession: Profession, readCallBack: ReadDataCallBack){
        val query = masterBranch.orderByChild("/master/profession/name").equalTo(profession.name)
        readMastersFromDB(query, readCallBack)
    }

    fun getMastersByCity(city: City, readCallBack: ReadDataCallBack){
        val query = masterBranch.orderByChild("/master/city").equalTo(city.name)
        readMastersFromDB(query, readCallBack)
    }

    fun getMasterByUID(uid:String, readCallBack: ReadDataCallBack){
        val query = masterBranch.orderByChild("$uid/master/uid").equalTo(uid)
        readMastersFromDB(query, readCallBack)
    }

    fun deleteMaster(master:Master, listener:FinishWorkListener){
        storage.child("Y7zafNTYNsZW5j1I8cOAVFfRWcC2").delete()
        masterBranch.child(master.key!!).removeValue().addOnCompleteListener {
            if(it.isSuccessful){
                listener.onFinish()
            }
        }
    }

    interface ReadDataCallBack {
        fun readData(list:List<Master>)
    }

    interface UpdateCallBack {
        fun updateData(master: Master)
    }

    fun interface FinishWorkListener {
        fun onFinish()
    }

}