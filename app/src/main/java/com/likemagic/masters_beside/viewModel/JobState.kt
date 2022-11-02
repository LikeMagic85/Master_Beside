package com.likemagic.masters_beside.viewModel

import com.likemagic.masters_beside.repository.Job


sealed class JobState{
   data class ListOfJobs(val list:List<Job>):JobState()
   data class JobPage(val job: Job, var isMy: Boolean):JobState()
   data class UpdateJob(val job: Job):JobState()
   object NewJob:JobState()
   object Loading:JobState()
   object EmptyList:JobState()
   object DeleteJob:JobState()
}
