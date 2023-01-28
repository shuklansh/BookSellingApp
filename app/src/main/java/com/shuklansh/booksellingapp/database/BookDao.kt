package com.shuklansh.booksellingapp.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface BookDao {
    //no need to write SQL code for insert and delete as Room library takes care of it
    @Insert
    fun insertBook (bookEntity: BookEntity)

    @Delete
    fun deleteBook (bookEntity: BookEntity)

    //to return all books in favourites <return type is BookEntity>
    //writing SQL Query using @Query
    @Query("SELECT * FROM books") //name of entity was given in BookEntity class
    fun getAllBooks() : List<BookEntity>

    @Query("SELECT * FROM books WHERE book_id = :bookId")
    fun getBookById( bookId:String):BookEntity

}