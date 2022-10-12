package com.likemagic.masters_beside.view.signIn

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import androidx.transition.TransitionInflater
import com.likemagic.masters_beside.R
import com.likemagic.masters_beside.databinding.FragmentCreateNewMasterBinding
import com.likemagic.masters_beside.utils.*
import com.likemagic.masters_beside.viewModel.MainViewModel

class CreateNewMasterFragment : Fragment() {

    private var _binding: FragmentCreateNewMasterBinding? = null
    private val binding: FragmentCreateNewMasterBinding
        get() = _binding!!

    private val viewModel: MainViewModel by lazy {
        ViewModelProvider(this)[MainViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val inflater = TransitionInflater.from(requireContext())
        enterTransition = inflater.inflateTransition(R.transition.fade_transition)
        exitTransition = inflater.inflateTransition(R.transition.fade_transition)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        removeFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateNewMasterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    private fun removeFragment() {
        val fm = requireActivity().supportFragmentManager
        fm.popBackStack(SIGN_FRAGMENT, FragmentManager.POP_BACK_STACK_INCLUSIVE)
    }

    companion object {
        fun newInstance() = CreateNewMasterFragment()
    }
}