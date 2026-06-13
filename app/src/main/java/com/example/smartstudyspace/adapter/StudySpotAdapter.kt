package com.example.smartstudyspace.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.smartstudyspace.StudySpotDetailActivity
import com.example.smartstudyspace.data.ImageResolver
import com.example.smartstudyspace.data.StudySpot
import com.example.smartstudyspace.databinding.ItemStudySpotBinding
import com.google.android.material.chip.Chip

class StudySpotAdapter(private var spots: List<StudySpot>) :
    RecyclerView.Adapter<StudySpotAdapter.StudySpotViewHolder>() {

    fun updateList(newList: List<StudySpot>) {
        val diffResult = DiffUtil.calculateDiff(StudySpotDiffCallback(spots, newList))
        spots = newList
        diffResult.dispatchUpdatesTo(this)
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
            tvCategoryDistance.text = context.getString(com.example.smartstudyspace.R.string.category_distance_format, spot.category, spot.distance)
            val reviewsCountText = context.getString(com.example.smartstudyspace.R.string.reviews_count_format, spot.reviewsCount)
            tvRatingAndCount.text = context.getString(com.example.smartstudyspace.R.string.rating_reviews_format, spot.rating, reviewsCountText)
            tvAvailability.text = spot.availability
            tvTag.text = spot.tag

            if (spot.imageResId != 0) {
                ivSpotImage.setImageResource(spot.imageResId)
            }

            val navigateToDetail = {
                val intent = Intent(context, StudySpotDetailActivity::class.java).apply {
                    putExtra(StudySpotDetailActivity.EXTRA_SPOT_ID, spot.id)
                    putExtra(StudySpotDetailActivity.EXTRA_SPOT_NAME, spot.name)
                    putExtra(StudySpotDetailActivity.EXTRA_SPOT_CATEGORY, spot.category)
                    putExtra(StudySpotDetailActivity.EXTRA_SPOT_DISTANCE, spot.distance)
                    putExtra(StudySpotDetailActivity.EXTRA_SPOT_RATING, spot.rating)
                    putExtra(StudySpotDetailActivity.EXTRA_SPOT_IMAGE, spot.imageResId)
                    putExtra(StudySpotDetailActivity.EXTRA_SPOT_TAG, spot.tag)
                    putExtra(StudySpotDetailActivity.EXTRA_SPOT_LATITUDE, spot.latitude)
                    putExtra(StudySpotDetailActivity.EXTRA_SPOT_LONGITUDE, spot.longitude)
                }
                context.startActivity(intent)
            }

            root.setOnClickListener { navigateToDetail() }
            btnReserve.setOnClickListener { navigateToDetail() }

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

    class StudySpotDiffCallback(
        private val oldList: List<StudySpot>,
        private val newList: List<StudySpot>
    ) : DiffUtil.Callback() {
        override fun getOldListSize(): Int = oldList.size
        override fun getNewListSize(): Int = newList.size
        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].id == newList[newItemPosition].id
        }
        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }
    }
}
