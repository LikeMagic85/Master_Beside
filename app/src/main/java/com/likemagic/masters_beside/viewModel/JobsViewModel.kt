package com.likemagic.masters_beside.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.likemagic.masters_beside.database.JobsDB
import com.likemagic.masters_beside.repository.Job
import com.likemagic.masters_beside.utils.getRandomString

class JobsViewModel: ViewModel() {
    private val liveData: MutableLiveData<JobState> by lazy { MutableLiveData<JobState>() }
    private val dataBase = JobsDB()
    private val accountBase = Firebase.auth

    fun getLiveData(): LiveData<JobState> {
        return liveData
    }

    fun addJob(job: Job){
        job.uid = accountBase.currentUser?.uid
        job.key = dataBase.jobsBranch.push().key!!
        job.id = getRandomString(20)
        dataBase.addJob(job){
            liveData.postValue(JobState.NewJob)
        }
    }

    fun getAllJobs(){
        liveData.postValue(JobState.Loading)
        dataBase.getAllJobs(){list ->
            list.sortWith(Comparator{j1:Job, j2:Job -> (j2.date - j1.date).toInt()})
            liveData.postValue(JobState.ListOfJobs(list))
        }
    }

    fun getJob(job: Job){
        liveData.postValue(JobState.Loading)
        getJobByID(job.uid!!, job.id!!) { result ->
            if (result.uid == accountBase.uid) {
                liveData.postValue(JobState.JobPage(result, true))
            }else{
                liveData.postValue(JobState.JobPage(result, false))
            }
        }
    }

    private fun getJobByID(uid:String, id:String, jobCallback: JobCallback){
        dataBase.getJobByUID(uid , id){list ->
            if(list.isNotEmpty()){
                jobCallback.returnJob(list[0])
            }else{
                liveData.postValue(JobState.EmptyList)
            }

        }
    }

    fun deleteJob(job: Job){
        dataBase.deleteJob(job) {
            liveData.postValue(JobState.DeleteJob)
        }
    }

    fun updateJob(job: Job){
        dataBase.updateJob(job){
            liveData.postValue(JobState.UpdateJob(it))
        }
    }

    fun interface JobCallback{
        fun returnJob(job: Job)
    }
}