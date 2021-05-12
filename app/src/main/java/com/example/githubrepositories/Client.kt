package com.example.githubrepositories

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

// Clientを作成
val httpBuilder: OkHttpClient.Builder get() {
    val httpClient = OkHttpClient.Builder()
        httpClient.addInterceptor(Interceptor { chain ->
            val original = chain.request()
            val request = original.newBuilder()
                    // MIMEタイプでリクエストする
                .header("Accept", "application/json")
                .method(original.method, original.body)
                .build()
            var response = chain.proceed(request)

            return@Interceptor response
        })
            .readTimeout(30,TimeUnit.SECONDS)

    // log
    val loggingInterceptor = HttpLoggingInterceptor()
    loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
    httpClient.addInterceptor(loggingInterceptor)

    return httpClient
}

// 繋ぎ込み
fun createService(): ApiService {
    var client = httpBuilder.build()
    var retrofit = Retrofit.Builder()
        // 基本のurl
        .baseUrl("https://api.github.com/")
        // Moshiの使用
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        // カスタマイズしたokhttpのクライアントの設定
        .client(client)
        .build()

    // interfaceから実装を取得
    var API = retrofit.create(ApiService::class.java)

    return API
}

// Moshiのversion1.9.0以降必要になった
val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()
