package com.likemagic.masters_beside.view.signIn

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.transition.TransitionInflater
import com.google.android.material.snackbar.Snackbar
import com.likemagic.masters_beside.R
import com.likemagic.masters_beside.databinding.FragmentChoseCategoryBinding
import com.likemagic.masters_beside.utils.*
import com.likemagic.masters_beside.view.masters.CreateNewMasterFragment
import com.likemagic.masters_beside.view.users.CreateNewUserFragment


class ChoseCategoryFragment : Fragment() {

    private var _binding: FragmentChoseCategoryBinding? = null
    private val binding: FragmentChoseCategoryBinding
        get() = _binding!!


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val inflater = TransitionInflater.from(requireContext())
        enterTransition = inflater.inflateTransition(R.transition.slide_transition)
        exitTransition = inflater.inflateTransition(R.transition.fade_transition)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChoseCategoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolbarVisibility(requireActivity(), false)
        setUpUser()
        detectBackClick()
    }

    private fun setUpUser() {
        binding.regBtn.setOnClickListener {
            if (binding.userChip.isChecked) {
                removeFragment(CHOOSE_FRAGMENT, requireActivity())
                navigateToNextStep(
                    CreateNewUserFragment.newInstance(),
                    CREATE_NEW_USER_FRAGMENT
                )
            } else if (binding.masterChip.isChecked) {
                removeFragment(CHOOSE_FRAGMENT, requireActivity())
                navigateToNextStep(
                    CreateNewMasterFragment.newInstance(),
                    CREATE_NEW_MASTER_FRAGMENT
                )
            } else {
                Snackbar.make(binding.root, "Пожалуйста, сделайте выбор", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun navigateToNextStep(fragment: Fragment, name: String) {
        requireActivity().supportFragmentManager
            .beginTransaction()
            .addToBackStack(name)
            .replace(R.id.mainContainer, fragment, name)
            .commit()
    }

    private fun detectBackClick() {
        requireView().isFocusableInTouchMode = true
        requireView().requestFocus()
        requireView().setOnKeyListener { _, keyCode, event ->
            if (event.action === KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                Snackbar.make(
                    binding.root,
                    "Пожалуйста, сделайте выбор, прежде чем покинуть этот экран",
                    Snackbar.LENGTH_LONG
                ).show()
                true
            } else false
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = ChoseCategoryFragment()
    }
}