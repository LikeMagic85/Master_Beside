package com.likemagic.masters_beside.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.transition.TransitionInflater
import com.likemagic.masters_beside.R
import com.likemagic.masters_beside.databinding.FragmentAboutBinding
import com.likemagic.masters_beside.repository.City
import com.likemagic.masters_beside.utils.ABOUT_FRAGMENT
import com.likemagic.masters_beside.utils.removeFragment
import com.likemagic.masters_beside.utils.setToolbarVisibility
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.io.InputStream

class AboutFragment : Fragment() {

    private var _binding: FragmentAboutBinding? = null
    private val binding: FragmentAboutBinding
        get() = _binding!!


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
        _binding = FragmentAboutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpButtons()
        setToolbarVisibility(requireActivity(), false)
    }

    private fun setUpButtons(){
        binding.apply {
            btnBack.setOnClickListener {
                removeFragment(ABOUT_FRAGMENT, requireActivity())
            }
            termsOfUse.setOnClickListener {

            }
            privacyPolicy.setOnClickListener {
                TODO()
            }
            license.setOnClickListener {
                TODO()
            }
        }
    }


    companion object {
        @JvmStatic
        fun newInstance() = AboutFragment()
    }
}