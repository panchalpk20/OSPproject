package com.anngrynerds.ospproject.WeatherClasses;


import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface weatherAPI {
    @GET("weather")
    Call<SupportClass> getweather(@Query("lat")String lat,
                                  @Query("lon")String lang
            ,@Query("appid")String ApiKey);
}
