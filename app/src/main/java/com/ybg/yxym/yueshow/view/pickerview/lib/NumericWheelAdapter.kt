package com.ybg.yxym.yueshow.view.pickerview.lib


/**
 * Numeric Wheel adapter.
 */
class NumericWheelAdapter
/**
 * Constructor
 * @param minValue the wheel min value
 * *
 * @param maxValue the wheel max value
 * *
 * @param format the format string
 */
@JvmOverloads constructor(// Values
        private val minValue: Int = NumericWheelAdapter.DEFAULT_MIN_VALUE, private val maxValue: Int = NumericWheelAdapter.DEFAULT_MAX_VALUE, // format
        private val format: String? = null) : WheelAdapter {

    override fun getItem(index: Int): String {
        if (index >= 0 && index < itemsCount) {
            val value = minValue + index
            return if (format != null) String.format(format, value) else Integer.toString(value)
        }
        return ""
    }

    override val itemsCount: Int
        get() {
            return maxValue - minValue + 1
        }

    override val maximumLength: Int
        get() {
            val max = Math.max(Math.abs(maxValue), Math.abs(minValue))
            var maxLen = Integer.toString(max).length
            if (minValue < 0) {
                maxLen++
            }
            return maxLen
        }

    companion object {

        /** The default min value  */
        val DEFAULT_MAX_VALUE = 9

        /** The default max value  */
        private val DEFAULT_MIN_VALUE = 0
    }
}
