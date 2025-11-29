package com.uk.ac.tees.mad.habitloop.domain.models

import com.uk.ac.tees.mad.habitloop.data.local.UserEntity
import kotlinx.serialization.Serializable

// Domain model
data class User(
    val uid: String,
    val name: String,
    val email: String,
    val profileImageUrl: String? = null
)

// DTO for Firestore
@Serializable
data class UserDto(
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val profileImageUrl: String? = null
) {
    fun toDomain() = User(uid, name, email, profileImageUrl)
}

// Mappers
fun User.toEntity() = UserEntity(uid, name, email, profileImageUrl)
fun UserEntity.toDomain() = User(uid, name, email, profileImageUrl)
