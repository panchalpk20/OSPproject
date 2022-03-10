package com.anngrynerds.ospproject.WeatherClasses;


import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface weatherAPI {
    @GET("weather")
    Call<SupportClass> getweather(@Query("q")String cityName,@Query("appid")String ApiKey);
}
