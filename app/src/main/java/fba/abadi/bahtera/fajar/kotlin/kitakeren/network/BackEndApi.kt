package fba.abadi.bahtera.fajar.kotlin.kitakeren.network

import fba.abadi.bahtera.fajar.kotlin.kitakeren.model.*
import io.reactivex.Observable
import retrofit2.http.*

interface BackEndApi {
    @FormUrlEncoded
    @POST("home/index")
    fun dash(@Field("code") code: String): Observable<MutableList<dashboard>>

    @GET("home/berita")
    fun news(): Observable<MutableList<berita>>

    @FormUrlEncoded
    @POST("home/detailD")
    fun Ddash(@Field("id_dashboard") id_dashboard: String): Observable<MutableList<DDashboard>>

    @FormUrlEncoded
    @POST("home/deskripsi")
    fun Des(@Field("id_detailDashboard") id_detailDashboard: String): Observable<MutableList<Deskripsi>>

    @FormUrlEncoded
    @POST("home/read_detail_deskripsi")
    fun Ddes(@Field("id_dashboard") id_dashboard: String): Observable<MutableList<DDeskripsi>>

    @FormUrlEncoded
    @POST("home/read_rating")
    fun Rating1(@Field("mode") mode: Int, @Field("id_dashboard") id_dashboard: String): Observable<MutableList<rating>>

    @FormUrlEncoded
    @POST("home/write_rating")
    fun WRating(
        @Field("id_dashboard") id_dashboard: String,
        @Field("email") email: String,
        @Field("rating") rating: String,
        @Field("comment") comment: String
    ): Observable<status>

    @FormUrlEncoded
    @POST("home/login")
    fun Login(@Field("username") username: String, @Field("email") email: String): Observable<status>

    @FormUrlEncoded
    @POST("home/register")
    fun Register(
        @Field("nickname") nickname: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): Observable<status>
}
