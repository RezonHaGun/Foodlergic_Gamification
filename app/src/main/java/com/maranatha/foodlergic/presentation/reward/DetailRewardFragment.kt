package com.maranatha.foodlergic.Reward

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.maranatha.foodlergic.R
import com.maranatha.foodlergic.databinding.FragmentDetailRewardBinding
import com.maranatha.foodlergic.domain.models.Book

class DetailRewardFragment : Fragment(R.layout.fragment_detail_reward) {

    private lateinit var binding: FragmentDetailRewardBinding

    private val args: DetailRewardFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentDetailRewardBinding.bind(view)

        val book = args.book
        binding.detailReward.text = book.name
        binding.detailImage.setImageResource(book.image)
        binding.summaryText.text = book.summary // Menampilkan summary buku

        binding.downloadButton.setOnClickListener {
            openGoogleDriveInBrowser(book)
        }
    }

    private fun openGoogleDriveInBrowser(book: Book) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(book.urlBook))
        startActivity(intent)
    }
}
