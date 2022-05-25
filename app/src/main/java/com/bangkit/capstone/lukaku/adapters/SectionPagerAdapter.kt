package com.bangkit.capstone.lukaku.adapters

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.bangkit.capstone.lukaku.ui.notifications.messages.MessagesFragment
import com.bangkit.capstone.lukaku.ui.notifications.updates.UpdatesFragment

class SectionPagerAdapter(
    fa: FragmentActivity
) : FragmentStateAdapter(fa) {

//    private var fragmentBundle: Bundle = bundle
//
//    init {
//        fragmentBundle = bundle
//    }

    override fun getItemCount(): Int = 2

    override fun createFragment(
        position: Int
    ): Fragment {
        var fragment: Fragment? = null
        when (position) {
            0 -> fragment = UpdatesFragment()
            1 -> fragment = MessagesFragment()
        }
//        fragment?.arguments = this.fragmentBundle
        return fragment as Fragment
    }
}