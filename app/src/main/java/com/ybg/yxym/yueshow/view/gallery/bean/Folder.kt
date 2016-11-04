package com.ybg.yxym.yueshow.view.gallery.bean

class Folder {
    var name: String? = null
    var path: String? = null
    var cover: Image? = null
    var images: MutableList<Image>? = null

    override fun equals(o: Any?): Boolean {
        try {
            val other = o as Folder?
            return this.path!!.equals(other!!.path!!, ignoreCase = true)
        } catch (e: ClassCastException) {
            e.printStackTrace()
        }

        return super.equals(o)
    }
}
