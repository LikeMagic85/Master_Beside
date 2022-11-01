package com.likemagic.masters_beside.view.navigation

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.transition.TransitionInflater
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.snackbar.Snackbar
import com.likemagic.masters_beside.R
import com.likemagic.masters_beside.databinding.FragmentFavoriteListBinding
import com.likemagic.masters_beside.repository.Master
import com.likemagic.masters_beside.utils.PROFILE_FRAGMENT
import com.likemagic.masters_beside.utils.setToolbarVisibility
import com.likemagic.masters_beside.view.masters.ListOfMastersAdapter
import com.likemagic.masters_beside.viewModel.AppState
import com.likemagic.masters_beside.viewModel.MastersViewModel

class FavoriteListFragment : Fragment() {

    private var _binding: FragmentFavoriteListBinding? = null
    private val binding: FragmentFavoriteListBinding
        get() = _binding!!
    private val mastersViewModel: MastersViewModel by viewModels()
    private lateinit var masterAdapter: ListOfMastersAdapter


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
        _binding = FragmentFavoriteListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolbarVisibility(requireActivity(), true)
        requireActivity().findViewById<BottomNavigationView>(R.id.bottomNav).menu.findItem(R.id.actionFavorite).isChecked =
            true
        mastersViewModel.getMyData()
        mastersViewModel.getLiveData().observe(viewLifecycleOwner){
            renderData(it)
        }
        masterAdapter = ListOfMastersAdapter(myData = Master(), context = requireContext())
        initRecycler(masterAdapter)
    }

    private fun initRecycler(masterAdapter: ListOfMastersAdapter){
        binding.masterList.adapter = masterAdapter
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            binding.masterList.setOnScrollChangeListener { _, _, _, _, _ ->
                binding.titleOfListContainer.isSelected = binding.masterList.canScrollVertically(-1)
            }
        }
        masterAdapter.onItemClick ={
            navigateToMasterPage(it)
        }
    }

    private fun renderData(appState: AppState) {
        when (appState) {
            is AppState.Loading -> {
                binding.loadingLayout.visibility = View.VISIBLE
            }
            is AppState.MyData -> {
                masterAdapter.myData = appState.master
                mastersViewModel.getFavoriteMasters(appState.master)
            }
            is AppState.ListOfFavoriteMasters -> {
                binding.loadingLayout.visibility = View.GONE
                masterAdapter.setList(appState.list)
            }
            is AppState.EmptyList -> {
                binding.loadingLayout.visibility = View.GONE
                Snackbar.make(binding.root, "У вас пока нет избранных мастеров", Snackbar.LENGTH_SHORT).show()
            }
            else -> {}
        }
    }

    private fun navigateToMasterPage(master: Master){
        requireActivity().supportFragmentManager
            .beginTransaction()
            .addToBackStack(PROFILE_FRAGMENT)
            .add(R.id.mainContainer, ProfileMasterFragment.newInstance(master), PROFILE_FRAGMENT)
            .commit()
    }

    companion object {
        @JvmStatic
        fun newInstance() = FavoriteListFragment()
    }
}