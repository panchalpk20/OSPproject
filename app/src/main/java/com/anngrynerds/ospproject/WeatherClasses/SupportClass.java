package com.anngrynerds.ospproject.WeatherClasses;


import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SupportClass {
    public Main getMain() {
        return main;
    }

    public void setWind(Wind wind) {
        this.wind = wind;
    }

    public Wind getWind() {
        return wind;
    }
    public void setMain(Main main) {
        this.main = main;
    }

    @SerializedName("main")
    Main main;
    @SerializedName("wind")
    Wind wind;

    public Sys getSys() {
        return sys;
    }

    public void setSys(Sys sys) {
        this.sys = sys;
    }


    @SerializedName("sys")
    Sys sys;
    @SerializedName("weather")
    private List<Weather> weather;

    public List<Weather> getWeather() {
        return weather;
    }

    public void setWeather(List<Weather> weather) {
        this.weather = weather;
    }





}

