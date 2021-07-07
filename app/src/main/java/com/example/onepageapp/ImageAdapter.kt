package com.example.onepageapp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.signature.ObjectKey
import com.example.onepageapp.databinding.RvItemImageBinding

class ImageAdapter() :
    RecyclerView.Adapter<ImageAdapter.ImageViewHolder>() {

    /**
     * ketList stores unique keys for each image
     */
    private val keyList = mutableListOf<Long>()
    private var currentKey: Long = 0

    init {
        initKeys()
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, type: Int): ImageViewHolder {
        return ImageViewHolder(
            RvItemImageBinding.inflate(
                LayoutInflater.from(viewGroup.context),
                viewGroup,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return keyList.size
    }

    override fun getItemId(position: Int): Long {
        return keyList[position]
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        holder.onBind(position)
    }

    /**
     * Method for filling keyList
     */
    private fun initKeys() {
        keyList.clear()
        while (keyList.size != 140) {
            addKey()
        }
    }

    /**
     * Method for adding unique key to keyList
     */
    private fun addKey() {
        keyList.add(currentKey)
        currentKey++
    }

    /**
     * Method for adding image
     */
    fun addImage() {
        addKey()
        notifyDataSetChanged()
    }

    /**
     * Method for loading new images
     */
    fun reloadAll() {
        initKeys()
        notifyDataSetChanged()
    }

    inner class ImageViewHolder(private val viewBinding: RvItemImageBinding) :
        RecyclerView.ViewHolder(viewBinding.root) {

        fun onBind(position: Int) {
            val imageKey = keyList[position]
            Glide.with(viewBinding.ivImage)
                .load(IMAGE_URL).apply(
                    RequestOptions()
                        .centerCrop()
                        .signature(ObjectKey(imageKey))
                        .transform(RoundedCorners(7))
                        .placeholder(R.drawable.ic_download)
                )
                .into(viewBinding.ivImage)
        }

    }

    companion object {
        private const val IMAGE_URL = "https://loremflickr.com/200/200/"
    }

}