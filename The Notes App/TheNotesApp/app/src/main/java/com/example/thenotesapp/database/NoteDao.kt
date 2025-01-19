package com.example.thenotesapp.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.thenotesapp.model.Note

@Dao
interface NoteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: Note)

    @Update
    suspend fun updateNote(note: Note)

    @Delete
    suspend fun deleteNote(note: Note)

    @Query("SELECT * FROM notes ORDER BY id DESC")
    fun getAllNotes(): LiveData<List<Note>>

    @Query("SELECT * FROM notes WHERE noteTitle LIKE :query OR noteDesc LIKE :query")
    fun searchNote(query: String?): LiveData<List<Note>>

    @Query("SELECT * FROM notes WHERE isFavorite = 1 ORDER BY id DESC")
    fun getFavoriteNotes(): LiveData<List<Note>>
}
