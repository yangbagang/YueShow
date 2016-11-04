package com.ybg.yxym.yueshow.view

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup.LayoutParams
import android.widget.TextView

import com.ybg.yxym.yueshow.R

/**
 * 自定义加载弹框
 */
class CustomerDialog : Dialog {

    constructor(context: Context, theme: Int) : super(context, theme) {
    }

    protected constructor(context: Context, cancelable: Boolean,
                          cancelListener: DialogInterface.OnCancelListener) : super(context, cancelable, cancelListener) {
    }

    constructor(context: Context) : super(context) {
    }

    /**
     * 所有的方法执行完都会返回一个Builder
     * 使得后面可以直接create和show
     */
    class Builder(private val context: Context) {
        private var title: String? = null
        private var message: String? = null
        private var positiveButtonText: String? = null//确定按钮
        private var negativeButtonText: String? = null//取消按钮
        private var color_msg: Int = 0
        private var color_btn: Int = 0
        //确定按钮事件
        private var positiveButtonClickListener: DialogInterface.OnClickListener? = null
        //取消按钮事件
        private var negativeButtonClickListener: DialogInterface.OnClickListener? = null

        /**
         * @param color
         * * 			设置信息和标题的字体颜色
         * *
         * @return
         */
        fun setMessageColor(color: Int): Builder {
            this.color_msg = color
            return this
        }

        /**
         * @param color 设置button的字体颜色
         * *
         * @return
         */
        fun setButtonColor(color: Int): Builder {
            this.color_btn = color
            return this
        }

        /**
         * 从资源文件中设置 dialog的消息
         * @param message
         * *
         * @return
         */
        fun setMessage(message: Int): Builder {
            this.message = context.getText(message) as String
            return this
        }

        /**
         * 用字符串设置 dialog的消息
         * @param message
         * *
         * @return
         */
        fun setMessage(message: String): Builder {
            this.message = message
            return this
        }

        /**
         * 从资源文件中设置 dialog的标题
         * @param title
         * *
         * @return
         */
        fun setTitle(title: Int): Builder {
            this.title = context.getText(title) as String
            return this
        }

        /**
         * 用字符串设置 dialog的标题
         * @param title
         * *
         * @return
         */
        fun setTitle(title: String): Builder {
            this.title = title
            return this
        }

        /**
         * 设置确定按钮和其点击事件
         * @param positiveButtonText
         * *
         * @return
         */
        fun setPositiveButton(positiveButtonText: String,
                              listener: DialogInterface.OnClickListener): Builder {
            this.positiveButtonText = positiveButtonText
            this.positiveButtonClickListener = listener
            return this
        }

        /**
         * 设置取消按钮和其点击事件
         * @return
         * *
         * @param negativeButtonText
         * *
         * @param listener
         */
        fun setNegativeButton(negativeButtonText: String,
                              listener: DialogInterface.OnClickListener): Builder {
            this.negativeButtonText = negativeButtonText
            this.negativeButtonClickListener = listener
            return this
        }

        /**
         * 对话框的 createview方法
         * @return
         */
        @SuppressLint("InflateParams")
        fun create(): CustomerDialog {
            val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            // 设置其风格
            val dialog = CustomerDialog(context, R.style.progressDialog)
            val layout = inflater.inflate(R.layout.customer_dialog, null)
            dialog.addContentView(layout, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT))
            if (title != null) {
                // 设置标题
                (layout.findViewById(R.id.tv_title) as TextView).text = title
                if (color_msg != 0) {
                    (layout.findViewById(R.id.tv_title) as TextView).setTextColor(color_msg)
                }
            } else {
                layout.findViewById(R.id.tv_title).visibility = View.GONE
            }
            if (positiveButtonText != null) {
                (layout.findViewById(R.id.btn_pos) as TextView).text = positiveButtonText
                if (positiveButtonClickListener != null) {
                    (layout.findViewById(R.id.btn_pos) as TextView).setOnClickListener {
                        positiveButtonClickListener!!.onClick(dialog,
                                DialogInterface.BUTTON_POSITIVE)
                    }
                    if (color_btn != 0) {
                        (layout.findViewById(R.id.btn_pos) as TextView).setTextColor(color_btn)
                    }
                }
            } else {
                layout.findViewById(R.id.btn_pos).visibility = View.GONE
            }
            if (negativeButtonText != null) {
                (layout.findViewById(R.id.btn_neg) as TextView).text = negativeButtonText
                if (negativeButtonClickListener != null) {
                    (layout.findViewById(R.id.btn_neg) as TextView).setOnClickListener { negativeButtonClickListener!!.onClick(dialog, DialogInterface.BUTTON_NEGATIVE) }
                    if (color_btn != 0) {
                        (layout.findViewById(R.id.btn_neg) as TextView).setTextColor(color_btn)
                    }
                }
            } else {
                layout.findViewById(R.id.btn_neg).visibility = View.GONE
            }
            if (message != null) {
                (layout.findViewById(R.id.tv_msg) as TextView).text = message
                if (color_msg != 0) {
                    (layout.findViewById(R.id.tv_title) as TextView).setTextColor(color_msg)
                }
            } else {
                layout.findViewById(R.id.tv_msg).visibility = View.GONE
            }
            dialog.setContentView(layout)
            return dialog
        }
    }
}
