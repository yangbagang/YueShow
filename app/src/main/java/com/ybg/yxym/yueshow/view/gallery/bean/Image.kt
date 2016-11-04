package com.ybg.yxym.yueshow.view.gallery.bean

class Image(var path: String, var name: String, var time: Long) {

    override fun equals(o: Any?): Boolean {
        try {
            val other = o as Image?
            return this.path.equals(other!!.path, ignoreCase = true)
        } catch (e: ClassCastException) {
            e.printStackTrace()
        }

        return super.equals(o)
    }
}
