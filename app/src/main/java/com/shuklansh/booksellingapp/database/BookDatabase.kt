package com.shuklansh.booksellingapp.database

import androidx.room.Database
import androidx.room.RoomDatabase
//abstract class BookDatabase of type RoomDatabase()

@Database (entities = [BookEntity::class], version = 1) // to tell the compiler that this class will be used as DB class for this app. also wrote the version of db
abstract class BookDatabase : RoomDatabase() {

    abstract fun bookDao():BookDao

}