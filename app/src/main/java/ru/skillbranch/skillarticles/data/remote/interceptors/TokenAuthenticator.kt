package ru.skillbranch.skillarticles.data.remote.interceptors

import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import ru.skillbranch.skillarticles.data.local.PrefManager
import ru.skillbranch.skillarticles.data.remote.NetworkManager
import ru.skillbranch.skillarticles.data.remote.req.RefreshReq

class TokenAuthenticator: Authenticator {
    override fun authenticate(route: Route?, response: Response): Request? {
        if (response.code == 401) {
            val refreshRes = NetworkManager.api.refreshToken(RefreshReq(PrefManager.refreshToken)).execute()
            val resBody = refreshRes.body()
            return if (refreshRes.isSuccessful) {
                PrefManager.accessToken = "Bearer ${resBody!!.accessToken}"
                PrefManager.refreshToken = resBody.refreshToken
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