package com.likemagic.masters_beside.database

import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.likemagic.masters_beside.repository.Master
import com.likemagic.masters_beside.repository.Profession
import com.likemagic.masters_beside.utils.DB_MASTER
import com.likemagic.masters_beside.utils.IMAGES

class DBManager {
    val storage = Firebase.storage.getReference(IMAGES)
    val masterBranch = Firebase.database.getReference(DB_MASTER)
    val accountBase = Firebase.auth

    fun addMaster(master: Master, listener: FinishWorkListener) {
        if (accountBase.uid != null) masterBranch.child(master.key ?: "empty")
            .child(accountBase.uid!!).child("master").setValue(master).addOnCompleteListener {
            if (it.isSuccessful) {
                listener.onFinish()
                masterBranch.child(master.key ?: "empty").child("zzz_master/profession")
                    .setValue(master.profession.name)
                masterBranch.child(master.key ?: "empty").child("zzz_master/city")
                    .setValue(master.city.name)
                masterBranch.child(master.key ?: "empty").child("zzz_master/uid")
                    .setValue(master.uid)
            }
        }
    }

    fun updateMaster(master: Master, updateCallBack: UpdateCallBack) {
        if (accountBase.uid != null) masterBranch.child(master.key ?: "empty")
            .child(accountBase.currentUser?.uid!!).child("master").setValue(master)
            .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                updateCallBack.updateData(master)
                masterBranch.child(master.key ?: "empty").child("zzz_master/profession")
                    .setValue(master.profession.name)
                masterBranch.child(master.key ?: "empty").child("zzz_master/city")
                    .setValue(master.city.name)
            }
        }
    }


    private fun readMastersFromDB(query: Query, readDataCallBack: ReadDataCallBack) {
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val tempList = ArrayList<Master>()
                for (item in snapshot.children) {
                    val master =
                        item.children.iterator().next().child("master").getValue(Master::class.java)
                    if (master != null) tempList.add(master)
                }
                readDataCallBack.readData(tempList)
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    fun getAllMasters(readCallBack: ReadDataCallBack) {
        val query = masterBranch
        readMastersFromDB(query, readCallBack)
    }

    fun getMastersByProfession(profession: Profession, readCallBack: ReadDataCallBack) {
        val query = masterBranch.orderByChild("/zzz_master/profession").equalTo(profession.name)
        readMastersFromDB(query, readCallBack)
    }

    fun getMasterByUID(uid: String, readCallBack: ReadDataCallBack) {
        val query = masterBranch.orderByChild("$uid/master/uid").equalTo(uid)
        readMastersFromDB(query, readCallBack)
    }

    fun getMyData(myDataCallback: MyDataCallback){
        masterBranch.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for (item in snapshot.children) {
                    val master = item.child("/${accountBase.currentUser?.uid}/master").getValue(Master::class.java)
                    if(master != null){
                        myDataCallback.myData(master)
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    fun deleteMaster(master: Master, listener: FinishWorkListener) {
        storage.child("${master.uid}/image${master.uid}").delete()
        masterBranch.child(master.key!!).removeValue().addOnCompleteListener {
            if (it.isSuccessful) {
                listener.onFinish()
            }
        }
    }

    fun addToOnline(master: Master) {
        master.lastVisit = System.currentTimeMillis()
        updateMaster(master){  }
    }

    fun getFavoriteMasters(master: Master, readFavoriteCallBack: ReadFavoriteCallBack) {
        val tempList = ArrayList<Master>()
        if(master.favorite.isEmpty()){
            readFavoriteCallBack.readFavorite(tempList)
        }else{
            for(uid:String in master.favorite){
                val query = masterBranch.orderByChild("/zzz_master/uid").equalTo(uid)
                query.addListenerForSingleValueEvent(object : ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        for (item in snapshot.children) {
                            val result = item.children.iterator().next().child("master").getValue(Master::class.java)
                            if(result != null){
                                tempList.add(result)
                            }
                        }
                        readFavoriteCallBack.readFavorite(tempList)
                    }
                    override fun onCancelled(error: DatabaseError) {}
                })
            }
        }
    }

    fun interface ReadDataCallBack {
        fun readData(list: ArrayList<Master>)
    }

    fun interface ReadFavoriteCallBack {
        fun readFavorite(list: ArrayList<Master>)
    }

    fun interface UpdateCallBack {
        fun updateData(master: Master)
    }

    fun interface FinishWorkListener {
        fun onFinish()
    }

    fun interface MyDataCallback {
        fun myData(master: Master)
    }

}