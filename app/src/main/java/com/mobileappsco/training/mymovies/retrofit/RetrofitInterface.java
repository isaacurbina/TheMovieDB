package com.mobileappsco.training.mymovies.retrofit;

import com.mobileappsco.training.mymovies.entities.PageResults;
import com.mobileappsco.training.mymovies.entities.PageReviews;
import com.mobileappsco.training.mymovies.entities.PageVideos;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface RetrofitInterface {

    @GET("discover/movie")
    Call<PageResults> mostPopularMovies(
            @Query("api_key") String apikey,
            @Query("sort_by") String sort_by,
            @Query("page") String page,
            @Query("language") String language
    );

    @GET("discover/movie")
    Call<PageResults> upcomingMovies(
            @Query("api_key") String apikey,
            @Query("primary_release_date.gte") String today,
            @Query("page") String page,
            @Query("language") String language
    );

    @GET("search/movie")
    Call<PageResults> searchMoviesByTitle(
            @Query("api_key") String apikey,
            @Query("query") String title,
            @Query("sort_by") String sort_by,
            @Query("page") String page,
            @Query("language") String language
    );

    @GET("discover/movie")
    Call<PageResults> searchMoviesByYear(
            @Query("api_key") String apikey,
            @Query("primary_release_year") String year,
            @Query("sort_by") String sort_by,
            @Query("page") String page,
            @Query("language") String language
    );

    @GET("search/movie")
    Call<PageResults> searchMoviesByTitleAndYear(
            @Query("api_key") String apikey,
            @Query("query") String title,
            @Query("primary_release_year") String year,
            @Query("sort_by") String sort_by,
            @Query("page") String page,
            @Query("language") String language
    );

    @GET("movie/{id}/videos")
    Call<PageVideos> fetchMovieTrailers(
            @Path("id") String id,
            @Query("api_key") String apikey,
            @Query("language") String language
    );

    @GET("movie/{id}/reviews")
    Call<PageReviews> fetchMovieReviews(
            @Path("id") String id,
            @Query("api_key") String apikey,
            @Query("language") String language
    );

}