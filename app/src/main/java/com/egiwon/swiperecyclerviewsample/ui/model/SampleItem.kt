package com.egiwon.swiperecyclerviewsample.ui.model

import com.egiwon.swiperecyclerviewsample.base.BaseIdentifier

data class SampleItem(
    override val id: Int,
    val title: String
) : BaseIdentifier()