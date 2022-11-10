package com.likemagic.masters_beside.view.masters

import android.app.AlertDialog
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.transition.TransitionInflater
import com.google.android.material.snackbar.Snackbar
import com.likemagic.masters_beside.R
import com.likemagic.masters_beside.databinding.FragmentReviewsListBinding
import com.likemagic.masters_beside.databinding.NewReviewDialogBinding
import com.likemagic.masters_beside.repository.Master
import com.likemagic.masters_beside.repository.Review
import com.likemagic.masters_beside.utils.MASTER_REV
import com.likemagic.masters_beside.utils.setToolbarVisibility
import com.likemagic.masters_beside.viewModel.AppState
import com.likemagic.masters_beside.viewModel.MastersViewModel
import kotlin.math.roundToInt

class ListOfReviewsFragment : Fragment(){

    private var _binding: FragmentReviewsListBinding? = null
    private val binding: FragmentReviewsListBinding
        get() = _binding!!

    private val mastersViewModel: MastersViewModel by viewModels()
    private lateinit var revAdapter: ListOfReviewsAdapter
    private lateinit var myData:Master
    private var grade = 0

    override fun onDestroyView() {
        setToolbarVisibility(requireActivity(), false)
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
        _binding = FragmentReviewsListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolbarVisibility(requireActivity(), false)
        revAdapter = ListOfReviewsAdapter(context = requireContext())
        initRecycler()
        mastersViewModel.getLiveData().observe(viewLifecycleOwner){
            renderData(it)
        }
        mastersViewModel.getMyData()
    }

    private fun renderData(appState: AppState) {
        when(appState){
            is AppState.Loading->{
                binding.loadingLayout.visibility = VISIBLE
            }
            is AppState.MyData ->{
                myData = appState.master
                val args = arguments
                val master = args?.getParcelable<Master>(MASTER_REV)
                mastersViewModel.getMaster(master!!, false)
            }
            is AppState.MasterPage->{
                binding.loadingLayout.visibility = GONE
                addReview(appState.master)
                if(appState.master.reviews.isEmpty()){
                    binding.emptyListReviews.visibility = VISIBLE
                }else{
                    revAdapter.setList(appState.master.reviews)
                }
            }
            is AppState.UpdateMaster -> {
                binding.loadingLayout.visibility = GONE
                revAdapter.setList(appState.master.reviews)
                binding.emptyListReviews.visibility = GONE
            }
            else -> {}
        }
    }

    private fun addReview(master:Master){
        if(master.uid == myData.uid){
            binding.newReviewsBtn.visibility = GONE
        }
        binding.newReviewsBtn.setOnClickListener {
            createReviewDialog(master)
        }
    }

