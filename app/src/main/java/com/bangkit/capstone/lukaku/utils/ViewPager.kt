package com.bangkit.capstone.lukaku.utils

import android.view.View
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.delay
import kotlin.math.abs

object ViewPager {

    private const val MARGIN_PX = 20
    private const val PERCENT_X = 0.8f
    private const val PERCENT_Y = 0.2f
    private const val DEFAULT = 1

    fun ViewPager2.transformer() {
        val transformer = CompositePageTransformer().apply {
            addTransformer(MarginPageTransformer(MARGIN_PX))
            addTransformer { page: View, position: Float ->
                val transport = DEFAULT - abs(position)
                page.scaleY = PERCENT_X + transport * PERCENT_Y
            }
        }

        setPageTransformer(transformer)
    }

    fun ViewPager2.mediator(tabLayout: TabLayout, title: List<String>) {
        TabLayoutMediator(
            tabLayout, this
        ) { tab: TabLayout.Tab, position: Int ->
            tab.text = title[position]
        }.attach()
    }

    suspend fun ViewPager2.autoScroll(interval: Long) {
        delay(interval)
        val itemCount = adapter?.itemCount ?: 0
        val lastItem = if (itemCount > 0) itemCount - 1 else 0
        val nextItem = if (currentItem == lastItem) 0 else currentItem + 1

        setCurrentItem(nextItem, true)
        this.autoScroll(interval)
    }

    fun ViewPager2.autoScroll(
        coroutineScope: LifecycleCoroutineScope,
        interval: Long
    ) {
        coroutineScope.launchWhenResumed {
            autoScroll(interval)
        }
    }
}