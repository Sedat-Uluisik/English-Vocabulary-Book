package com.sedat.englishvocabularybook.adapter

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.sedat.englishvocabularybook.databinding.HomeItemLayoutBinding
import com.sedat.englishvocabularybook.model.Word
import javax.inject.Inject

class HomeAdapter @Inject constructor(
        private val glide: RequestManager
): RecyclerView.Adapter<HomeAdapter.Holder>() {

    private val diffUtil = object :DiffUtil.ItemCallback<Word>(){
        override fun areItemsTheSame(oldItem: Word, newItem: Word): Boolean {
            return oldItem.rowid == newItem.rowid
        }

        override fun areContentsTheSame(oldItem: Word, newItem: Word): Boolean {
            return oldItem == newItem
        }
    }

    private val recyclerListDiffer = AsyncListDiffer(this, diffUtil)

    var words: List<Word>
        get() = recyclerListDiffer.currentList
        set(value) = recyclerListDiffer.submitList(value)

    private var onItemClick: ((Word) -> Unit) ?= null
    fun setOnItemClick(listener: (Word) -> Unit){
        onItemClick = listener
    }

    private var onMoreBtnClick: ((Word, View)->Unit) ?= null
    fun setOnMoreBtnClick(listener: (Word, View) -> Unit){
        onMoreBtnClick = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeAdapter.Holder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = HomeItemLayoutBinding.inflate(inflater, parent, false)
        return Holder(binding)
    }

    override fun onBindViewHolder(holder: HomeAdapter.Holder, position: Int) {
        val word = words[position]

        if(word.imageBitmap != null)
            glide.load(word.imageBitmap).into(holder.item.homeItemImage)
        else
            holder.item.homeItemImage.visibility = View.GONE

        holder.item.enText.text = word.en
        holder.item.trText.text = word.tr
        holder.item.sentenceText.text = word.sentence

        holder.itemView.setOnClickListener {
            onItemClick?.let {
                it(word)
            }
        }

        holder.item.moreButton.setOnClickListener { view ->
            onMoreBtnClick?.let {
                it(word, view)
            }
        }
    }

    override fun getItemCount(): Int {
        return words.size
    }

    class Holder(val item: HomeItemLayoutBinding): RecyclerView.ViewHolder(item.root)

}