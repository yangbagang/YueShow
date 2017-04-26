package com.ybg.yxym.yueshow.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.TextView
import com.ybg.yxym.yb.bean.RuiCard
import com.ybg.yxym.yueshow.R

/**
 * Created by yangbagang on 2017/4/24.
 */
class CardItemAdapter(private val context: Context) : BaseAdapter() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)

    private var cardList: List<RuiCard>? = null

    fun setData(cardList: List<RuiCard>) {
        this.cardList = cardList
    }

    override fun getCount(): Int {
        return cardList?.size ?: 0
    }

    override fun getItem(position: Int): RuiCard? {
        return if (cardList != null) cardList!![position] else null
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view = convertView
        var viewHolder: ViewHolder? = null
        val card = getItem(position)
        if (view == null) {
            viewHolder = ViewHolder()
            view = inflater.inflate(R.layout.list_item_card, null)
            viewHolder.tv_meiPiao = view
                    .findViewById(R.id.tv_card_price) as TextView
            viewHolder.tv_price = view
                    .findViewById(R.id.bt_card_price) as Button
            view.tag = viewHolder
        } else {
            viewHolder = view.tag as ViewHolder
        }
        if (card != null) {
            viewHolder.tv_meiPiao!!.text = "${card.ruiMoney}"
            viewHolder.tv_price!!.text = "￥ ${card.realPrice}"
        }
        return view!!
    }

    internal inner class ViewHolder {
        var tv_meiPiao: TextView? = null
        var tv_price: Button? = null
    }

}