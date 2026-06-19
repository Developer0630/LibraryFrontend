package com.example.librarymobile.data.api

import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object NetworkModule {
    private const val BASE_URL = "http://10.0.2.2:8080/"

    // Biến để lưu token tạm thời sau khi đăng nhập thành công
    var authToken: String? = null

    // Interceptor tự động thêm Header Authorization: Bearer <Token>
    private val authInterceptor = Interceptor { chain ->
        val originalRequest = chain.request()

        // Nếu không có token, cứ để request đi tự nhiên (Dùng cho API Login/Register)
        if (authToken.isNullOrEmpty()) {
            chain.proceed(originalRequest)
        } else {
            // Nếu có token, đính kèm vào Header
            val newRequest = originalRequest.newBuilder()
                .header("Authorization", "Bearer $authToken")
                .build()
            chain.proceed(newRequest)
        }
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(authInterceptor) // Đăng ký bộ thêm Token tự động ở đây
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val apiService: ApiService = retrofit.create(ApiService::class.java)
    val bookApiService: BookApiService = retrofit.create(BookApiService::class.java)
    val loanApiService: LoanApiService = retrofit.create(LoanApiService::class.java)
    val returnApiService: ReturnApiService = retrofit.create(ReturnApiService::class.java)
    val studentApiService: StudentApiService = retrofit.create(StudentApiService::class.java)
    val adminApiService: AdminApiService = retrofit.create(AdminApiService::class.java)

}