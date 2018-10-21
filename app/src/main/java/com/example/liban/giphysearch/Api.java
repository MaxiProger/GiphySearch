package com.example.liban.giphysearch;

import com.example.liban.giphysearch.mvp.model.ListData;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by liban on 15.08.2018.
 */

public interface Api {

    /**
     * Returns list data
     *
     * @param query query for searching
     * @param apiKey api key
     */
    @GET("search")
    Observable<ListData> search(@Query("q") String query, @Query("api_key") String apiKey, @Query("offset") int offset);

    @GET("trending")
    Observable<ListData> getTrending(@Query("api_key") String apiKey, @Query("offset") int offset);
}
