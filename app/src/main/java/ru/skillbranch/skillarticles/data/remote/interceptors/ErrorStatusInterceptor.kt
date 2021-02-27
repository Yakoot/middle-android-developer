package ru.skillbranch.skillarticles.data.remote.interceptors

import com.squareup.moshi.JsonEncodingException
import com.squareup.moshi.Moshi
import okhttp3.Interceptor
import okhttp3.Response
import ru.skillbranch.skillarticles.data.remote.err.ApiError.*
import ru.skillbranch.skillarticles.data.remote.err.ErrorBody

class ErrorStatusInterceptor(val moshi: Moshi) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val res = chain.proceed(chain.request())

        if (res.isSuccessful) return res

        val errMessage = try {
            moshi.adapter(ErrorBody::class.java).fromJson(res.body!!.string())?.message
        } catch (e: JsonEncodingException) {
            e.message
        }

        when(res.code) {
            400 -> throw BadRequest(errMessage)
            401 -> throw Unauthorized(errMessage)
            403 -> throw Forbidden(errMessage)
            404 -> throw NotFound(errMessage)
            500 -> throw InternalServerError(errMessage)
            else -> throw UnknownError(errMessage)
        }
    }
}