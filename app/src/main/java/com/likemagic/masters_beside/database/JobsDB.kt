package com.likemagic.masters_beside.database

import android.util.Log
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.likemagic.masters_beside.repository.Job
import com.likemagic.masters_beside.repository.Master
import com.likemagic.masters_beside.utils.JOBS_BRANCH

class JobsDB {
    val jobsBranch = Firebase.database.getReference(JOBS_BRANCH)
    private val accountBase = Firebase.auth

    fun addJob(job: Job, listener: MastersDB.FinishWorkListener){
        if (accountBase.uid != null) jobsBranch.child(job.key ?: "empty")
            .child(accountBase.uid!!).child("job").setValue(job).addOnCompleteListener {
                if (it.isSuccessful) {
                    listener.onFinish()
                }
            }
    }

    fun updateJob(job: Job, updateCallBack: UpdateCallBack){
        if (accountBase.uid != null) jobsBranch.child(job.key ?: "empty")
            .child(accountBase.uid!!).child("job").setValue(job).addOnCompleteListener {
                if (it.isSuccessful) {
                    updateCallBack.updateData(job)
                }
            }
    }

    private fun readJobsFromDB(query: Query, readJobsDataCallBack: ReadJobsDataCallBack) {
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val tempList = ArrayList<Job>()
                for (item in snapshot.children) {
                    val job =
                        item.children.iterator().next().child("job").getValue(Job::class.java)
                    if (job != null) tempList.add(job)
                }
                readJobsDataCallBack.readData(tempList)
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    fun getAllJobs(readJobsDataCallBack: ReadJobsDataCallBack) {
        val query = jobsBranch
        readJobsFromDB(query, readJobsDataCallBack)
    }

    fun getJobByUID(uid: String, id:String, readJobsDataCallBack: ReadJobsDataCallBack) {
        val query = jobsBranch.orderByChild("$uid/job/id").equalTo(id)
        readJobsFromDB(query, readJobsDataCallBack)
    }

    fun deleteJob(job: Job, listener: MastersDB.FinishWorkListener) {
        jobsBranch.child(job.key!!).removeValue().addOnCompleteListener {
            if (it.isSuccessful) {
                listener.onFinish()
            }
        }
    }

    fun interface ReadJobsDataCallBack {
        fun readData(list: ArrayList<Job>)
    }

    fun interface UpdateCallBack {
        fun updateData(job: Job)
    }

}