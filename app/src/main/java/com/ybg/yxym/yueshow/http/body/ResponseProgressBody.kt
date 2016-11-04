package com.ybg.yxym.yueshow.http.body

import com.ybg.yxym.yueshow.http.Model.Progress
import com.ybg.yxym.yueshow.http.listener.ProgressListener

import java.io.IOException

import okhttp3.MediaType
import okhttp3.ResponseBody
import okio.Buffer
import okio.BufferedSource
import okio.ForwardingSource
import okio.Okio
import okio.Source

class ResponseProgressBody(private val mResponseBody: ResponseBody, private val mProgressListener: ProgressListener?) : ResponseBody() {
    private var bufferedSource: BufferedSource? = null

    override fun contentType(): MediaType {
        return mResponseBody.contentType()
    }

    override fun contentLength(): Long {
        return mResponseBody.contentLength()
    }

    override fun source(): BufferedSource {
        if (bufferedSource == null) {
            bufferedSource = Okio.buffer(source(mResponseBody.source()))
        }
        return bufferedSource!!
    }

    private fun source(source: Source): Source {

        return object : ForwardingSource(source) {

            internal var totalBytesRead: Long = 0

            @Throws(IOException::class)
            override fun read(sink: Buffer, byteCount: Long): Long {
                val bytesRead = super.read(sink, byteCount)
                totalBytesRead += if (bytesRead != -1L) bytesRead else 0L
                mProgressListener?.onProgress(Progress(totalBytesRead, mResponseBody
                        .contentLength(), bytesRead == -1L))
                return bytesRead
            }
        }
    }
}