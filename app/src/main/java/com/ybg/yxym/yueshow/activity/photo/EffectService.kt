package com.ybg.yxym.yueshow.activity.photo


import com.ybg.yxym.yueshow.gpuimage.util.GPUImageFilterTools

import java.util.ArrayList

class EffectService private constructor() {

    val localFilters: List<FilterEffect>
        get() {
            val filters = ArrayList<FilterEffect>()
            filters.add(FilterEffect("原始", GPUImageFilterTools.FilterType.NORMAL, 0))
            filters.add(FilterEffect("暧昧", GPUImageFilterTools.FilterType.ACV_AIMEI, 0))
            filters.add(FilterEffect("淡蓝", GPUImageFilterTools.FilterType.ACV_DANLAN, 0))
            filters.add(FilterEffect("蛋黄", GPUImageFilterTools.FilterType.ACV_DANHUANG, 0))
            filters.add(FilterEffect("复古", GPUImageFilterTools.FilterType.ACV_FUGU, 0))
            filters.add(FilterEffect("高冷", GPUImageFilterTools.FilterType.ACV_GAOLENG, 0))
            filters.add(FilterEffect("怀旧", GPUImageFilterTools.FilterType.ACV_HUAIJIU, 0))
            filters.add(FilterEffect("胶片", GPUImageFilterTools.FilterType.ACV_JIAOPIAN, 0))
            filters.add(FilterEffect("可爱", GPUImageFilterTools.FilterType.ACV_KEAI, 0))
            filters.add(FilterEffect("落寞", GPUImageFilterTools.FilterType.ACV_LOMO, 0))
            filters.add(FilterEffect("加强", GPUImageFilterTools.FilterType.ACV_MORENJIAQIANG, 0))
            filters.add(FilterEffect("暖心", GPUImageFilterTools.FilterType.ACV_NUANXIN, 0))
            filters.add(FilterEffect("清新", GPUImageFilterTools.FilterType.ACV_QINGXIN, 0))
            filters.add(FilterEffect("日系", GPUImageFilterTools.FilterType.ACV_RIXI, 0))
            filters.add(FilterEffect("温暖", GPUImageFilterTools.FilterType.ACV_WENNUAN, 0))
            return filters
        }

    companion object {

        private var mInstance: EffectService? = null

        val inst: EffectService
            get() {
                if (mInstance == null) {
                    synchronized(EffectService::class.java) {
                        if (mInstance == null)
                            mInstance = EffectService()
                    }
                }
                return mInstance!!
            }
    }

}
