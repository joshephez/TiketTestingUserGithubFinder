package com.gudangada.tikettesting.apiconfig

import android.os.SystemClock
import com.gudangada.tikettesting.Url
import com.gudangada.tikettesting.response.Response
import io.reactivex.Observable
import okhttp3.Dispatcher
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface UserAPI {

    @GET("search/users")
    @Headers("Content-Type: application/json")
    fun search(@Query("q") q: String, @Query("page") page: Int, @Query("per_page") perPage: Int): Observable<Response>

    companion object {
        fun create(): UserAPI {

            val builder = OkHttpClient.Builder()
            val dispatcher = Dispatcher()

            dispatcher.maxRequests = 1

            val interceptor = Interceptor { chain ->
                SystemClock.sleep(2550)
                chain.proceed(chain.request())
            }

            builder.addNetworkInterceptor(interceptor)
            builder.dispatcher(dispatcher)

            var client = builder.build()

            val retrofit = Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(Url.API)
                .client(client)
                .build()

            return retrofit.create(UserAPI::class.java)
        }
    }

}