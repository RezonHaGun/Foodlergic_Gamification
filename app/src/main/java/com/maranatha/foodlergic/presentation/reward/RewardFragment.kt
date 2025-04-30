package com.maranatha.foodlergic.presentation.reward

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.maranatha.foodlergic.R
import com.maranatha.foodlergic.databinding.FragmentRewardBinding
import com.maranatha.foodlergic.domain.models.Book

class RewardFragment : Fragment(R.layout.fragment_reward) {

    private lateinit var binding: FragmentRewardBinding
    private lateinit var bookList: List<Book>
    private lateinit var rewardAdapter: RewardAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentRewardBinding.bind(view)

        // Set RecyclerView with GridLayoutManager (3 columns)
        binding.recyclerView.layoutManager = GridLayoutManager(requireContext(), 2) // 3 columns
        binding.recyclerView.setHasFixedSize(true)

        bookList = getListBook()
        rewardAdapter = RewardAdapter(bookList)
        binding.recyclerView.adapter = rewardAdapter

        rewardAdapter.onItemClick = { book ->
            val action = RewardFragmentDirections.actionRewardFragmentToDetailRewardFragment(book)
            findNavController().navigate(action)
        }
    }

    fun getListBook(): List<Book> {
        return listOf(
            Book(
                R.drawable.a_001_book,
                "Easy Allergy-Free Cooking",
                "a_001_book",
                "https://drive.google.com/file/d/1X0oO8vfNodrtvMzM1AhUupLhfqc187OY/view?usp=sharing",
                2500,
                "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s..."
            ),
            Book(
                R.drawable.a_002_book,
                "The Autoimmune Protocol Cookbook",
                "a_002_book",
                "https://drive.google.com/file/d/198glwmmQXSxMjL8rB9y7S2hJOVPJnrM8/view?usp=sharing",
                3100,
                "It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged..."
            ),
            Book(
                R.drawable.a_003_book,
                "Allergic: A Graphic Novel",
                "a_003_book",
                "https://drive.google.com/file/d/16Sp-uFLHVP-CO3XTwhwOjRPcdz_dQOcf/view?usp=sharing",
                4000,
                "It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages, and more recently with desktop publishing software..."
            ),
            Book(
                R.drawable.a_004_book,
                "Deep Medicine",
                "a_004_book",
                "https://drive.google.com/file/d/1bAjHSukZbOjAfQBJBfyAMoFj13npOIHT/view?usp=sharing",
                2900,
                "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s..."
            ),
            Book(
                R.drawable.a_005_book,
                "Anti-Inflammatory Diet",
                "a_005_book",
                "https://drive.google.com/file/d/1XCuLeMfDH7oWZgagyGOSnPDoW84uA73j/view?usp=sharing",
                3300,
                "It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged..."
            )
        )
    }
}
