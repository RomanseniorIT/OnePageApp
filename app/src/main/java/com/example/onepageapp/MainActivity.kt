package com.example.onepageapp

import android.graphics.Point
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.onepageapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private var imageAdapter: ImageAdapter? = null
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        initViews()
        initClicks()

    }

    private fun initViews() {
        imageAdapter = ImageAdapter()
        imageAdapter?.setHasStableIds(true)
        binding.rvPhotos.adapter = imageAdapter
        binding.rvPhotos.setItemViewCacheSize(70)
        binding.rvPhotos.setHasFixedSize(true)
        val columnCount = 10
        val point = Point()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            display?.getRealSize(point)
        } else {
            windowManager?.defaultDisplay?.getRealSize(point)
        }
        val screenWidth = point.x
        val extraLayoutSpace = screenWidth * 2
        val layoutManger = PreCashingLayoutManager(
            this,
            columnCount,
            RecyclerView.HORIZONTAL,
            false,
            extraLayoutSpace
        )
        layoutManger.isSmoothScrollbarEnabled = true
        binding.rvPhotos.layoutManager = layoutManger
        val itemSpace = resources.getDimensionPixelSize(R.dimen.dimen_1pt)
        val imageDecoration = ImageDecoration(itemSpace)
        binding.rvPhotos.addItemDecoration(imageDecoration)
    }

    private fun initClicks() {
        binding.btnAddImage.setOnClickListener {
            imageAdapter?.addImage()
            scrollRvToEnd()
        }

        binding.btnReloadAll.setOnClickListener {
            imageAdapter?.reloadAll()
            scrollRvToStart()
        }
    }

    private fun scrollRvToStart() {
        val rvSize = imageAdapter?.itemCount ?: -1
        if (rvSize > 0) {
            binding.rvPhotos.scrollToPosition(0)
        }
    }

    private fun scrollRvToEnd() {
        val positionToScroll = imageAdapter?.itemCount?.minus(1)
        positionToScroll?.let { binding.rvPhotos.scrollToPosition(it) }
    }

}