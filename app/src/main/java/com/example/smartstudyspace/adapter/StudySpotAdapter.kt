package com.example.smartstudyspace.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.smartstudyspace.R
import com.example.smartstudyspace.data.StudySpot
import com.example.smartstudyspace.databinding.ItemStudySpotBinding
import com.google.android.material.chip.Chip

class StudySpotAdapter(private var spots: List<StudySpot>) :
    RecyclerView.Adapter<StudySpotAdapter.StudySpotViewHolder>() {

    fun updateList(newList: List<StudySpot>) {
        spots = newList
        notifyDataSetChanged()
    }

    class StudySpotViewHolder(val binding: ItemStudySpotBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudySpotViewHolder {
        val binding = ItemStudySpotBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return StudySpotViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StudySpotViewHolder, position: Int) {
        val spot = spots[position]
        val context = holder.binding.root.context
        holder.binding.apply {
            tvSpotName.text = spot.name
            tvCategoryDistance.text = context.getString(R.string.category_distance_format, spot.category, spot.distance)
            tvRating.text = spot.rating.toString()
            tvReviewsCount.text = context.getString(R.string.reviews_count_format, spot.reviewsCount)
            tvAvailability.text = spot.availability
            tvTag.text = spot.tag
            ivSpotImage.setImageResource(spot.imageResId)

            cgFeatures.removeAllViews()
            spot.features.forEach { feature ->
                val chip = Chip(context).apply {
                    text = feature
                    isCheckable = false
                    isClickable = false
                }
                cgFeatures.addView(chip)
            }
        }
    }

    override fun getItemCount(): Int = spots.size
}
