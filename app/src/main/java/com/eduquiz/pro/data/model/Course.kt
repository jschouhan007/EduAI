// FILE: app/src/main/java/com/eduquiz/pro/data/model/Course.kt
package com.eduquiz.pro.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "courses")
data class Course(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val description: String,
    val icon: String
)
