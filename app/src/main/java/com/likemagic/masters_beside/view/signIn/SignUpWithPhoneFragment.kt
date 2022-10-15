package com.likemagic.masters_beside.view.signIn

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
            if(appState.isNew){
                removeFragment(SIGN_FRAGMENT, requireActivity())
                navigateTo(ChoseCategoryFragment.newInstance(), CHOOSE_FRAGMENT)
            }else{
                removeFragment(SIGN_FRAGMENT, requireActivity())
            }
        } else if (appState is AppState.ErrorVerificationCode){
            Snackbar.make(binding.root, "Неверный код из СМС", Snackbar.LENGTH_SHORT).show()
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