package com.example.smartstudyspace.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.smartstudyspace.R
import com.example.smartstudyspace.StudySpotDetailActivity
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
            tvCategoryDistance.text = context.getString(R.string.category_distance_format, spot.category, spot.distance)
            tvRating.text = spot.rating.toString()
            tvReviewsCount.text = context.getString(R.string.reviews_count_format, spot.reviewsCount)
            tvAvailability.text = spot.availability
            tvTag.text = spot.tag
            ivSpotImage.setImageResource(spot.imageResId)

            root.setOnClickListener {
                val intent = Intent(context, StudySpotDetailActivity::class.java).apply {
                    putExtra("SPOT_NAME", spot.name)
                    putExtra("SPOT_CATEGORY", spot.category)
                    putExtra("SPOT_DISTANCE", spot.distance)
                    putExtra("SPOT_RATING", spot.rating)
                    putExtra("SPOT_IMAGE", spot.imageResId)
                    putExtra("SPOT_TAG", spot.tag)
                }
                context.startActivity(intent)
            }

            btnReserve.setOnClickListener {
                val intent = Intent(context, StudySpotDetailActivity::class.java).apply {
                    putExtra("SPOT_NAME", spot.name)
                    putExtra("SPOT_CATEGORY", spot.category)
                    putExtra("SPOT_DISTANCE", spot.distance)
                    putExtra("SPOT_RATING", spot.rating)
                    putExtra("SPOT_IMAGE", spot.imageResId)
                    putExtra("SPOT_TAG", spot.tag)
                }
                context.startActivity(intent)
            }

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
