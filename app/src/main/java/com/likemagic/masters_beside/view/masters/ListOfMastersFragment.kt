package com.likemagic.masters_beside.view.masters

import android.app.AlertDialog
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.transition.TransitionInflater
import com.likemagic.masters_beside.R
import com.likemagic.masters_beside.databinding.ExitDialogBinding
import com.likemagic.masters_beside.databinding.FragmentListOfMastersBinding
import com.likemagic.masters_beside.repository.City
import com.likemagic.masters_beside.repository.Contact
import com.likemagic.masters_beside.repository.Master
import com.likemagic.masters_beside.repository.Profession
import com.likemagic.masters_beside.utils.setToolbarVisibility
import com.likemagic.masters_beside.viewModel.SignViewModel
import kotlin.system.exitProcess

class ListOfMastersFragment : Fragment() {

    private var _binding: FragmentListOfMastersBinding? = null
    private val binding: FragmentListOfMastersBinding
        get() = _binding!!

    private val viewModel: SignViewModel by activityViewModels()

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
        initRecycler()
        setUpSearch()
    }

    private fun initRecycler(){
        val mastersList = listOf(
            Master(Profession("", listOf("","")), City("",0.0,0.0),"Дмитрий","","", Contact("","","",""),0f,0),
            Master(Profession("", listOf("","")), City("",0.0,0.0),"Дмитрий","","", Contact("","","",""),0f,0),
            Master(Profession("", listOf("","")), City("",0.0,0.0),"Дмитрий","","", Contact("","","",""),0f,0),
            Master(Profession("", listOf("","")), City("",0.0,0.0),"Дмитрий","","", Contact("","","",""),0f,0),
            Master(Profession("", listOf("","")), City("",0.0,0.0),"Дмитрий","","", Contact("","","",""),0f,0),
            Master(Profession("", listOf("","")), City("",0.0,0.0),"Дмитрий","","", Contact("","","",""),0f,0),
            Master(Profession("", listOf("","")), City("",0.0,0.0),"Дмитрий","","", Contact("","","",""),0f,0),
            Master(Profession("", listOf("","")), City("",0.0,0.0),"Дмитрий","","", Contact("","","",""),0f,0),
            Master(Profession("", listOf("","")), City("",0.0,0.0),"Дмитрий","","", Contact("","","",""),0f,0),
            Master(Profession("", listOf("","")), City("",0.0,0.0),"Дмитрий","","", Contact("","","",""),0f,0),
            Master(Profession("", listOf("","")), City("",0.0,0.0),"Дмитрий","","", Contact("","","",""),0f,0),
            Master(Profession("", listOf("","")), City("",0.0,0.0),"Дмитрий","","", Contact("","","",""),0f,0),
            Master(Profession("", listOf("","")), City("",0.0,0.0),"Дмитрий","","", Contact("","","",""),0f,0),

        )
        val masterAdapter = ListOfMastersAdapter()
        masterAdapter.setList(mastersList)
        binding.masterList.adapter = masterAdapter
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            binding.masterList.setOnScrollChangeListener { _, _, _, _, _ ->
                binding.searchContainer.isSelected = binding.masterList.canScrollVertically(-1)
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

    companion object {
        @JvmStatic
        fun newInstance() = ListOfMastersFragment()
    }
}