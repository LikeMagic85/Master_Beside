package com.likemagic.masters_beside.view.masters

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.transition.TransitionInflater
import com.google.android.material.snackbar.Snackbar
import com.likemagic.masters_beside.R
import com.likemagic.masters_beside.databinding.FragmentCreateNewMasterBinding
import com.likemagic.masters_beside.repository.City
import com.likemagic.masters_beside.repository.Contact
import com.likemagic.masters_beside.repository.Master
import com.likemagic.masters_beside.repository.Profession
import com.likemagic.masters_beside.utils.hideKeyboard
import com.likemagic.masters_beside.utils.setToolbarVisibility
import com.likemagic.masters_beside.viewModel.SignViewModel

class CreateNewMasterFragment : Fragment() {

    private var _binding: FragmentCreateNewMasterBinding? = null
    private val binding: FragmentCreateNewMasterBinding
        get() = _binding!!

    private val viewModel: SignViewModel by lazy {
        ViewModelProvider(this)[SignViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val inflater = TransitionInflater.from(requireContext())
        enterTransition = inflater.inflateTransition(R.transition.fade_transition)
        exitTransition = inflater.inflateTransition(R.transition.fade_transition)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateNewMasterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolbarVisibility(requireActivity(), false)
        setUpFields()
    }


    private fun setUpFields() {
        var profession = Profession()
        var city = City()
        val professionList = Profession().getAllProfession()
        val profAdapter = ListOfProfessionAdapter()
        profAdapter.onItemClick = {
            binding.masterProfessionInput.setText(it.name)
            binding.profRecycler.visibility = GONE
            profession = it
        }
        profAdapter.setList(professionList)
        binding.profRecycler.adapter = profAdapter
        binding.masterProfessionInput.addTextChangedListener {
            val professionName = binding.masterProfessionInput.text.toString()
            binding.profRecycler.visibility = VISIBLE

        }
        binding.masterCityInput.addTextChangedListener {
            val cityList = City().getAllCities(requireContext())
            val cityName = binding.masterCityInput.text.toString()
            for (selection: City in cityList) {
                if (selection.name == cityName) {
                    binding.cityChoose.visibility = VISIBLE
                    binding.cityChoose.text = selection.name
                    binding.cityChoose.setOnClickListener {
                        binding.cityChoose.visibility = GONE
                        city = selection
                        binding.masterCity.isErrorEnabled = false
                    }
                } else {
                    binding.masterCity.error = "Город"
                }
            }
        }
        binding.newMasterSave.setOnClickListener {
            val name = binding.masterNameInput.text.toString()
            val about = binding.masterAboutInput.text.toString()
            val contact = Contact()
            contact.phone = binding.masterPhoneInput.text.toString()
            contact.email = binding.masterEmailInput.text.toString()
            if (binding.masterProfessionInput.text.isNullOrEmpty()) {
                binding.masterProfession.error = resources.getString(R.string.no_empty)
            } else if (binding.masterCityInput.text.isNullOrEmpty()) {
                binding.masterCity.error = resources.getString(R.string.no_empty)
            } else if (binding.masterNameInput.text.isNullOrEmpty()) {
                binding.masterName.error = resources.getString(R.string.no_empty)
            } else if (binding.masterCity.isErrorEnabled) {
                Snackbar.make(binding.root, "Мы не знаем такого города", Snackbar.LENGTH_SHORT)
                    .show()
            } else {
                createNewMaster(profession, city, name, about, contact)
            }
            it.hideKeyboard()
        }
    }

    private fun createNewMaster(
        profession: Profession,
        city: City,
        name: String,
        about: String,
        contact: Contact
    ) {
        val master = Master(profession, city, name, about, "0", contact, 0f, 0)
        Log.d("@@@", master.toString())
    }

    companion object {
        fun newInstance() = CreateNewMasterFragment()
    }
}