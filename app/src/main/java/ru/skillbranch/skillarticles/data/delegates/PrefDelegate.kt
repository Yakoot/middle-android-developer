package ru.skillbranch.skillarticles.data.delegates

import ru.skillbranch.skillarticles.data.local.PrefManager
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class PrefDelegate<T>(private val defaultValue: T) {
    private var storedValue: T? = null

    operator fun provideDelegate(
        thisRef: PrefManager,
        prop: KProperty<*>
    ): ReadWriteProperty<PrefManager, T?> {
        val key = prop.name
        return object : ReadWriteProperty<PrefManager, T?> {
            @Suppress("UNCHECKED_CAST")
            override fun getValue(thisRef: PrefManager, property: KProperty<*>): T? {
                if (storedValue == null) {
                    storedValue = when(defaultValue) {
                        is Int -> thisRef.preferences.getInt(key, defaultValue as Int) as T
                        is Long -> thisRef.preferences.getLong(key, defaultValue as Long) as T
                        is Float -> thisRef.preferences.getFloat(key, defaultValue as Float) as T
                        is String -> thisRef.preferences.getString(key, defaultValue as String) as T
                        is Boolean -> thisRef.preferences.getBoolean(key, defaultValue as Boolean) as T
                        else -> error("Incorrect type")
                    }
                }
                return storedValue
            }

            override fun setValue(thisRef: PrefManager, property: KProperty<*>, value: T?) {
                with(thisRef.preferences.edit()) {
                    when (value) {
                        is Int -> putInt(key, value)
                        is Long -> putLong(key, value)
                        is Float -> putFloat(key, value)
                        is String -> putString(key, value)
                        is Boolean -> putBoolean(key, value)
                        else -> error("Incorrect type")
                    }
                    apply()
                }
                storedValue = value
            }

        }
    }

}