    private fun createReviewDialog(master: Master){
        val builder = AlertDialog.Builder(requireContext())
        val view = NewReviewDialogBinding.inflate(requireActivity().layoutInflater)
        builder.setView(view.root)
        val dialog = builder.show()
        setGrade(view)
        view.saveBtn.setOnClickListener {
            val text = view.editRev.text.toString()
            if(text.isNotEmpty()){
                if(grade != 0){
                    val review = Review(myData.name, myData.uriImage, grade, text)
                    master.reviews.add(review)
                    var sum = 0
                    for(item in master.reviews){
                        sum += item.rating
                    }
                    master.rating = (sum.toDouble()/master.reviews.size*100).roundToInt()/100.0
                    mastersViewModel.addReview(master)
                    dialog.dismiss()
                }else{
                    Snackbar.make(view.root, "Оцените мастера", Snackbar.LENGTH_SHORT).show()
                }
            }else{
                Snackbar.make(view.root, "Напишите текст отзыва", Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    private fun setGrade(view: NewReviewDialogBinding): Int {
        view.gradeFive.setOnClickListener {
            view.gradeFive.setImageDrawable(ContextCompat.getDrawable(requireContext(),R.drawable.ic_star_gold))
            view.gradeFour.setImageDrawable(ContextCompat.getDrawable(requireContext(),R.drawable.ic_star_gold))
            view.gradeThree.setImageDrawable(ContextCompat.getDrawable(requireContext(),R.drawable.ic_star_gold))
            view.gradeTwo.setImageDrawable(ContextCompat.getDrawable(requireContext(),R.drawable.ic_star_gold))
            view.gradeOne.setImageDrawable(ContextCompat.getDrawable(requireContext(),R.drawable.ic_star_gold))
            grade = 5
        }
        view.gradeFour.setOnClickListener {
            view.gradeFive.setImageDrawable(ContextCompat.getDrawable(requireContext(),R.drawable.ic_star))
            view.gradeFour.setImageDrawable(ContextCompat.getDrawable(requireContext(),R.drawable.ic_star_gold))
            view.gradeThree.setImageDrawable(ContextCompat.getDrawable(requireContext(),R.drawable.ic_star_gold))
            view.gradeTwo.setImageDrawable(ContextCompat.getDrawable(requireContext(),R.drawable.ic_star_gold))
            view.gradeOne.setImageDrawable(ContextCompat.getDrawable(requireContext(),R.drawable.ic_star_gold))
            grade = 4
        }
        view.gradeThree.setOnClickListener {
            view.gradeFive.setImageDrawable(ContextCompat.getDrawable(requireContext(),R.drawable.ic_star))
            view.gradeFour.setImageDrawable(ContextCompat.getDrawable(requireContext(),R.drawable.ic_star))
            view.gradeThree.setImageDrawable(ContextCompat.getDrawable(requireContext(),R.drawable.ic_star_gold))
            view.gradeTwo.setImageDrawable(ContextCompat.getDrawable(requireContext(),R.drawable.ic_star_gold))
            view.gradeOne.setImageDrawable(ContextCompat.getDrawable(requireContext(),R.drawable.ic_star_gold))
            grade = 3
        }
        view.gradeTwo.setOnClickListener {
            view.gradeFive.setImageDrawable(ContextCompat.getDrawable(requireContext(),R.drawable.ic_star))
            view.gradeFour.setImageDrawable(ContextCompat.getDrawable(requireContext(),R.drawable.ic_star))
            view.gradeThree.setImageDrawable(ContextCompat.getDrawable(requireContext(),R.drawable.ic_star))
            view.gradeTwo.setImageDrawable(ContextCompat.getDrawable(requireContext(),R.drawable.ic_star_gold))
            view.gradeOne.setImageDrawable(ContextCompat.getDrawable(requireContext(),R.drawable.ic_star_gold))
            grade = 2
        }
        view.gradeOne.setOnClickListener {
            view.gradeFive.setImageDrawable(ContextCompat.getDrawable(requireContext(),R.drawable.ic_star))
            view.gradeFour.setImageDrawable(ContextCompat.getDrawable(requireContext(),R.drawable.ic_star))
            view.gradeThree.setImageDrawable(ContextCompat.getDrawable(requireContext(),R.drawable.ic_star))
            view.gradeTwo.setImageDrawable(ContextCompat.getDrawable(requireContext(),R.drawable.ic_star))
            view.gradeOne.setImageDrawable(ContextCompat.getDrawable(requireContext(),R.drawable.ic_star_gold))
            grade = 1
        }
        return grade
    }

    private fun initRecycler(){
        binding.reviewsRecycler.adapter = revAdapter
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            binding.reviewsRecycler.setOnScrollChangeListener { _, _, _, _, _ ->
                binding.titleOfListContainer.isSelected = binding.reviewsRecycler.canScrollVertically(-1)
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(master:Master):ListOfReviewsFragment{
            val args = Bundle()
            args.putParcelable(MASTER_REV, master)
            val fragment = ListOfReviewsFragment()
            fragment.arguments = args
            return fragment
        }
    }

}