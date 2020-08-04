package com.egiwon.swiperecyclerviewsample.ui

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.egiwon.swiperecyclerviewsample.base.BaseViewModel
import com.egiwon.swiperecyclerviewsample.ui.model.SampleItem
import kotlin.random.Random

class MainViewModel @ViewModelInject constructor() : BaseViewModel() {

    private val _items = MutableLiveData<List<SampleItem>>()
    val items: LiveData<List<SampleItem>> get() = _items

    private val randomArray = arrayOf(
        "BMW 320i",
        "BMW 520i",
        "BMW 530d",
        "BMW X3",
        "BMW X5",
        "BENZ E220d",
        "BENZ E300",
        "BENZ C200"
    )

    fun loadSampleItems() {

        val dummyList = mutableListOf<SampleItem>()
        for (i in 0..30) {
            val index = Random.nextInt(0, randomArray.lastIndex)
            dummyList += SampleItem(
                i,
                randomArray[index]
            )
        }

        _items.value = dummyList

    }
}