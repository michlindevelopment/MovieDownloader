package com.michlindev.moviedownloader.api;


import com.michlindev.moviedownloader.data.Constants;
import com.michlindev.moviedownloader.data.MoviesResponse;

import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiService {

    @GET(Constants.JSON_FILE)
    Single<MoviesResponse> getAllMovies();

    @GET(Constants.JSON_FILE)
    Single<MoviesResponse> getWithParameters(
            @Query("minimum_rating") int rating,
            @Query("limit") int limit,
            @Query("page") int page,
            @Query("query_term") String query);
}
