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
import kotlin.concurrent.thread

class SignUpWithPhoneFragment : Fragment() {

    private var _binding: FragmentSignUpWithPhoneBinding? = null
    private val binding: FragmentSignUpWithPhoneBinding
        get() = _binding!!

    private val signViewModel: SignViewModel by activityViewModels()

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
        signViewModel.getLiveData().observe(viewLifecycleOwner) {
            renderSignResult(it)
        }
        signUpWithPhone()
        binding.title.text = "Вход по номеру телефона"
        binding.timer.visibility = GONE
        binding.nextTimerText.visibility = GONE
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
                title.apply {
                    text = "Введите код подтверждения"
                    textSize = 22f
                }
                binding.timer.visibility = VISIBLE
                binding.nextTimerText.visibility = VISIBLE
            }
            signViewModel.signInWithPhone(phone, requireActivity())
            startTimer(){
                try {
                    requireActivity().runOnUiThread{
                        try{
                            binding.timer.text = it.toString()
                        }catch (e:Throwable){return@runOnUiThread}
                    }
                }catch (e:Throwable){return@startTimer}
            }
        }
        binding.codeInput.addTextChangedListener {
            val code = binding.codeInput.text.toString()
            if(code.length == 6 ){
                signViewModel.signInWithPhoneAuthCredential(code)
                binding.root.hideKeyboard()
            }
        }

    }

    private fun renderSignResult(appState: AppState) {
        if(appState is AppState.SuccessSignInWithPhone){
            binding.loadingLayout.visibility = GONE
            if(appState.isNew){
                Snackbar.make(binding.root, "Пользователь не зарегистрирован", Snackbar.LENGTH_SHORT).show()
                signViewModel.deleteAccount()
                removeFragment(SIGN_FRAGMENT, requireActivity())
            }else{
                removeFragment(SIGN_FRAGMENT, requireActivity())
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

    private fun startTimer(callback:(Int)->Unit){
        thread {
            var i = 60
            while (i>=0){
                Thread.sleep(1000)
                if(i == 0){
                    try{
                        requireActivity().runOnUiThread {
                            binding.signInBtn.visibility = VISIBLE
                        }
                    }catch (e:Throwable){return@thread}
                    break
                }
                i--
                callback.invoke(i)
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = SignUpWithPhoneFragment()
    }
}