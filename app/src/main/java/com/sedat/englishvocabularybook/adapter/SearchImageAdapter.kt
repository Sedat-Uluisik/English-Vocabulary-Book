package com.sedat.englishvocabularybook.adapter

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.sedat.englishvocabularybook.databinding.SearchItemLayoutBinding
import javax.inject.Inject

class SearchImageAdapter @Inject constructor(
    private val glide: RequestManager
): RecyclerView.Adapter<SearchImageAdapter.Holder>() {

    private val diffUtil = object :DiffUtil.ItemCallback<String>(){
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }
    }

    private val recylerListDiffer = AsyncListDiffer(this, diffUtil)

    var images: List<String>
        get() = recylerListDiffer.currentList
        set(value) = recylerListDiffer.submitList(value)

    private var imageClickListener: ((Bitmap) -> Unit) ?= null
    fun imageClick(listener: (Bitmap) -> Unit){
        imageClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding = SearchItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Holder(binding)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val imageUrl = images[position]

        glide.asBitmap().load(imageUrl).into(object : CustomTarget<Bitmap>(){
            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                holder.item.imageSearch.setImageBitmap(resource)

                holder.itemView.setOnClickListener {
                    imageClickListener?.let { bitmap ->
                        bitmap(resource)
                    }
                }
            }

            override fun onLoadCleared(placeholder: Drawable?) {

            }
        })
    }

    override fun getItemCount(): Int {
        return images.size
    }

    class Holder(val item: SearchItemLayoutBinding): RecyclerView.ViewHolder(item.root)

}