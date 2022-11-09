package com.likemagic.masters_beside.view.masters

import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.transition.Slide
import androidx.transition.TransitionInflater
import androidx.transition.TransitionManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.likemagic.masters_beside.R
import com.likemagic.masters_beside.databinding.FragmentListOfMastersBinding
import com.likemagic.masters_beside.repository.*
import com.likemagic.masters_beside.utils.*
import com.likemagic.masters_beside.view.navigation.ProfileMasterFragment
import com.likemagic.masters_beside.viewModel.AppState
import com.likemagic.masters_beside.viewModel.MastersViewModel

class ListOfMastersFragment : Fragment(), IOnBackPressed {

    private var _binding: FragmentListOfMastersBinding? = null
    private val binding: FragmentListOfMastersBinding
        get() = _binding!!

    private val mastersViewModel: MastersViewModel by viewModels()
    private lateinit var masterAdapter:ListOfMastersAdapter
    private val searchAdapter = ListOfSearchAdapter()
    private val cityAdapter = ListOfCityAdapter()
    private lateinit var bottomSheetBehavior:BottomSheetBehavior<View>
    private var filterProfession = Profession()
    private var sortedList = arrayListOf<Master>()
    private var sortedByCostList = arrayListOf<Master>()
    private var backPress = 0
    private var closeTimer = 0L

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

