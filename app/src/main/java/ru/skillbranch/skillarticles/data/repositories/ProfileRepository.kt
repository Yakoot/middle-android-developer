package ru.skillbranch.skillarticles.data.repositories

import androidx.lifecycle.LiveData
import okhttp3.MultipartBody
import ru.skillbranch.skillarticles.data.local.PrefManager
import ru.skillbranch.skillarticles.data.models.User
import ru.skillbranch.skillarticles.data.remote.RestService
import ru.skillbranch.skillarticles.data.remote.req.EditProfileReq
import javax.inject.Inject

interface IProfileRepository : IRepository {
    fun getProfile(): LiveData<User?>
    suspend fun uploadAvatar(body: MultipartBody.Part)
    suspend fun removeAvatar()
    suspend fun editProfile(name: String, about: String)
}

class ProfileRepository @Inject constructor(private val preferences: PrefManager, private val network: RestService): IProfileRepository{

    override fun getProfile(): LiveData<User?> = preferences.profileLive

    override suspend fun uploadAvatar(body: MultipartBody.Part) {
        val (url) = network.upload(body, preferences.accessToken)
        preferences.replaceAvatarUrl(url)
    }

    override suspend fun removeAvatar() {
        network.removeAvatar(preferences.accessToken)
        preferences.replaceAvatarUrl("")
    }

    override suspend fun editProfile(name: String, about: String) {
        val user = network.editProfile(EditProfileReq(name, about), preferences.accessToken)
        preferences.profile = user    }
}