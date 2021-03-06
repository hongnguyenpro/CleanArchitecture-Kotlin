package com.hieupham.data.source.remote.api.middleware

import android.content.Context
import okhttp3.Interceptor
import okhttp3.Response

/**
 * Created by hieupham on 6/26/18.
 */
abstract class Interceptor(protected val context: Context) : Interceptor {

    override fun intercept(chain: Interceptor.Chain?): Response? {
        return chain?.proceed(chain.request())
    }
}