    override fun onResume() {
        super.onResume()
        mastersViewModel.getMyData()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListOfMastersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolbarVisibility(requireActivity(), true)
        requireActivity().findViewById<BottomNavigationView>(R.id.bottomNav).menu.findItem(R.id.actionHome).isChecked =
            true
        bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheet.bottomSheetContainer)
        mastersViewModel.getMyData()
        mastersViewModel.getLiveData().observe(viewLifecycleOwner){
            renderData(it)
        }
        masterAdapter = ListOfMastersAdapter(myData = Master(), context = requireContext())
        initSearchRecycler(searchAdapter)
        initRecycler(masterAdapter)
        initCityRecycler(cityAdapter)
        bottomSheetBehaviorSetup()
    }

    private fun bottomSheetBehaviorSetup(){
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        binding.filterBtn.setOnClickListener {
            if(filterProfession.name.isEmpty()){
                Snackbar.make(binding.root, "Сначала выберите профессию", Snackbar.LENGTH_SHORT).show()
            }else{
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                binding.bottomSheet.filterProfessionName.text = filterProfession.name
            }

        }
    }

    private fun renderData(appState: AppState) {
        when (appState) {
            is AppState.Loading -> {
                binding.loadingLayout.visibility = VISIBLE
            }
            is AppState.MyData -> {
                masterAdapter.myData = appState.master
                mastersViewModel.getAllMasters()
            }
            is AppState.ListOfMasters -> {
                binding.loadingLayout.visibility = GONE
                binding.searchRecycler.visibility = GONE
                masterAdapter.setList(appState.list)
                setUpFilterSearch(appState.list)
            }
            is AppState.EmptyList -> {
                binding.loadingLayout.visibility = GONE
                Snackbar.make(binding.root, "Мастера не найдены", Snackbar.LENGTH_SHORT).show()
                bottomSheetBehavior.isHideable = true
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            }
            else -> {}
        }
    }

    private fun initRecycler(masterAdapter: ListOfMastersAdapter){
        binding.masterList.adapter = masterAdapter
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            binding.masterList.setOnScrollChangeListener { _, _, _, _, _ ->
                binding.titleOfListContainer.isSelected = binding.masterList.canScrollVertically(-1)
            }
        }
        masterAdapter.onItemClick ={
            navigateToMasterPage(it)
        }
    }

    private fun initSearchRecycler(searchAdapter: ListOfSearchAdapter) {
        binding.searchRecycler.adapter = searchAdapter
        binding.masterSearchInput.addTextChangedListener {
            val search = binding.masterSearchInput.text.toString()
            binding.searchRecycler.visibility = VISIBLE
            ProfessionGetter().getProfessionByKeyWords(search){
                requireActivity().runOnUiThread{
                    searchAdapter.setList(it)
                }
            }

        }
        searchAdapter.onItemClick= {profession ->
            filterProfession = profession
            mastersViewModel.getMastersByProfession(profession)
            binding.apply {
                searchRecycler.visibility = GONE
                root.hideKeyboard()
                masterSearchInput.setText("")
            }
        }
    }

    private fun initCityRecycler(cityAdapter: ListOfCityAdapter){
        binding.bottomSheet.cityRecycler.adapter = cityAdapter
        binding.bottomSheet.filterCityInput.addTextChangedListener {
            binding.bottomSheet.cityRecycler.visibility = VISIBLE
            val cityName = binding.bottomSheet.filterCityInput.text.toString()
            CityGetter().getCityByName(cityName, requireContext()){
                requireActivity().runOnUiThread {
                    cityAdapter.setList(it)
                }
            }

        }
    }

    private fun setUpFilterSearch(list:ArrayList<Master>){
        var city = City()
        val slide = Slide(Gravity.BOTTOM)
        binding.bottomSheet.filterCostInput.setText("0")
        cityAdapter.onItemClick = { select->
            binding.bottomSheet.filterCityInput.setText(select.name)
            binding.bottomSheet.cityRecycler.visibility = GONE
            city = select
            binding.bottomSheet.bottomSheetContainer.hideKeyboard()
        }
        binding.bottomSheet.toCostBtn.setOnClickListener {
            TransitionManager.beginDelayedTransition(binding.bottomSheet.bottomSheetContainer, slide)
            binding.bottomSheet.cityContainer.visibility = GONE
            binding.bottomSheet.costContainer.visibility = VISIBLE
            sortByCity(list, city)
            makeTopSnackBar("Найдено мастеров: ${sortedList.size}")
        }
        binding.bottomSheet.toCityBtn.setOnClickListener {
            TransitionManager.beginDelayedTransition(binding.bottomSheet.bottomSheetContainer, slide)
            binding.bottomSheet.cityContainer.visibility = VISIBLE
            binding.bottomSheet.costContainer.visibility = GONE
        }
        binding.bottomSheet.toSwitchBtn.setOnClickListener {
            var cost = binding.bottomSheet.filterCostInput.text.toString()
            if(cost.isEmpty())cost = "0"
            TransitionManager.beginDelayedTransition(binding.bottomSheet.bottomSheetContainer, slide)
            binding.bottomSheet.costContainer.visibility = GONE
            binding.bottomSheet.switchContainer.visibility = VISIBLE
            binding.bottomSheet.bottomSheetContainer.hideKeyboard()
            sortByCost(sortedList, cost.toInt())
            makeTopSnackBar("Найдено мастеров: ${sortedByCostList.size}")
        }
        binding.bottomSheet.backToCostBtn.setOnClickListener {
            TransitionManager.beginDelayedTransition(binding.bottomSheet.bottomSheetContainer, slide)
            binding.bottomSheet.switchContainer.visibility = GONE
            binding.bottomSheet.costContainer.visibility = VISIBLE
        }
        binding.bottomSheet.onlyVipSwitch.setOnCheckedChangeListener{_, isChecked ->
            if(isChecked){
                sortByVIP(sortedByCostList, true)
            }else{
                sortByVIP(sortedByCostList, false)
            }
        }
    }

    private fun sortByCity(list: ArrayList<Master>, city: City){
        val tempList = arrayListOf<Master>()
        tempList.addAll(list)
        masterAdapter.setList(tempList)
        if(city.name.isEmpty()){
            sortedList = list
        }else{
            for(i in list.size - 1 downTo 0){
                if(list[i].city.name != city.name){
                    tempList.removeAt(i)
                    masterAdapter.notifyItemRemoved(i)
                }
            }
            sortedList = tempList
            sortedByCostList = tempList
        }
    }

    private fun sortByCost(list:ArrayList<Master>, cost:Int){
        val tempList = arrayListOf<Master>()
        tempList.addAll(list)
        masterAdapter.setList(tempList)
        for(i:Int in  list.size - 1 downTo 0){
            if(list[i].cost.toInt() > cost){
                tempList.removeAt(i)
                masterAdapter.notifyItemRemoved(i)
            }
        }
        sortedByCostList =tempList
    }

    private fun sortByVIP(list:ArrayList<Master>, isVIP:Boolean){
        val tempList = arrayListOf<Master>()
        tempList.addAll(list)
        masterAdapter.setList(tempList)
        if(isVIP){
            for(i:Int in  list.size - 1 downTo 0){
                if(!list[i].vipStatus){
                    tempList.removeAt(i)
                    masterAdapter.notifyItemRemoved(i)
                }
            }
        }
    }

    private fun navigateToMasterPage(master: Master){
        requireActivity().supportFragmentManager
            .beginTransaction()
            .addToBackStack(PROFILE_FRAGMENT)
            .add(R.id.mainContainer, ProfileMasterFragment.newInstance(master), PROFILE_FRAGMENT)
            .commit()
    }

    private fun makeTopSnackBar(text:String){
        val snackBarView = Snackbar.make(binding.root, text , Snackbar.LENGTH_LONG)
        snackBarView.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
        val view = snackBarView.view
        val params = view.layoutParams as CoordinatorLayout.LayoutParams
        params.gravity = Gravity.TOP
        params.topMargin = 180
        view.layoutParams = params
        view.background = ContextCompat.getDrawable(requireContext(),R.drawable.custom_snackbar)
        snackBarView.animationMode = BaseTransientBottomBar.ANIMATION_MODE_FADE
        snackBarView.show()
    }

    companion object {
        @JvmStatic
        fun newInstance() = ListOfMastersFragment()
    }

    override fun onBackPressed(): Boolean {
        backPress++
        if(backPress==1){
            closeTimer = System.currentTimeMillis()
            Snackbar.make(binding.root, "Нажмите снова для выхода", Snackbar.LENGTH_SHORT).show()
        }else if(backPress==2){
            val timer = System.currentTimeMillis()
            if(timer - closeTimer> TIME_DELAY){
                backPress = 0
            }else{
                requireActivity().finish()
            }
        }
        return false
    }
}