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
                val normalizedPhone = normalizePhone(login)
                require(!map.containsKey(normalizedPhone)) { "A user with this phone already exists" }
                map[normalizedPhone] = this
                return this
            }
    }

    fun loginUser(login: String, password: String): String? {
        return (map[login.trim()]?:map[normalizePhone(login)])?.run {
            if (checkPassword(password)) userInfo
            else null
        }
    }

    @VisibleForTesting(otherwise = VisibleForTesting.NONE)
    fun clearHolder(){
        map.clear()
    }

    fun requestAccessCode(login: String): Unit? {
        return map[normalizePhone(login)]?.run {
            requestAccessCode()
        }
    }

    private fun normalizePhone(phone: String): String {
        return phone.replace("[^\\d]".toRegex(), "")
    }

    fun importUsers(list: List<String>): List<User> {
        val userList = mutableListOf<User>()
        list.forEach {
            val partList: List<String> = it.split(";")
            val (fullname, email, saltHash, phone) = partList
            println("$fullname, $email, $saltHash, $phone")
            User.makeUser(fullname, email, phone = phone, saltHash = saltHash)
                .run {
                    val normalizedPhone = normalizePhone(login)
                    require(!map.containsKey(normalizedPhone)) { "A user with this phone already exists" }
                    map[normalizedPhone] = this
                    return this
                }


        }
        return listOf()
    }
}