package com.likemagic.masters_beside.view.signIn

import android.animation.Animator
import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.transition.TransitionInflater
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.likemagic.masters_beside.R
import com.likemagic.masters_beside.databinding.FragmentLinkPhoneToEmailBinding
import com.likemagic.masters_beside.databinding.SignDialogBinding
import com.likemagic.masters_beside.utils.*
import com.likemagic.masters_beside.view.masters.CreateNewMasterFragment
import com.likemagic.masters_beside.viewModel.AppState
import com.likemagic.masters_beside.viewModel.SignViewModel

class LinkWithEmailFragment : Fragment() {

    private var _binding: FragmentLinkPhoneToEmailBinding? = null
    private val binding: FragmentLinkPhoneToEmailBinding
        get() = _binding!!

    private val viewModel: SignViewModel by activityViewModels()

    lateinit var sp: SharedPreferences
    lateinit var editor: SharedPreferences.Editor

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
        _binding = FragmentLinkPhoneToEmailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolbarVisibility(requireActivity(), false)
        viewModel.getLiveData().observe(viewLifecycleOwner) {
            renderSignResult(it)
        }
        linkWithEmail()
    }

    private fun linkWithEmail() {
        binding.signInBtn.setOnClickListener {
            val email = binding.loginEmailInput.text.toString()
            val password = binding.loginPasswordInput.text.toString()
            sp = requireActivity().getSharedPreferences(PASSWORD, Context.MODE_PRIVATE)
            editor = sp.edit()
            editor.putString(PASSWORD, password).apply()
            if (isValidEmail(email)) {
                if (isValidPassword(password)) {
                    viewModel.linkEmailToPhone(email, password)
                } else {
                    binding.loginPassword.error = resources.getString(R.string.helper_password_text)
                }
            } else {
                Snackbar.make(
                    binding.root,
                    "Пожалуйста, введите правильный E-mail",
                    Snackbar.LENGTH_SHORT
                ).show()
            }
            it.hideKeyboard()
        }
    }

    private fun renderSignResult(appState: AppState) {
        binding.loadingLayout.visibility = GONE
        if (appState is AppState.ErrorSignIn) {
            when (appState.result) {
                ALREADY_REGISTER -> {
                    Snackbar.make(
                        binding.root,
                        "Пользователь с данным E-mail уже зарегистрирован",
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
                SIGN_ERROR -> {
                    Snackbar.make(binding.root, "Ошибка при регистрации", Snackbar.LENGTH_SHORT)
                        .show()
                }
            }
        }
        if (appState is AppState.SuccessPostEmail) {
            binding.loadingLayout.visibility = GONE
            if (appState.result) {
                createSignDialog()
            } else {
                Snackbar.make(
                    binding.root,
                    "Ошибка отправки письма на вашу почту",
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        }
        if(appState is AppState.SuccessLink){
            binding.loadingLayout.visibility = GONE
            if (appState.result == SUCCESS_LINK){
                val user = FirebaseAuth.getInstance().currentUser
                viewModel.sendVerificationEmail(user!!)
            }
        }
        if(appState is AppState.Loading){
            binding.loadingLayout.visibility = VISIBLE
        }
    }

    private fun createSignDialog() {
        val builder = AlertDialog.Builder(requireContext())
        val alertBinding = SignDialogBinding.inflate(requireActivity().layoutInflater)
        builder.setView(alertBinding.root)
        alertBinding.mail.animate().x(1000f).setDuration(1500)
            .setListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {}
                override fun onAnimationEnd(animation: Animator) {
                    alertBinding.mail.animate().x(50f).duration = 1500
                }

                override fun onAnimationCancel(animation: Animator) {}
                override fun onAnimationRepeat(animation: Animator) {}
            })
        val dialog = builder.show()
        alertBinding.alertBtn.setOnClickListener {
            removeFragment(LINK_WITH_EMAIL_FRAGMENT, requireActivity())
            navigateTo(CreateNewMasterFragment.newInstance(binding.loginEmailInput.text.toString()), CREATE_NEW_MASTER_FRAGMENT, requireActivity())
            dialog.dismiss()
        }

    }


    companion object {
        @JvmStatic
        fun newInstance() = LinkWithEmailFragment()
    }
}