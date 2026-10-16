package com.hrm.mobile.data

import kotlinx.serialization.Serializable

// Mirrors backend DTO records (LoginRequest/Response, EmployeeResponse).
@Serializable
data class LoginRequest(val email: String, val password: String)

@Serializable
data class LoginResponse(
    val accessToken: String,
    val tokenType: String = "Bearer",
    val expiresInSeconds: Long = 0,
    val fullName: String = "",
    val userType: String = "",
)

@Serializable
data class Employee(
    val id: Long,
    val organizationId: Long? = null,
    val employeeCode: String = "",
    val fullName: String = "",
    val email: String? = null,
    val phone: String? = null,
    val position: String? = null,
    val address: String? = null,
    val emergencyContactName: String? = null,
    val emergencyContactPhone: String? = null,
    val emergencyContactRelation: String? = null,
    val employmentType: String? = null,
    val joinedAt: String? = null,
    val active: Boolean = true,
)

@Serializable
data class ErrorBody(val message: String? = null, val error: String? = null)
