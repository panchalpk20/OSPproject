package com.anngrynerds.ospproject.WeatherClasses;


import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SupportClass {
    @SerializedName("main")
    Main main;
    @SerializedName("wind")
    Wind wind;
    @SerializedName("sys")
    Sys sys;
    @SerializedName("weather")
    private List<Weather> weather;

    @SerializedName("name")
    String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Main getMain() {
        return main;
    }

    public void setMain(Main main) {
        this.main = main;
    }

    public Wind getWind() {
        return wind;
    }

    public void setWind(Wind wind) {
        this.wind = wind;
    }

    public Sys getSys() {
        return sys;
    }

    public void setSys(Sys sys) {
        this.sys = sys;
    }

    public List<Weather> getWeather() {
        return weather;
    }

    public void setWeather(List<Weather> weather) {
        this.weather = weather;
    }


}

