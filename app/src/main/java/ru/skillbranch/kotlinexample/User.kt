package ru.skillbranch.kotlinexample

import androidx.annotation.VisibleForTesting
import java.lang.StringBuilder
import java.math.BigInteger
import java.security.MessageDigest
import java.security.SecureRandom

class User private constructor(
    private val firstName: String,
    private val lastName: String?,
    email: String? = null,
    rawPhone: String? = null,
    meta: Map<String, Any>? = null
) {
    val userInfo: String

    private val fullName: String
        get() = listOfNotNull(firstName, lastName)
            .joinToString(" ")
            .capitalize()

    private val initials: String
        get() = listOfNotNull(firstName, lastName)
            .map { it.first().toUpperCase() }
            .joinToString(" ")

    private var phone: String? = null
        set(value) {
            field = value?.replace("[^+\\d]".toRegex(), "")
        }

    private var _login: String? = null

    internal var login: String
        set(value) {
            _login = value?.toLowerCase()
        }
        get() = _login!!



    private val _salt: String by lazy {
        ByteArray(16).also { SecureRandom().nextBytes(it) }.toString()
    }

    private var salt: String = _salt

    private lateinit var passwordHash: String

    @VisibleForTesting(otherwise = VisibleForTesting.NONE)
    var accessCode: String? = null

    //for email
    constructor(
        firstName: String,
        lastName: String?,
        email: String,
        password: String
    ) : this(firstName, lastName, email = email, meta = mapOf("auth" to "password")) {
        println("Secondary email constructor")
        passwordHash = encrypt(password)
    }

    //for phone
    constructor(
        firstName: String,
        lastName: String?,
        rawPhone: String
    ) : this(firstName, lastName, rawPhone = rawPhone, meta = mapOf("auth" to "sms")) {
        println("Secondary phone constructor")
        require(isPhoneValid(rawPhone)) { "Enter a valid phone number starting with a + and containing 11 digits" }
        requestAccessCode()

    }

    // for csv
    constructor(
        firstName: String,
        lastName: String?,
        rawPhone: String?,
        email: String?,
        saltHash: String
    ) : this(firstName, lastName, rawPhone = rawPhone, email = email, meta = mapOf("src" to "csv")) {
        println("Secondary csv constructor")
        val (newSalt, newHash) = saltHash.split(":")
        passwordHash = newHash
        salt = newSalt
    }

    init {
        println("First init block, primary constructor was called")

        check(!firstName.isBlank()) { "First name must be not blank" }
        check(email.isNullOrBlank() || rawPhone.isNullOrBlank()) { "Email or phone must be not blank" }

        phone = if (rawPhone.isNullOrBlank()) null else rawPhone
        login = if (email.isNullOrEmpty()) phone!! else email
        userInfo = """
            firstName: $firstName
            lastName: $lastName
            login: $login
            fullName: $fullName
            initials: $initials
            email: ${if (email.isNullOrEmpty()) null else email}
            phone: $phone
            meta: $meta
        """.trimIndent()
    }

    fun checkPassword(pass: String): Boolean {
        val a = 1
        println("${encrypt(pass)} $passwordHash")
        return encrypt(pass) == passwordHash
    }
    fun changePassword(oldPass: String, newPass: String) {
        if (checkPassword(oldPass)) passwordHash = encrypt(newPass)
        else throw IllegalArgumentException("The entered password does not match the current password")
    }

    private fun isPhoneValid(phone: String): Boolean {
        return Regex("""\+(\d[^\w]*){11}""").matches(phone)
    }

    private fun encrypt(password: String): String = salt.plus(password).md5()

    private fun generateAccessCode(): String {
        val possible = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
        return StringBuilder().apply {
            repeat(6) {
                (possible.indices).random().also { index ->
                    append(possible[index])
                }
            }
        }.toString()
    }

    private fun sendAccessCodeToUser(phone: String?, code: String) {
        println("....  sending access code: $code on $phone")
    }

    fun requestAccessCode() {
        val code = generateAccessCode()
        passwordHash = encrypt(code)
        accessCode = code
        sendAccessCodeToUser(phone, code)
    }

    private fun String.md5(): String {
        val md = MessageDigest.getInstance("MD5")
        val digest = md.digest(toByteArray())
        val hexString = BigInteger(1, digest).toString(16)
        return hexString.padStart(32, '0')
    }

    companion object Factory {
        fun makeUser(
            fullName: String,
            email: String? = null,
            password: String? = null,
            phone: String? = null,
            saltHash: String? = null
        ): User {
            val (firstName, lastName) = fullName.fullNameToPair()
            return when {
                !saltHash.isNullOrBlank() -> User(firstName, lastName, phone, email, saltHash)
                !phone.isNullOrBlank() -> User(firstName, lastName, phone)
                !email.isNullOrBlank() && !password.isNullOrBlank() -> User(
                    firstName,
                    lastName,
                    email,
                    password
                )
                else -> throw IllegalArgumentException("Email or phone must be not null or blank")
            }
        }

        private fun String.fullNameToPair(): Pair<String, String?> {
            return this.split(" ")
                .filter { it.isNotBlank() }
                .run {
                    when (size) {
                        1 -> first() to null
                        2 -> first() to last()
                        else -> throw IllegalArgumentException(
                            "Fullname must contain only first name " +
                                    "and last name, current split result: ${this@fullNameToPair}"
                        )
                    }
                }
        }

    }

}
