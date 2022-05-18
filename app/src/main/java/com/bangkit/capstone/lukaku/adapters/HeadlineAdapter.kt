package com.bangkit.capstone.lukaku.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bangkit.capstone.lukaku.R
import com.bangkit.capstone.lukaku.databinding.ItemListHeadlineBinding
import com.bangkit.capstone.lukaku.databinding.ItemListHeadlineBinding.inflate
import com.bangkit.capstone.lukaku.models.Headline
import com.bumptech.glide.Glide

class HeadlineAdapter(
    private var data: List<Headline>
) : RecyclerView.Adapter<HeadlineAdapter.ListViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ListViewHolder {
        val binding = inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val headline = data[position]
        holder.bind(headline)
    }

    override fun getItemCount(): Int = data.size

    class ListViewHolder(
        private var binding: ItemListHeadlineBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(headline: Headline) {
            binding.apply {
                Glide.with(itemView.context)
                    .load(headline.image)
                    .centerCrop()
                    .placeholder(R.drawable.ic_image_load)
                    .error(R.drawable.ic_image_broken)
                    .into(ivHeadline)

                tvTitle.text = headline.title
                tvDescription.text = headline.description
            }
        }
    }
}