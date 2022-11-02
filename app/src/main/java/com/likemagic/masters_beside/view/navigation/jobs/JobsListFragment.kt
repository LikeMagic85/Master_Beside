package com.likemagic.masters_beside.view.navigation.jobs

import android.app.AlertDialog
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.transition.TransitionInflater
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import com.likemagic.masters_beside.R
import com.likemagic.masters_beside.databinding.FragmentJobsListBinding
import com.likemagic.masters_beside.databinding.NewJobDialogBinding
import com.likemagic.masters_beside.repository.Job
import com.likemagic.masters_beside.repository.Master
import com.likemagic.masters_beside.utils.JOB_FRAGMENT
import com.likemagic.masters_beside.utils.PROFILE_FRAGMENT
import com.likemagic.masters_beside.utils.setToolbarVisibility
import com.likemagic.masters_beside.viewModel.AppState
import com.likemagic.masters_beside.viewModel.JobState
import com.likemagic.masters_beside.viewModel.JobsViewModel
import com.likemagic.masters_beside.viewModel.MastersViewModel

class JobsListFragment : Fragment() {

    private var _binding: FragmentJobsListBinding? = null
    private val binding: FragmentJobsListBinding
        get() = _binding!!
    private val mastersViewModel: MastersViewModel by viewModels()
    private val jobsViewModel: JobsViewModel by viewModels()
    lateinit var jobsAdapter: ListOfJobsAdapter

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
        _binding = FragmentJobsListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolbarVisibility(requireActivity(), true)
        jobsAdapter = ListOfJobsAdapter()
        initRecycler()
        requireActivity().findViewById<BottomNavigationView>(R.id.bottomNav).menu.findItem(R.id.actionOrders).isChecked =
            true
        mastersViewModel.getLiveData().observe(viewLifecycleOwner){
            setUpMaster(it)
        }
        jobsViewModel.getLiveData().observe(viewLifecycleOwner){
            renderData(it)
        }
        mastersViewModel.getMyData()
    }

    private fun initRecycler(){
        binding.jobList.adapter = jobsAdapter
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            binding.jobList.setOnScrollChangeListener { _, _, _, _, _ ->
                binding.titleOfListContainer.isSelected = binding.jobList.canScrollVertically(-1)
            }
        }
        jobsAdapter.onItemClick = {
            navigateToJobPage(it)
        }
    }

    private fun renderData(jobState: JobState) {
        if(jobState is JobState.ListOfJobs){
            jobsAdapter.setList(jobState.list)
        }
    }

    private fun setUpMaster(appState: AppState) {
        if (appState is AppState.MyData){
            setUpFab(appState.master)
            jobsViewModel.getAllJobs()
        }
    }

    private fun setUpFab(master: Master){
        binding.newJobBtn.setOnClickListener{
            if(master.isPhoneChecked){
                createNewJobDialog(master)
            }else{
                createNewJobDialog(master)
                Snackbar.make(binding.root, "Необходимо подтверить номер телефона", Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    private fun createNewJobDialog(master: Master){
        val builder = AlertDialog.Builder(requireContext())
        val view = NewJobDialogBinding.inflate(requireActivity().layoutInflater)
        builder.setView(view.root)
        val dialog = builder.show()
        view.createJobBtn.setOnClickListener {
            val jobName = view.jobNameInput.text.toString()
            val jobMessage = view.jobMessageInput.text.toString()
            val jobCost = view.jobCostInput.text.toString()
            if(jobName.isNotEmpty()){
                if(jobMessage.isNotEmpty()){
                    val job = Job(master.name, jobName,jobMessage, master.city, System.currentTimeMillis(), master.uid!!, uriImage = master.uriImage, cost = jobCost)
                    jobsViewModel.addJob(job)
                    dialog.dismiss()
                    mastersViewModel.getMyData()
                }else{
                    view.jobMessage.error = "Введите описание"
                }
            }else{
                view.jobName.error = "Введите название"
            }
        }
    }

    private fun navigateToJobPage(job: Job){
        requireActivity().supportFragmentManager
            .beginTransaction()
            .addToBackStack(JOB_FRAGMENT)
            .add(R.id.mainContainer, JobFragment.newInstance(job), PROFILE_FRAGMENT)
            .commit()
    }

    companion object {
        @JvmStatic
        fun newInstance() = JobsListFragment()
    }
}