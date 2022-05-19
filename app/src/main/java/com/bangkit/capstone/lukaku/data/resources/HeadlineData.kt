package com.bangkit.capstone.lukaku.data.resources

import com.bangkit.capstone.lukaku.R
import com.bangkit.capstone.lukaku.models.Headline

object HeadlineData {

    fun getHeadlines(): List<Headline> = HEADLINE

    private val HEADLINE_1: Headline =
        Headline(R.drawable.ic_detection, "Start detection", "for all wounds.")

    private val HEADLINE_2: Headline =
        Headline(R.drawable.ic_health, "Latest Technology", "fast and accurate information.")

    private val HEADLINE_3: Headline =
        Headline(
            R.drawable.ic_drug,
            "Drug recommendations",
            "according to the results of wound detection."
        )

    private val HEADLINE: ArrayList<Headline> = arrayListOf(HEADLINE_1, HEADLINE_2, HEADLINE_3)
}