package com.likemagic.masters_beside.view.signIn

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ImageView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.transition.TransitionInflater
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.snackbar.Snackbar
import com.likemagic.masters_beside.R
import com.likemagic.masters_beside.databinding.FragmentSignBinding
import com.likemagic.masters_beside.utils.*
import com.likemagic.masters_beside.viewModel.AppState
import com.likemagic.masters_beside.viewModel.SignViewModel

class SignFragment : Fragment() {
    private var _binding: FragmentSignBinding? = null
    private val binding: FragmentSignBinding
        get() = _binding!!
    private val signViewModel: SignViewModel by activityViewModels()
    private lateinit var resultLauncher: ActivityResultLauncher<Intent>

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val inflater = TransitionInflater.from(requireContext())
        enterTransition = inflater.inflateTransition(R.transition.fade_transition)
        exitTransition = inflater.inflateTransition(R.transition.fade_transition)
        resultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val data: Intent? = result.data
                    val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                    try {
                        val user = task.getResult(ApiException::class.java)
                        if (user != null) {
                            signViewModel.signInWithGoogle(user.idToken!!, user)
                        }
                    } catch (e: ApiException) {return@registerForActivityResult
                    }
                }
            }
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
        signViewModel.getLiveData().observe(viewLifecycleOwner){
            renderResult(it)
        }
        setUpBtn()
        bottomSheetBehaviorSetup()
    }



    private fun setUpBtn() {
        binding.regBtn.setOnClickListener {
            removeFragment(SIGN_FRAGMENT, requireActivity())
            navigateTo(SignUpWithEmailFragment.newInstance(), SIGN_UP_WITH_EMAIL_FRAGMENT, requireActivity())
        }
        binding.bottomSheet.emailBtn.setOnClickListener {
            navigateTo(SignInWithEmailFragment.newInstance(), SIGN_IN_WITH_EMAIL_FRAGMENT, requireActivity())
        }
        binding.bottomSheet.phoneBtn.setOnClickListener {
            navigateTo(SignUpWithPhoneFragment.newInstance(), SIGN_UP_WITH_PHONE_FRAGMENT,requireActivity())
        }
        binding.bottomSheet.signWithGoogleBtn.setOnClickListener {
            signInWithGoogle()
        }
        binding.bottomSheet.signWithVkBtn.setOnClickListener {
            Snackbar.make(binding.root, "Скоро будет доступно", Snackbar.LENGTH_SHORT).show()
        }
        binding.bottomSheet.signWithFbBtn.setOnClickListener {
            Snackbar.make(binding.root, "Скоро будет доступно", Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun bottomSheetBehaviorSetup(){
        val bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheet.bottomSheetContainer)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        binding.signInBtn.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }
    }

    private fun renderResult(appState: AppState) {
        if(appState is AppState.SuccessSignInWithGoogle){
            binding.loadingLayout.visibility = GONE
            if(appState.isNew){
                Snackbar.make(binding.root, "Пользователь не зарегистрирован", Snackbar.LENGTH_SHORT).show()
                signViewModel.deleteAccount()
                removeFragment(SIGN_FRAGMENT, requireActivity())
            }else{
                removeFragment(SIGN_FRAGMENT, requireActivity())
            }
        }else if(appState is AppState.Loading){
            binding.loadingLayout.visibility = VISIBLE
        }else if (appState is AppState.ErrorSignIn){
            binding.loadingLayout.visibility = GONE
        }
    }

    private fun getSignInClient(): GoogleSignInClient {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(requireActivity().resources.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        return GoogleSignIn.getClient(requireActivity(), gso)
    }

    private fun signInWithGoogle() {
        val intent = getSignInClient().signInIntent
        resultLauncher.launch(intent)
    }

    companion object {
        @JvmStatic
        fun newInstance() = SignFragment()
    }
}