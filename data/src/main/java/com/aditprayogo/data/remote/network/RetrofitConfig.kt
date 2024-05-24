package com.aditprayogo.data.remote.network

import android.content.Context
import com.aditprayogo.data.BuildConfig
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Created by Aditiya Prayogo.
 */
object RetrofitConfig {

    fun retrofitClient(context: Context, url: String = BuildConfig.BASE_URL): Retrofit {
        return Retrofit.Builder()
            .baseUrl(url)
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient(context))
            .build()
    }

    private fun okHttpClient(context: Context): OkHttpClient {
        val chuckerInterceptor = ChuckerInterceptor.Builder(context).build()

        return OkHttpClient.Builder()
            .addInterceptor { chain ->
                var original = chain.request()

                val url = original.url
                    .newBuilder()
                    .addQueryParameter("key", BuildConfig.API_KEY)
                    .build()

                original =
                    original.newBuilder().url(url).addHeader("Accept", "application/json").build()
                chain.proceed(original)
            }
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            .addInterceptor(chuckerInterceptor) // Add Chucker Interceptor
            .connectTimeout(120, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS)
            .build()
    }


}