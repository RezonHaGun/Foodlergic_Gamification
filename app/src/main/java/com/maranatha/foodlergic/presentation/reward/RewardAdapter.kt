package com.maranatha.foodlergic.presentation.reward

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.maranatha.foodlergic.R
import com.maranatha.foodlergic.domain.models.Book

class RewardAdapter(private val bookList: List<Book>) :
    RecyclerView.Adapter<RewardAdapter.RewardViewHolder>() {

    var onItemClick: ((Book) -> Unit)? = null

    class RewardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageBook: ImageView = itemView.findViewById(R.id.imageBook)
        val bookTitle: TextView = itemView.findViewById(R.id.bookTitle)
        val summaryText: TextView = itemView.findViewById(R.id.summaryText) // Menambahkan TextView untuk summary
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RewardViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.book_cardview, parent, false)
        return RewardViewHolder(view)
    }

    override fun getItemCount(): Int {
        return bookList.size
    }

    override fun onBindViewHolder(holder: RewardViewHolder, position: Int) {
        val book = bookList[position]
        holder.imageBook.setImageResource(book.image)
        holder.bookTitle.text = book.name
        holder.summaryText.text = book.summary // Mengisi TextView dengan teks summary

        holder.itemView.setOnClickListener {
            onItemClick?.invoke(book)
        }
    }
}
