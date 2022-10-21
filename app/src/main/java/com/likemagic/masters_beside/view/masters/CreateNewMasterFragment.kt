package com.likemagic.masters_beside.view.masters

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.transition.TransitionInflater
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import com.likemagic.masters_beside.R
import com.likemagic.masters_beside.databinding.FragmentCreateNewMasterBinding
import com.likemagic.masters_beside.repository.*
import com.likemagic.masters_beside.utils.CREATE_NEW_MASTER_FRAGMENT
import com.likemagic.masters_beside.utils.hideKeyboard
import com.likemagic.masters_beside.utils.removeFragment
import com.likemagic.masters_beside.utils.setToolbarVisibility
import com.likemagic.masters_beside.viewModel.SignViewModel

class CreateNewMasterFragment : Fragment() {

    private var _binding: FragmentCreateNewMasterBinding? = null
    private val binding: FragmentCreateNewMasterBinding
        get() = _binding!!

    private val viewModel: SignViewModel by activityViewModels()

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
        detectBackClick()
    }


    private fun setUpFields() {
        var profession = Profession()
        var city = City()
        val professionList = ProfessionGetter().getAllProfession()
        val profAdapter = ListOfProfessionAdapter()
        val cityList = CityGetter().getAllCities(requireContext())
        val cityAdapter = ListOfCityAdapter()

        binding.profRecycler.adapter = profAdapter
        initProfRecycler(profAdapter)
        profAdapter.onItemClick = {
            binding.masterProfessionInput.setText(it.name)
            binding.profRecycler.visibility = GONE
            binding.masterProfession.isErrorEnabled = false
            profession = it
        }

        binding.cityRecycler.adapter = cityAdapter
        initCityRecycler(cityAdapter)
        cityAdapter.onItemClick = {
            binding.masterCityInput.setText(it.name)
            binding.cityRecycler.visibility = GONE
            binding.masterCity.isErrorEnabled = false
            city = it
        }
        binding.newMasterSave.setOnClickListener {
            saveMaster(profession, city, cityList, professionList)
        }

    }

    private fun initProfRecycler(profAdapter: ListOfProfessionAdapter
    ) {
        binding.masterProfessionInput.addTextChangedListener {
            val professionName = binding.masterProfessionInput.text.toString()
            binding.profRecycler.visibility = VISIBLE
            ProfessionGetter().getProfessionByName(professionName){
               requireActivity().runOnUiThread{
                   profAdapter.setList(it)
               }
            }
        }
    }

    private fun initCityRecycler(cityAdapter: ListOfCityAdapter) {
        binding.masterCityInput.addTextChangedListener {
            binding.cityRecycler.visibility = VISIBLE
            val cityName = binding.masterCityInput.text.toString()
            CityGetter().getCityByName(cityName, requireContext()){
                requireActivity().runOnUiThread {
                    cityAdapter.setList(it)
                }
            }

        }
    }

    private fun saveMaster(profession: Profession, city: City, cityList: List<City>, professionList:List<Profession>) {
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
        } else  {
            if(city in cityList ){
                if(profession in professionList){
                    createNewMaster(profession, city,name, about, contact)
                    removeFragment(CREATE_NEW_MASTER_FRAGMENT, requireActivity())
                }else(Snackbar.make(binding.root, "Выберите профессию из списка", Snackbar.LENGTH_SHORT)
                    .show())
            }else(Snackbar.make(binding.root, "Выберите город из списка", Snackbar.LENGTH_SHORT)
                .show())
        }
        binding.root.hideKeyboard()
    }

    private fun createNewMaster(
        profession: Profession,
        city: City,
        name: String,
        about: String,
        contact: Contact,
    ) {
        val master = Master(profession, city, name, about, "0", contact, 0.0, 0)
        viewModel.createNewMaster(master)
    }

    private fun detectBackClick() {
        requireView().isFocusableInTouchMode = true
        requireView().requestFocus()
        requireView().setOnKeyListener { _, keyCode, event ->
            if (event.action === KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                createNewMaster(Profession(),City(),"Мастер", "", Contact())
                true
            } else false
        }
    }

    companion object {
        fun newInstance() = CreateNewMasterFragment()
    }
}