package com.example.githubrepositories

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

// Clientを作成
val httpBuilder: OkHttpClient.Builder get() {
    val httpClient = OkHttpClient.Builder()
        httpClient.addInterceptor { chain ->
            val original = chain.request()
            val request = original.newBuilder()
                    // MIMEタイプでリクエストする
                    .header("Accept", "application/json")
                    .method(original.method, original.body)
                    .build()

            return@addInterceptor chain.proceed(request)
        }
                .readTimeout(30,TimeUnit.SECONDS)

    // log
    val loggingInterceptor = HttpLoggingInterceptor()
    loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
    httpClient.addInterceptor(loggingInterceptor)

    return httpClient
}

// 繋ぎ込み
fun createService(): ApiService {
    val client = httpBuilder.build()
    val retrofit = Retrofit.Builder()
        // 基本のurl
        .baseUrl("https://api.github.com/")
        // Moshiの使用
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        // RxJavaの利用
        .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
        // カスタマイズしたokhttpのクライアントの設定
        .client(client)
        .build()

    // interfaceから実装を取得
    return retrofit.create(ApiService::class.java)
}

// Moshiのversion1.9.0以降必要になった
val moshi: Moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()
