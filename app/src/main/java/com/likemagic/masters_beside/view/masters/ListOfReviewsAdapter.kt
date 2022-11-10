package com.likemagic.masters_beside.view.masters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import coil.dispose
import coil.load
import com.likemagic.masters_beside.R
import com.likemagic.masters_beside.databinding.ItemListCityBinding
import com.likemagic.masters_beside.databinding.ItemListReviewsBinding
import com.likemagic.masters_beside.repository.City
import com.likemagic.masters_beside.repository.Review

class ListOfReviewsAdapter(private var list: List<Review> = listOf(),private val context:Context): RecyclerView.Adapter<ListOfReviewsAdapter.ReviewsViewHolder>(){

    fun setList(newList: List<Review>) {
        this.list = newList
        notifyDataSetChanged()
    }

    inner class ReviewsViewHolder(view: View):RecyclerView.ViewHolder(view) {
        val binding = ItemListReviewsBinding.bind(itemView)
        fun myBind(review: Review) {
            binding.apply {
                setReviewsStar(review.rating, binding)
                if(review.ownerPhoto.isNotEmpty()){
                    reviewOwnerPhoto.load(review.ownerPhoto)
                }else{
                    reviewOwnerPhoto.dispose()
                    reviewOwnerPhoto.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_account))
                }
                masterNameListItem.text = review.ownerName
                reviewText.text = review.text
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewsViewHolder {
        val v = ItemListReviewsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ReviewsViewHolder(v.root)
    }

    override fun onBindViewHolder(holder: ReviewsViewHolder, position: Int) {
        holder.apply{
            myBind(list[position])
        }
    }

    override fun getItemCount() = list.size

    private fun setReviewsStar(count:Int, view:ItemListReviewsBinding){
        when(count ){
            5 -> {
                view.gradeFive.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_star_gold))
                view.gradeFour.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_star_gold))
                view.gradeThree.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_star_gold))
                view.gradeTwo.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_star_gold))
                view.gradeOne.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_star_gold))
            }
            4 -> {
                view.gradeFive.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_star))
                view.gradeFour.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_star_gold))
                view.gradeThree.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_star_gold))
                view.gradeTwo.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_star_gold))
                view.gradeOne.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_star_gold))
            }
            3 -> {
                view.gradeFive.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_star))
                view.gradeFour.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_star))
                view.gradeThree.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_star_gold))
                view.gradeTwo.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_star_gold))
                view.gradeOne.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_star_gold))
            }
            2 -> {
                view.gradeFive.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_star))
                view.gradeFour.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_star))
                view.gradeThree.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_star))
                view.gradeTwo.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_star_gold))
                view.gradeOne.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_star_gold))
            }
            1 -> {
                view.gradeFive.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_star))
                view.gradeFour.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_star))
                view.gradeThree.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_star))
                view.gradeTwo.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_star))
                view.gradeOne.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_star_gold))
            }
        }
    }
}