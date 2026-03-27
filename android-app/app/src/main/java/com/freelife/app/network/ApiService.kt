package com.freelife.app.network

import com.freelife.app.model.AuthResponse
import com.freelife.app.model.CreateGroupRequest
import com.freelife.app.model.GroupResponse
import com.freelife.app.model.JoinGroupRequest
import com.freelife.app.model.LocationResponse
import com.freelife.app.model.LoginRequest
import com.freelife.app.model.MemberResponse
import com.freelife.app.model.RegisterRequest
import com.freelife.app.model.UpdateLocationRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {
    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    @GET("groups")
    suspend fun getGroups(@Header("Authorization") token: String): Response<List<GroupResponse>>

    @POST("groups")
    suspend fun createGroup(
        @Header("Authorization") token: String,
        @Body request: CreateGroupRequest
    ): Response<GroupResponse>

    @POST("groups/join")
    suspend fun joinGroup(
        @Header("Authorization") token: String,
        @Body request: JoinGroupRequest
    ): Response<Unit>

    @GET("groups/{id}/members")
    suspend fun getGroupMembers(
        @Header("Authorization") token: String,
        @Path("id") groupId: Int
    ): Response<List<MemberResponse>>

    @DELETE("groups/{id}/leave")
    suspend fun leaveGroup(
        @Header("Authorization") token: String,
        @Path("id") groupId: Int
    ): Response<Unit>

    @POST("location")
    suspend fun updateLocation(
        @Header("Authorization") token: String,
        @Body request: UpdateLocationRequest
    ): Response<Unit>

    @GET("location/{userId}")
    suspend fun getLatestLocation(
        @Header("Authorization") token: String,
        @Path("userId") userId: Int
    ): Response<LocationResponse>
}
