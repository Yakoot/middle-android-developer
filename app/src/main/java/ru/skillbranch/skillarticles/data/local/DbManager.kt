package ru.skillbranch.skillarticles.data.local

import androidx.room.Room
import ru.skillbranch.skillarticles.App

object DbManager {
    val db = Room.databaseBuilder(
        App.applicationContext(),
        AppDb::class.java,
        AppDb.DATABASE_NAME
    ).build()
}