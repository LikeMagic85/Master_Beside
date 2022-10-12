package com.likemagic.masters_beside.view.signIn

import android.animation.Animator
import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels
import androidx.transition.TransitionInflater
import com.google.android.material.snackbar.Snackbar
import com.likemagic.masters_beside.R
import com.likemagic.masters_beside.databinding.FragmentResetPasswordBinding
import com.likemagic.masters_beside.databinding.FragmentSignUpUserBinding
import com.likemagic.masters_beside.databinding.ResetDialogBinding
import com.likemagic.masters_beside.databinding.SignDialogBinding
import com.likemagic.masters_beside.utils.*
import com.likemagic.masters_beside.viewModel.AppState
import com.likemagic.masters_beside.viewModel.MainViewModel

class ResetPasswordFragment : Fragment() {

    private var _binding:FragmentResetPasswordBinding? = null
    private val binding: FragmentResetPasswordBinding
    get() = _binding!!

    private val viewModel: MainViewModel by activityViewModels()

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
        _binding = FragmentResetPasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getLiveData().observe(viewLifecycleOwner){
            renderResult(it)
        }
        resetPassword()
    }

    private fun renderResult(appState: AppState?) {
        if(appState is AppState.SuccessReset){
            createSignDialog()
        }
        if(appState is AppState.ErrorSignIn){
            if(appState.result == USER_NOT_FOUND){
                Snackbar.make(binding.root, "Пользователь с таким E-mail не зарегистрирован", Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    private fun resetPassword(){
        binding.signInBtn.setOnClickListener {
            val email = binding.loginEmailInput.text.toString()
            viewModel.resetPassword(email)
            it.hideKeyboard()
        }
    }

    private fun createSignDialog(){
        val builder = AlertDialog.Builder(requireContext())
        val alertBinding = ResetDialogBinding.inflate(requireActivity().layoutInflater)
        builder.setView(alertBinding.root)
        alertBinding.mail.animate().x(1000f).setDuration(1500).setListener(object : Animator.AnimatorListener{
            override fun onAnimationStart(animation: Animator) {}
            override fun onAnimationEnd(animation: Animator) {
                alertBinding.mail.animate().x(50f).duration = 1500
            }
            override fun onAnimationCancel(animation: Animator) {}
            override fun onAnimationRepeat(animation: Animator) {}
        })
        val dialog = builder.show()
        alertBinding.alertBtn.setOnClickListener {
            removeFragment(SIGN_FRAGMENT)
            dialog.dismiss()
        }

    }

    private fun removeFragment(name:String){
        val fm = requireActivity().supportFragmentManager
        fm.popBackStack(name, FragmentManager.POP_BACK_STACK_INCLUSIVE)
    }

    companion object {
        @JvmStatic
        fun newInstance() = ResetPasswordFragment()
    }
}