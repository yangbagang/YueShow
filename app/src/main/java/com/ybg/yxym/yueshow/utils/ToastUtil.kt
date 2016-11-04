package com.ybg.yxym.yueshow.utils

import android.support.design.widget.Snackbar
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast

import com.ybg.yxym.yueshow.R
import com.ybg.yxym.yueshow.app.ShowApplication


/**
 * ToastUtil
 */
object ToastUtil {

    fun show(resId: Int) {
        show(ShowApplication.instance!!.resources.getText(resId), Toast.LENGTH_SHORT)
    }

    fun show(resId: Int, duration: Int) {
        show(ShowApplication.instance!!.resources.getText(resId), duration)
    }

    fun toast(text: CharSequence) {
        show(text, Toast.LENGTH_SHORT)
    }

    @JvmOverloads fun show(text: CharSequence, duration: Int = Toast.LENGTH_SHORT) {
        val view = LayoutInflater.from(ShowApplication.instance).inflate(R.layout.layout_toast_center, null)
        val messageTv = view.findViewById(R.id.tv_title_toast) as TextView
        messageTv.text = text
        val toast = Toast(ShowApplication.instance)
        toast.setGravity(Gravity.BOTTOM, 0, 100)
        toast.duration = duration
        toast.view = view
        toast.show()
    }

    @JvmOverloads fun showSnack(view: View, message: String, action: String? = null, listener: View.OnClickListener? = null) {
        val snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG).setAction(action, listener)
        (snackbar.view.findViewById(R.id.snackbar_text) as TextView).setTextColor(ResourcesUtils.getColor(R.color.white))
        snackbar.show()
    }
}
