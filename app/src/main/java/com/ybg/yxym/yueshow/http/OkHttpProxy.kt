package com.ybg.yxym.yueshow.http

import android.content.Context

import com.ybg.yxym.yueshow.http.body.BodyWrapper
import com.ybg.yxym.yueshow.http.builder.GetRequestBuilder
import com.ybg.yxym.yueshow.http.builder.PostRequestBuilder
import com.ybg.yxym.yueshow.http.builder.UploadRequestBuilder
import com.ybg.yxym.yueshow.http.callback.OkCallback
import com.ybg.yxym.yueshow.http.listener.DownloadListener

import okhttp3.Call
import okhttp3.Dispatcher
import okhttp3.OkHttpClient
import okhttp3.Request

/**
 * Created by zhaokaiqiang on 15/11/22.
 */
object OkHttpProxy {

    private var mHttpClient: OkHttpClient? = null

    private fun init(): OkHttpClient {
        synchronized(OkHttpProxy::class.java) {
            if (mHttpClient == null) {
                mHttpClient = OkHttpClient()
            }
        }
        return mHttpClient!!
    }

    var instance: OkHttpClient
        get() = if (mHttpClient == null) init() else mHttpClient!!
        set(okHttpClient) {
            OkHttpProxy.mHttpClient = okHttpClient
        }

    fun get(): GetRequestBuilder {
        return GetRequestBuilder()
    }

    fun post(): PostRequestBuilder {
        return PostRequestBuilder()
    }

    /**
     * @param url      请求url
     * *
     * @param param    请求参数
     * *
     * @param tag      请求的tag(对应的Context,方便在结束时取消)
     * *
     * @param callback 请求的回调
     */
    fun postJson(url: String, tag: Context, param: String, callback: OkCallback<*>) {
        PostRequestBuilder().url(url).tag(tag).enqueueJson(param, callback)
    }

    operator fun get(url: String, tag: Context, params: Map<String, Any>, callback: OkCallback<*>) {
        GetRequestBuilder().url(url).tag(tag).addParams(params).enqueue(callback)
    }

    fun post(url: String, tag: Context, params: Map<String, Any>, callback: OkCallback<*>) {
        PostRequestBuilder().url(url).tag(tag).addParams(params).enqueue(callback)
    }

    fun download(url: String, downloadListener: DownloadListener): Call {
        val request = Request.Builder().url(url).build()
        val call = BodyWrapper.addProgressResponseListener(instance, downloadListener).newCall(request)
        call.enqueue(downloadListener)
        return call
    }

    /**
     * default time out is 30 min
     */
    fun upload(): UploadRequestBuilder {
        return UploadRequestBuilder()
    }

    fun cancel(tag: Any) {
        val dispatcher = instance.dispatcher()
        for (call in dispatcher.queuedCalls()) {
            if (tag == call.request().tag()) {
                call.cancel()
            }
        }
        for (call in dispatcher.runningCalls()) {
            if (tag == call.request().tag()) {
                call.cancel()
            }
        }
    }


}
