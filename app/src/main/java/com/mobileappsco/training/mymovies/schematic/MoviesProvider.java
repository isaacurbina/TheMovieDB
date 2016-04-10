package com.mobileappsco.training.mymovies.schematic;

import android.net.Uri;

import net.simonvt.schematic.annotation.ContentProvider;
import net.simonvt.schematic.annotation.ContentUri;
import net.simonvt.schematic.annotation.InexactContentUri;
import net.simonvt.schematic.annotation.TableEndpoint;

@ContentProvider(authority = MoviesProvider.AUTHORITY,
        database = TheMovieDB.class,
        packageName = MoviesProvider.AUTHORITY)
public final class MoviesProvider {

    public static final String AUTHORITY = "com.mobileappsco.training.mymovies";
    public static final String MOVIES_PATH = "movies";
    public static final String TRAILERS_PATH = "trailers";
    public static final String REVIEWS_PATH = "reviews";

    public static final String[] MOVIES_PROJECTION = new String[]{
            ResultColumns._ID,
            ResultColumns.POSTER_PATH,
            ResultColumns.ADULT,
            ResultColumns.OVERVIEW,
            ResultColumns.RELEASE_DATE,
            ResultColumns.ORIGINAL_TITLE,
            ResultColumns.ORIGINAL_LANGUAGE,
            ResultColumns.TITLE,
            ResultColumns.BACKDROP_PATH,
            ResultColumns.POPULARITY,
            ResultColumns.VOTE_COUNT,
            ResultColumns.VIDEO,
            ResultColumns.VOTE_AVERAGE
    };

    public static final String[] TRAILERS_PROJECTION = new String[]{
            VideoColumns._ID,
            VideoColumns.MID,
            VideoColumns.ISO_639_1,
            VideoColumns.ISO_3166_1,
            VideoColumns.KEY,
            VideoColumns.NAME,
            VideoColumns.SITE,
            VideoColumns.SIZE,
            VideoColumns.TYPE
    };

    public static final String[] REVIEWS_PROJECTION = new String[] {
            ReviewColumns._ID,
            ReviewColumns.MID,
            ReviewColumns.CONTENT,
            ReviewColumns.URL
    };

    @TableEndpoint(table = TheMovieDB.MOVIES)
    public static class Movies {
        // General query, returns a set of records
        // content://com.mobileappsco.training.mymovies/movies
        @ContentUri(
                path = "movies",
                type = "vnd.android.cursor.dir/list",
                defaultSort = ResultColumns.POPULARITY + " ASC")
        public static final Uri MOVIES = Uri.parse("content://" + AUTHORITY + "/movies");
        // Query by ID, returns a single record
        // content://com.mobileappsco.training.mymovies/movies/#
        @InexactContentUri(
                path = "movies/#",
                name = "MOVIE_ID",
                type = "vnd.android.cursor.item/list",
                whereColumn = ResultColumns._ID,
                pathSegment = 1)
        public static Uri withId(long id) {
            return Uri.parse("content://" + AUTHORITY + "/movies/" + id);
        }
        // Query by year, returns a set of records
        @InexactContentUri(
                path = "movies/year/#",
                name = "YEAR",
                type = "vnd.android.cursor.dir/list",
                whereColumn = ResultColumns.RELEASE_DATE,
                pathSegment = 1)
        public static Uri withYear(String year) {
            return Uri.parse("content://" + AUTHORITY + "/movies/year/" + year);
        }
        @InexactContentUri(
                path = "movies/title/*",
                name = "TITLE",
                type = "vnd.android.cursor.dir/list",
                whereColumn = ResultColumns.TITLE,
                pathSegment = 1)
        public static Uri withTitle(String title) {
            return Uri.parse("content://" + AUTHORITY + "/movies/title/" + title);
        }
        // Query by title, returns a set of records
        @InexactContentUri(
                path = "movies/title/*/year/#",
                name = "TITLEYEAR",
                type = "vnd.android.cursor.dir/list",
                whereColumn = {ResultColumns.TITLE, ResultColumns.RELEASE_DATE},
                pathSegment = {1,2})
        public static Uri withTitleAndYear(String title, String year) {
            return Uri.parse("content://" + AUTHORITY + "/movies/title/" + title + "/year/" + year);
        }
    }

    @TableEndpoint(table = TheMovieDB.TRAILERS)
    public static class Trailers {
        // General query, returns a set of records
        @ContentUri(
                path = "trailers",
                type = "vnd.android.cursor.dir/list",
                defaultSort = VideoColumns._ID + " DESC")
        public static final Uri TRAILERS = Uri.parse("content://" + AUTHORITY + "/trailers");
        // Query by ID, returns a set of records
        @InexactContentUri(
                path = "trailers/#",
                name = "TRAILER_ID",
                type = "vnd.android.cursor.dir/list",
                whereColumn = VideoColumns._ID,
                pathSegment = 1)
        public static Uri withId(long id) {
            return Uri.parse("content://" + AUTHORITY + "/trailers/" + id);
        }
    }

    @TableEndpoint(table = TheMovieDB.REVIEWS)
    public static class Reviews {
        // General query, returns a set of records
        @ContentUri(
                path = "reviews",
                type = "vnd.android.cursor.dir/list",
                defaultSort = ReviewColumns._ID + " DESC")
        public static final Uri TRAILERS = Uri.parse("content://" + AUTHORITY + "/reviews");
        // Query by ID, returns a set of records
        @InexactContentUri(
                path = "trailers/#",
                name = "TRAILER_ID",
                type = "vnd.android.cursor.dir/list",
                whereColumn = ReviewColumns._ID,
                pathSegment = 1)
        public static Uri withId(long id) {
            return Uri.parse("content://" + AUTHORITY + "/reviews/" + id);
        }
    }

}