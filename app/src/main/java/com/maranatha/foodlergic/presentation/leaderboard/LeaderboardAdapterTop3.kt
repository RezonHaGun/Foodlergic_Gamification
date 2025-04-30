package com.maranatha.foodlergic.presentation.leaderboard

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.maranatha.foodlergic.databinding.ItemLeaderboardBinding
import com.maranatha.foodlergic.databinding.ItemLeaderboardTop3Binding

class LeaderboardAdapterTop3 : ListAdapter<LeaderboardItem, LeaderboardAdapterTop3.LeaderboardViewHolder>(DIFF_CALLBACK) {

    inner class LeaderboardViewHolder(private val binding: ItemLeaderboardTop3Binding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: LeaderboardItem) {
            binding.rankTextView.text = "${item.rank}."
            binding.playerNameTextView.text = item.playerName
            binding.scoreTextView.text = item.score.toString()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LeaderboardViewHolder {
        val binding = ItemLeaderboardTop3Binding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LeaderboardViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LeaderboardViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<LeaderboardItem>() {
            override fun areItemsTheSame(oldItem: LeaderboardItem, newItem: LeaderboardItem): Boolean {
                return oldItem.rank == newItem.rank
            }

            override fun areContentsTheSame(oldItem: LeaderboardItem, newItem: LeaderboardItem): Boolean {
                return oldItem == newItem
            }
        }
    }
}
