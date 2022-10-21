package com.likemagic.masters_beside.view.signIn

import android.animation.Animator
import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.transition.TransitionInflater
import androidx.transition.TransitionManager
import com.google.android.material.snackbar.Snackbar
import com.likemagic.masters_beside.R
import com.likemagic.masters_beside.databinding.AlertDialogBinding
import com.likemagic.masters_beside.databinding.FragmentSignUpWithPhoneBinding
import com.likemagic.masters_beside.utils.*
import com.likemagic.masters_beside.viewModel.AppState
import com.likemagic.masters_beside.viewModel.SignViewModel

class SignUpWithPhoneFragment : Fragment() {

    private var _binding: FragmentSignUpWithPhoneBinding? = null
    private val binding: FragmentSignUpWithPhoneBinding
        get() = _binding!!

    private val viewModel: SignViewModel by activityViewModels()

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setToolbarVisibility(requireActivity(), false)
        val inflater = TransitionInflater.from(requireContext())
        enterTransition = inflater.inflateTransition(R.transition.fade_transition)
        exitTransition = inflater.inflateTransition(R.transition.fade_transition)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignUpWithPhoneBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolbarVisibility(requireActivity(), false)
        viewModel.getLiveData().observe(viewLifecycleOwner) {
            renderSignResult(it)
        }
        signUpWithPhone()
        binding.title.text = "Вход по номеру телефона"
    }

    private fun signUpWithPhone() {
        var phone = ""
        binding.signInBtn.setOnClickListener {
            phone = binding.loginPhoneInput.text.toString()
            binding.apply {
                TransitionManager.beginDelayedTransition(binding.root)
                signInBtn.visibility = GONE
                loginPhone.visibility = GONE
                confirmCode.visibility = VISIBLE
                text.visibility = GONE
                title.apply {
                    text = "Введите код подтверждения"
                    textSize = 22f
                }
            }
            viewModel.signInWithPhone(phone, requireActivity())
        }
        binding.codeInput.addTextChangedListener {
            val code = binding.codeInput.text.toString()
            if(code.length == 6 ){
                viewModel.signInWithPhoneAuthCredential(code)
                binding.root.hideKeyboard()
            }
        }

    }

    private fun renderSignResult(appState: AppState) {
        if(appState is AppState.SuccessSignInWithPhone){
            binding.loadingLayout.visibility = GONE
            if(appState.isNew){
                createAlertDialog()
            }else{
                if(appState.hasEmail){
                    removeFragment(SIGN_FRAGMENT, requireActivity())
                }else{
                    createAlertDialog()
                }
            }
        } else if (appState is AppState.ErrorVerificationCode){
            binding.loadingLayout.visibility = GONE
            Snackbar.make(binding.root, "Неверный код из СМС", Snackbar.LENGTH_SHORT).show()
        } else if(appState is AppState.Loading){
            binding.loadingLayout.visibility = VISIBLE
        } else if (appState is AppState.ErrorSignIn){
            binding.loadingLayout.visibility = GONE
        }
    }

    private fun createAlertDialog() {
        val builder = AlertDialog.Builder(requireContext())
        val alertBinding = AlertDialogBinding.inflate(requireActivity().layoutInflater)
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
            removeFragment(SIGN_UP_WITH_PHONE_FRAGMENT, requireActivity())
            navigateTo(LinkWithEmailFragment.newInstance(), LINK_WITH_EMAIL_FRAGMENT)
            dialog.dismiss()
        }

    }

    private fun navigateTo(fragment: Fragment, name: String) {
        requireActivity().supportFragmentManager
            .beginTransaction()
            .replace(R.id.mainContainer, fragment, name)
            .addToBackStack(name)
            .commit()
    }

    companion object {
        @JvmStatic
        fun newInstance() = SignUpWithPhoneFragment()
    }
}