package ru.skillbranch.kotlinexample

import androidx.annotation.VisibleForTesting

object UserHolder {
    private val map = mutableMapOf<String, User>()
    fun registerUser(
        fullName: String,
        email: String,
        password: String
    ):User {
        User.makeUser(fullName, email = email, password = password)
            .run {
                require(!map.containsKey(login)) { "A user with this email already exists" }
                map[login] = this
                return this
            }
    }

    fun registerUserByPhone(
        fullName: String,
        rawPhone: String
    ): User {
        User.makeUser(fullName, phone = rawPhone)
            .run {
                require(!map.containsKey(login)) { "A user with this phone already exists" }
                map[login] = this
                return this
            }
    }

    fun loginUser(login: String, password: String): String? {
        return map[login.trim()]?.run {
            if (checkPassword(password)) userInfo
            else null
        }
    }

    @VisibleForTesting(otherwise = VisibleForTesting.NONE)
    fun clearHolder(){
        map.clear()
    }
}