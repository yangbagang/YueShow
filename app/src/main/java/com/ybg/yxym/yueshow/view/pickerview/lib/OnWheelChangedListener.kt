package com.ybg.yxym.yueshow.view.pickerview.lib

/**
 * Wheel changed listener interface.
 *
 * The currentItemChanged() method is called whenever current wheel positions is changed:
 *  *  New Wheel position is set
 *  *  Wheel view is scrolled
 */
interface OnWheelChangedListener {
    /**
     * Callback method to be invoked when current item changed
     * @param wheel the wheel view whose state has changed
     * *
     * @param oldValue the old value of current item
     * *
     * @param newValue the new value of current item
     */
    fun onChanged(wheel: WheelView, oldValue: Int, newValue: Int)
}
