package yb.com.imgloader.util

import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import yb.com.imgloader.ImageConfig

object NetworkUtil {
    fun getRetrofit(): Api {
        var retrofit = Retrofit.Builder()
            .baseUrl(ImageConfig.BASE_URL)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(Api::class.java)
    }

}