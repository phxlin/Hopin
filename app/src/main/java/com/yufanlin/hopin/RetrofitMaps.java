package com.yufanlin.hopin;

import com.yufanlin.hopin.POJO.Example;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Created by Yufan on 2/18/17.
 */

public interface RetrofitMaps {


    /*
     * Retrofit get annotation with our URL
     * And our method that will return us details of student.
     */
    @GET("api/directions/json?key=AIzaSyC22GfkHu9FdgT9SwdCWMwKX1a4aohGifM")
    Call<Example> getDistanceDuration(@Query("units") String units, @Query("origin") String origin, @Query("destination") String destination, @Query("mode") String mode);

}
