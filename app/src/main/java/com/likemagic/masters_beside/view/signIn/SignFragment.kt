package com.likemagic.masters_beside.view.signIn

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.transition.TransitionInflater
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.likemagic.masters_beside.R
import com.likemagic.masters_beside.databinding.FragmentSignBinding
import com.likemagic.masters_beside.utils.*
import com.likemagic.masters_beside.viewModel.AppState
import com.likemagic.masters_beside.viewModel.SignViewModel

class SignFragment : Fragment() {
    private var _binding: FragmentSignBinding? = null
    private val binding: FragmentSignBinding
        get() = _binding!!
    private val viewModel: SignViewModel by activityViewModels()

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
        _binding = FragmentSignBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.loadingLayout.visibility = GONE
        setToolbarVisibility(requireActivity(), false)
        viewModel.getLiveData().observe(viewLifecycleOwner){
            renderResult(it)
        }
        setUpBtn()
    }



    private fun setUpBtn() {
        binding.regBtn.setOnClickListener {
            removeFragment(SIGN_FRAGMENT, requireActivity())
            navigateTo(SignUpWithEmailFragment.newInstance(), SIGN_UP_WITH_EMAIL_FRAGMENT)
        }
        binding.emailBtn.setOnClickListener {
            navigateTo(SignInWithEmailFragment.newInstance(), SIGN_IN_WITH_EMAIL_FRAGMENT)
        }
        binding.phoneBtn.setOnClickListener {
            navigateTo(SignUpWithPhoneFragment.newInstance(), SIGN_UP_WITH_PHONE_FRAGMENT)
        }
        binding.signWithGoogleBtn.setOnClickListener {
            signInWithGoogle()
        }
    }

    private fun renderResult(appState: AppState) {
        if(appState is AppState.SuccessSignInWithGoogle){
            binding.loadingLayout.visibility = GONE
            if(appState.isNew){
                removeFragment(SIGN_FRAGMENT, requireActivity())
                navigateTo(ChoseCategoryFragment.newInstance(), CHOOSE_FRAGMENT)
            }else{
                removeFragment(SIGN_FRAGMENT, requireActivity())
            }
        }else if(appState is AppState.Loading){
            binding.loadingLayout.visibility = VISIBLE
        }else if (appState is AppState.ErrorSignIn){
            binding.loadingLayout.visibility = GONE
        }
    }

    private fun navigateTo(fragment: Fragment, name: String) {
        requireActivity().supportFragmentManager.beginTransaction()
            .addToBackStack(name)
            .replace(R.id.mainContainer, fragment, name)
            .commit()
    }

    private fun getSignInClient(): GoogleSignInClient {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(requireActivity().resources.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        return GoogleSignIn.getClient(requireActivity(), gso)
    }

    private fun signInWithGoogle() {
        requireActivity().startActivityForResult(
            getSignInClient().signInIntent,
            SIGN_IN_WITH_GOOGLE_REQUEST_CODE
        )
    }

    companion object {
        @JvmStatic
        fun newInstance() = SignFragment()
    }
}