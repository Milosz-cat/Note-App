package com.example.thenotesapp.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.thenotesapp.database.Converters
import kotlinx.parcelize.Parcelize

@Entity(tableName = "notes")
@Parcelize
data class Note(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val noteTitle: String,
    val noteDesc: String,
    val isSecured: Boolean = false,
    val isFavorite: Boolean = false,
    val date: String,
    @TypeConverters(Converters::class)
    val imageUris: List<String> = emptyList()
) : Parcelable
