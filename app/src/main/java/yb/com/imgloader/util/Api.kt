package yb.com.imgloader.util

import com.google.gson.JsonObject
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Single
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*


interface Api {
    @GET
    fun downloadFile(@Url fileUrl : String) : Call<ResponseBody>

//    @GET
//    fun downloadFile(@Url fileUrl : String) : Flowable<ResponseBody>

}





