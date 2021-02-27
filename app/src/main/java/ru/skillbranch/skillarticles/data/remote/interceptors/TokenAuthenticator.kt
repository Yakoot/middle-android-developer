package ru.skillbranch.skillarticles.data.remote.interceptors

import dagger.Lazy
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import ru.skillbranch.skillarticles.data.local.PrefManager
import ru.skillbranch.skillarticles.data.remote.RestService
import ru.skillbranch.skillarticles.data.remote.req.RefreshReq

class TokenAuthenticator(val prefs: PrefManager, val lazyApi: Lazy<RestService>): Authenticator {
    override fun authenticate(route: Route?, response: Response): Request? {
        if (response.code == 401) {
            val refreshRes = lazyApi.get().refreshToken(RefreshReq(prefs.refreshToken)).execute()
            val resBody = refreshRes.body()
            return if (refreshRes.isSuccessful) {
                prefs.accessToken = "Bearer ${resBody!!.accessToken}"
                prefs.refreshToken = resBody.refreshToken
                response.request.newBuilder()
                    .header("Authorization", "Bearer ${resBody.accessToken} ")
                    .build()
            } else {
                null
            }
        }
        return null
    }
}