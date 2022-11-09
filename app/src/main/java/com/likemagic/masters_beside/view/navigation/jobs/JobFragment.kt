package com.likemagic.masters_beside.view.navigation.jobs

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.transition.TransitionInflater
import coil.dispose
import coil.load
import com.google.android.material.snackbar.Snackbar
import com.likemagic.masters_beside.R
import com.likemagic.masters_beside.databinding.FragmentJobBinding
import com.likemagic.masters_beside.databinding.NewJobDialogBinding
import com.likemagic.masters_beside.repository.Job
import com.likemagic.masters_beside.repository.Master
import com.likemagic.masters_beside.utils.*
import com.likemagic.masters_beside.view.navigation.ProfileMasterFragment
import com.likemagic.masters_beside.viewModel.JobState
import com.likemagic.masters_beside.viewModel.JobsViewModel
import com.likemagic.masters_beside.viewModel.MastersViewModel

class JobFragment : Fragment() {

    private var _binding: FragmentJobBinding? = null
    private val binding: FragmentJobBinding
        get() = _binding!!
    private val jobsViewModel:JobsViewModel by viewModels()
    private val mastersViewModel: MastersViewModel by viewModels()

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val inflater = TransitionInflater.from(requireContext())
        enterTransition = inflater.inflateTransition(R.transition.fade_transition)
        exitTransition = inflater.inflateTransition(R.transition.fade_transition)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentJobBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolbarVisibility(requireActivity(), false)
        val job = initJob()
        jobsViewModel.getLiveData().observe(viewLifecycleOwner){
            renderData(it)
        }
        jobsViewModel.getJob(job!!)
    }

    private fun initJob(): Job? {
        val arguments = arguments
        return arguments?.getParcelable(JOB_ID)
    }

    private fun renderData(jobState: JobState) {
        when(jobState){
            is JobState.JobPage ->{
                setUpFields(jobState.job)
                showBtn(jobState.isMy)
                deleteJob(jobState.job)
                createEditJobDialog(jobState.job)
                setUpBtn(jobState.job)
            }
            is JobState.DeleteJob -> {
                Snackbar.make(binding.root, "Удалено", Snackbar.LENGTH_SHORT).show()
                removeFragment(JOBS_LIST_FRAGMENT,requireActivity())
                navigateTo(JobsListFragment.newInstance(), JOBS_LIST_FRAGMENT,requireActivity())
                delFragment(this, requireActivity())
            }
            is JobState.UpdateJob -> {
                setUpFields(jobState.job)
            }
            else -> {}
        }
    }

    private  fun setUpFields(job: Job){
        binding.apply {
            if(job.uriImage.isNotEmpty()){
                jobOwnerPhoto.load(job.uriImage)
            }else{
                jobOwnerPhoto.dispose()
                jobOwnerPhoto.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_account))
            }
            ownerName.text = job.ownerName
            jobName.text = job.name
            jobMessage.text = job.message
            if(job.cost.isNotEmpty()){
                jobCost.text = job.cost
            }else{
                jobCost.text = "Договорная"
            }
            jobDate.text = JobTextHelper().repairText(((System.currentTimeMillis() - job.date)/60000).toInt())
        }
    }

    private fun showBtn(isMy: Boolean){
        if(isMy){
            binding.apply {
                editJobBtn.visibility = VISIBLE
                delJobBtn.visibility = VISIBLE
                toProfileBtn.visibility = GONE
            }
        }else{
            binding.apply {
                editJobBtn.visibility = GONE
                delJobBtn.visibility = GONE
                toProfileBtn.visibility = VISIBLE
            }
        }
    }

    private fun deleteJob(job: Job){
        binding.delJobBtn.setOnClickListener {
            jobsViewModel.deleteJob(job)
        }
    }

    private fun setUpBtn(job: Job){
        binding.toProfileBtn.setOnClickListener {
            mastersViewModel.getMasterById(job.uid!!){
                gotoMasterPage(it)
            }
        }
    }

    private fun gotoMasterPage(master: Master){
        requireActivity().supportFragmentManager
            .beginTransaction()
            .addToBackStack(PROFILE_FRAGMENT)
            .replace(R.id.mainContainer, ProfileMasterFragment.newInstance(master))
            .commit()
    }

    private fun createEditJobDialog(job: Job){
        binding.editJobBtn.setOnClickListener {
            val newJob = job.copy()
            val builder = AlertDialog.Builder(requireContext())
            val view = NewJobDialogBinding.inflate(requireActivity().layoutInflater)
            view.dialogTitle.text = "Редактирование работы"
            view.jobNameInput.setText(job.name)
            view.jobMessageInput.setText(job.message)
            view.jobCostInput.setText(job.cost)
            view.createJobBtn.text = "Сохранить"
            builder.setView(view.root)
            val dialog = builder.show()
            view.createJobBtn.setOnClickListener {
                val jobName = view.jobNameInput.text.toString()
                val jobMessage = view.jobMessageInput.text.toString()
                val jobCost = view.jobCostInput.text.toString()
                newJob.name = jobName
                newJob.message = jobMessage
                newJob.cost = jobCost
                if(jobName.isNotEmpty()){
                    if(jobMessage.isNotEmpty()){
                        jobsViewModel.updateJob(newJob)
                        dialog.dismiss()
                    }else{
                        view.jobMessage.error = "Введите описание"
                    }
                }else{
                    view.jobName.error = "Введите название"
                }
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(job: Job):JobFragment{
            val args = Bundle()
            args.putParcelable(JOB_ID, job)
            val fragment = JobFragment()
            fragment.arguments = args
            return fragment
        }
    }
}