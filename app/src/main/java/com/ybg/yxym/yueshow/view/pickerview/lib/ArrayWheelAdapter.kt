package com.ybg.yxym.yueshow.view.pickerview.lib

import java.util.ArrayList

/**
 * The simple Array wheel adapter
 * @param  the element type
 */
class ArrayWheelAdapter<T>
/**
 * Constructor
 * @param items the items
 * *
 * @param length the max items length
 */
@JvmOverloads constructor(// items
        private val items: ArrayList<T>, // length
        private val length: Int = ArrayWheelAdapter.DEFAULT_LENGTH) : WheelAdapter {

    override fun getItem(index: Int): String {
        if (index >= 0 && index < items.size) {
            return items[index].toString()
        }
        return ""
    }

    override val itemsCount: Int = items.size

    override val maximumLength: Int = length

    companion object {

        /** The default items length  */
        val DEFAULT_LENGTH = 4
    }

}
