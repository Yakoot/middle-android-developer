package ru.skillbranch.skillarticles.data.repositories

import androidx.lifecycle.LiveData
import okhttp3.MultipartBody
import ru.skillbranch.skillarticles.data.local.PrefManager
import ru.skillbranch.skillarticles.data.models.User
import ru.skillbranch.skillarticles.data.remote.NetworkManager
import ru.skillbranch.skillarticles.data.remote.req.EditProfileReq

object ProfileRepository {
    private val prefs = PrefManager
    private val network = NetworkManager.api

    fun getProfile(): LiveData<User?> = prefs.profileLive

    suspend fun uploadAvatar(body: MultipartBody.Part) {
        val (url) = network.upload(body, prefs.accessToken)
        prefs.replaceAvatarUrl(url)
    }

    suspend fun removeAvatar() {
        network.removeAvatar(prefs.accessToken)
        prefs.replaceAvatarUrl("")
    }

    suspend fun editProfile(name: String, about: String) {
        val user = network.editProfile(EditProfileReq(name, about), prefs.accessToken)
        prefs.profile = user    }
}