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
import com.likemagic.masters_beside.repository.Master
import com.likemagic.masters_beside.utils.*
import com.likemagic.masters_beside.view.navigation.ProfileFragment
import com.likemagic.masters_beside.viewModel.AppState
import com.likemagic.masters_beside.viewModel.MastersViewModel
import com.likemagic.masters_beside.viewModel.SignViewModel
import kotlin.concurrent.thread

class LinkPhoneFragment : Fragment() {

    private var _binding: FragmentSignUpWithPhoneBinding? = null
    private val binding: FragmentSignUpWithPhoneBinding
        get() = _binding!!

    private val signViewModel: SignViewModel by activityViewModels()
    private val mastersViewModel:MastersViewModel by activityViewModels()

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
        binding.title.text = "Подтвердить телефон"
        signViewModel.getLiveData().observe(viewLifecycleOwner){
            renderSignResult(it)
        }
        mastersViewModel.getLiveData().observe(viewLifecycleOwner){
            if(it is AppState.ConfirmPhone){
                linkPhone(it.master)
            }
        }
        binding.text.visibility = VISIBLE
    }

    private fun linkPhone(master: Master) {
        val phone = arguments?.getString(PHONE)
        binding.apply {
            TransitionManager.beginDelayedTransition(binding.root)
            signInBtn.visibility = GONE
            loginPhone.visibility = GONE
            confirmCode.visibility = VISIBLE
            title.apply {
                text = "Введите код подтверждения"
                textSize = 22f
            }
        }
        signViewModel.signInWithPhone(phone!!, requireActivity())
        startTimer(){
            try {
                requireActivity().runOnUiThread{
                    try{
                        binding.timer.text = it.toString()
                    }catch (e:Throwable){return@runOnUiThread}
                }
            }catch (e:Throwable){return@startTimer}
        }
        binding.codeInput.addTextChangedListener {
            val code = binding.codeInput.text.toString()
            if(code.length == 6 ){
                try{
                    signViewModel.linkPhoneToEmail(code, master)
                }catch (e: Throwable){
                    Snackbar.make(binding.root, "Дождитесь проверочного кода", Snackbar.LENGTH_SHORT).show()
                    binding.loadingLayout.visibility = GONE
                }
                binding.root.hideKeyboard()
            }
        }
        binding.signInBtn.setOnClickListener {
            binding.signInBtn.visibility = GONE
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

    }

    private fun renderSignResult(appState: AppState) {
        when (appState) {
            is AppState.Loading -> {
                binding.loadingLayout.visibility = VISIBLE
            }
            is AppState.ConfirmDone -> {
                binding.loadingLayout.visibility = GONE
                appState.master.isPhoneChecked = true
                mastersViewModel.updateMaster(appState.master, true)
                navigateTo(ProfileFragment.newInstance(appState.master), PROFILE_FRAGMENT, requireActivity())
            }
            is AppState.PhoneInUse -> {
                binding.loadingLayout.visibility = GONE
                Snackbar.make(binding.root, "Номер уже привязан к другому аккаунту", Snackbar.LENGTH_SHORT).show()
            }
            else -> {
                binding.loadingLayout.visibility = GONE
            }
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
        fun newInstance(phone:String):LinkPhoneFragment{
            val args = Bundle()
            args.putString(PHONE, phone)
            val fragment = LinkPhoneFragment()
            fragment.arguments = args
            return fragment
        }
    }
}