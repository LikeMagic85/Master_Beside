package com.likemagic.masters_beside.view.masters

import android.app.AlertDialog
import android.os.Build
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.transition.TransitionInflater
import com.likemagic.masters_beside.R
import com.likemagic.masters_beside.databinding.ExitDialogBinding
import com.likemagic.masters_beside.databinding.FragmentListOfMastersBinding
import com.likemagic.masters_beside.repository.Master
import com.likemagic.masters_beside.repository.ProfessionGetter
import com.likemagic.masters_beside.utils.PROFILE_FRAGMENT
import com.likemagic.masters_beside.utils.hideKeyboard
import com.likemagic.masters_beside.utils.setToolbarVisibility
import com.likemagic.masters_beside.view.navigation.ProfileFragment
import com.likemagic.masters_beside.viewModel.AppState
import com.likemagic.masters_beside.viewModel.MastersViewModel
import com.likemagic.masters_beside.viewModel.SignViewModel
import kotlin.system.exitProcess

class ListOfMastersFragment : Fragment() {

    private var _binding: FragmentListOfMastersBinding? = null
    private val binding: FragmentListOfMastersBinding
        get() = _binding!!

    private val signViewModel: SignViewModel by activityViewModels()
    private val mastersViewModel: MastersViewModel by viewModels()
    private val masterAdapter = ListOfMastersAdapter()
    private val searchAdapter = ListOfSearchAdapter()
    private var listOfMasters = listOf<Master>()

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onStart() {
        super.onStart()
        detectBackClick()
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
        _binding = FragmentListOfMastersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolbarVisibility(requireActivity(), true)
        signViewModel.getLiveData().observe(viewLifecycleOwner){
            renderData(it)
        }
        mastersViewModel.getLiveData().observe(viewLifecycleOwner){
            renderData(it)
        }
        mastersViewModel.getAllMasters()
        initRecycler(masterAdapter)
        setUpSearch()
        initSearchRecycler(searchAdapter)
    }

    private fun renderData(appState: AppState) {
        when (appState) {
            is AppState.Loading -> {
                binding.loadingLayout.visibility = VISIBLE
            }
            is AppState.ListOfMasters -> {
                binding.loadingLayout.visibility = GONE
                masterAdapter.setList(appState.list)
                listOfMasters = appState.list
            }
            else -> {}
        }
    }

    private fun initRecycler(masterAdapter: ListOfMastersAdapter){
        binding.masterList.adapter = masterAdapter
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            binding.masterList.setOnScrollChangeListener { _, _, _, _, _ ->
                binding.searchContainer.isSelected = binding.masterList.canScrollVertically(-1)
            }
        }
        masterAdapter.onItemClick ={
            navigateToMasterPage(it.uid!!)
        }
    }


    private fun initSearchRecycler(
        searchAdapter: ListOfSearchAdapter
    ) {
        binding.searchRecycler.adapter = searchAdapter
        binding.masterSearchInput.addTextChangedListener {
            val search = binding.masterSearchInput.text.toString()
            binding.searchRecycler.visibility = VISIBLE
            ProfessionGetter().getProfessionByKeyWords(search){
                requireActivity().runOnUiThread{
                    searchAdapter.setList(it)

                }
            }

        }
        searchAdapter.onItemClick= {profession ->
            mastersViewModel.getMastersByProfession(profession)
            binding.apply {
                masterSearchInput.setText(profession.name)
                searchRecycler.visibility = GONE
                root.hideKeyboard()
                masterSearchInput.setText("")
            }
        }
    }

    private fun setUpSearch(){
        binding.masterSearchInput.setOnKeyListener{v,keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN &&
                keyCode == KeyEvent.KEYCODE_ENTER) {
                createExitDialog()
                true
            } else false
        }
    }

    private fun detectBackClick() {
        requireView().isFocusableInTouchMode = true
        requireView().requestFocus()
        requireView().setOnKeyListener { _, keyCode, event ->
            if (event.action === KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                createExitDialog()
                true
            } else false
        }
    }

    private fun createExitDialog() {
        val builder = AlertDialog.Builder(requireContext())
        val exitBinding = ExitDialogBinding.inflate(requireActivity().layoutInflater)
        builder.setView(exitBinding.root)
        val dialog = builder.show()
        exitBinding.runMan.animate().x(1000f).duration = 1500
        exitBinding.positiveBtn.setOnClickListener {
            exitProcess(0)
        }
        exitBinding.negativeBtn.setOnClickListener {
            dialog.dismiss()
        }
    }

    private fun navigateToMasterPage(uid: String){
        requireActivity().supportFragmentManager
            .beginTransaction()
            .addToBackStack(PROFILE_FRAGMENT)
            .replace(R.id.mainContainer, ProfileFragment.newInstance(uid), PROFILE_FRAGMENT)
            .commit()
    }

    companion object {
        @JvmStatic
        fun newInstance() = ListOfMastersFragment()
    }
}