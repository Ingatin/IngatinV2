package id.co.brainy.data.network.retrofit

import id.co.brainy.data.model.LoginReq
import id.co.brainy.data.model.RegisterReq
import id.co.brainy.data.model.TaskReq
import id.co.brainy.data.network.response.DeleteResponse
import id.co.brainy.data.network.response.LoginResponse
import id.co.brainy.data.network.response.RegistResponse
import id.co.brainy.data.network.response.TaskResponse
import id.co.brainy.data.network.response.TasksItem
import id.co.brainy.data.network.response.UserResponse
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiService {


//    Auth
    @POST("auth/reg")
    suspend fun register(
        @Body request: RegisterReq
    ): RegistResponse

    @POST("auth/login")
    suspend fun login(
        @Body request: LoginReq
    ): LoginResponse

    @GET("auth/{userId}")
    suspend fun getUser(
        @Header("Authorization") token: String,
        @Path("userId") userId: String
    ): UserResponse

//  Task
    @GET("tasks/user/{userId}")
    suspend fun getAllTasks(
        @Header("Authorization") token: String,
        @Path("userId") userId: String
    ): TaskResponse

    @POST("tasks")
    suspend fun createTask(
        @Header("Authorization") token: String,
        @Body request: TaskReq
    ): TasksItem

    @GET("tasks/id/{taskId}")
    suspend fun getTaskById(
        @Header("Authorization") token: String,
        @Path("taskId") taskId: String
    ): TaskResponse

    @GET("tasks/category/{userId}/{category}")
    suspend fun getTaskByCategory(
        @Header("Authorization") token: String,
        @Path("userId") userId: String,
        @Path("category") category: String
    ): TaskResponse

    @PUT("tasks/id/{taskId}")
    suspend fun editTask(
        @Header("Authorization") token: String,
        @Path("taskId") taskId: String,
        @Body request: TaskReq
    ): TasksItem

    @DELETE("tasks/id/{taskId}")
    suspend fun deleteTask(
        @Header("Authorization") token: String,
        @Path("taskId") taskId: String
    ): DeleteResponse

}