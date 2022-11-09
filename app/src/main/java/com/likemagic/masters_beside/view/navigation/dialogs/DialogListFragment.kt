package com.likemagic.masters_beside.view.navigation.dialogs

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.transition.TransitionInflater
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.likemagic.masters_beside.R
import com.likemagic.masters_beside.databinding.FragmentListOfDialogsBinding
import com.likemagic.masters_beside.repository.Dialog
import com.likemagic.masters_beside.repository.Master
import com.likemagic.masters_beside.utils.*
import com.likemagic.masters_beside.view.navigation.ProfileMasterFragment
import com.likemagic.masters_beside.viewModel.AppState
import com.likemagic.masters_beside.viewModel.DialogState
import com.likemagic.masters_beside.viewModel.DialogsViewModel
import com.likemagic.masters_beside.viewModel.MastersViewModel


class DialogListFragment : Fragment() {

    private var _binding: FragmentListOfDialogsBinding? = null
    private val binding: FragmentListOfDialogsBinding
        get() = _binding!!
    private val dialogsViewModel: DialogsViewModel by viewModels()
    private val mastersViewModel: MastersViewModel by viewModels()
    private lateinit var dialogAdapter: ListOfDialogsAdapter


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
        _binding = FragmentListOfDialogsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolbarVisibility(requireActivity(), true)
        requireActivity().findViewById<BottomNavigationView>(R.id.bottomNav).menu.findItem(R.id.actionMessage).isChecked =
            true
        dialogAdapter = ListOfDialogsAdapter(myData = Master(), context = requireContext())
        initRecycler(dialogAdapter)
        mastersViewModel.getLiveData().observe(viewLifecycleOwner){
            dialogAdapter.myData = (it as AppState.MyData).master
            dialogsViewModel.getMyDialogs((it).master.uid!!)
        }
        mastersViewModel.getMyData()
        dialogsViewModel.getLiveData().observe(viewLifecycleOwner){
            renderData(it)
        }
    }

    private fun initRecycler(dialogsAdapter: ListOfDialogsAdapter){
        binding.dialogList.adapter = dialogAdapter
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            binding.dialogList.setOnScrollChangeListener { _, _, _, _, _ ->
                binding.titleOfListContainer.isSelected = binding.dialogList.canScrollVertically(-1)
            }
        }
        dialogsAdapter.onItemClick ={
            navigateToDialog(it)
        }
        dialogsAdapter.onBtnClick = {dialog, v ->
            showPopUpMenu(dialog, v)
        }
    }

    private fun renderData(dialogState: DialogState) {
        when(dialogState){
            is DialogState.Loading->{
                binding.loadingLayout.visibility = View.VISIBLE
            }
            is DialogState.ListOfDialogs -> {
                binding.loadingLayout.visibility = View.GONE
                dialogAdapter.setList(dialogState.list)
            }
            else -> {}
        }
    }

    private fun showPopUpMenu(dialog:Dialog, v:View){
        val popupMenu = PopupMenu(requireContext(), v)
        popupMenu.inflate(R.menu.popup_menu)
        popupMenu.setOnMenuItemClickListener {
            when(it.itemId){
                R.id.deleteDialog -> {
                    dialogsViewModel.deleteDialog(dialog)
                    true
                }
                else -> {true}
            }
        }
        popupMenu.show()
    }

    private fun navigateToDialog(dialog:Dialog){
        requireActivity().supportFragmentManager
            .beginTransaction()
            .addToBackStack(DIALOG_FRAGMENT)
            .add(R.id.mainContainer, DialogFragment.newInstance(dialog))
            .commit()
    }
    companion object {
        @JvmStatic
        fun newInstance() = DialogListFragment()
    }
}