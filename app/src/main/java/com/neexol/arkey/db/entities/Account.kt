package com.neexol.arkey.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(
    tableName = "accounts",
    foreignKeys = [ForeignKey(
        entity = Category::class,
        parentColumns = ["id"],
        childColumns = ["category_id"],
        onDelete = ForeignKey.SET_NULL)]
)
data class Account(
    @PrimaryKey(autoGenerate = true) val id: Int? = null,
    val name: String,
    val login: String,
    var password: String,
    val iv: String,
    val site: String,
    val description: String,
    @ColumnInfo(name = "category_id") val categoryId: Int?,
    @ColumnInfo(name = "last_modified") val lastModified: String
): Serializable