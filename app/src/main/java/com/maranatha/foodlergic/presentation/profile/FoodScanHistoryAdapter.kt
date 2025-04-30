package com.maranatha.foodlergic.presentation.profile

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.maranatha.foodlergic.R
import com.maranatha.foodlergic.databinding.ItemFoodScanHistoryBinding
import com.maranatha.foodlergic.domain.models.Predict

class FoodScanHistoryAdapter :
    ListAdapter<Predict, FoodScanHistoryAdapter.FoodScanHistoryViewHolder>(DIFF_CALLBACK) {

    inner class FoodScanHistoryViewHolder(private val binding: ItemFoodScanHistoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Predict) {
            if (item.hasAllergy) {
                binding.statusIcon.setImageResource(R.drawable.ic_cross)
                binding.tvAllergydesc.text = "You have an allergy to these foods"
            } else {
                binding.statusIcon.setImageResource(R.drawable.ic_checked)
                binding.tvAllergydesc.text = "Safe for consumption"
            }
            binding.tvAllergyLabel.text = item.predictedAllergen
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodScanHistoryViewHolder {
        val binding =
            ItemFoodScanHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FoodScanHistoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FoodScanHistoryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Predict>() {
            override fun areItemsTheSame(
                oldItem: Predict,
                newItem: Predict
            ): Boolean {
                return oldItem.timestamp == newItem.timestamp
            }

            override fun areContentsTheSame(
                oldItem: Predict,
                newItem: Predict
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}