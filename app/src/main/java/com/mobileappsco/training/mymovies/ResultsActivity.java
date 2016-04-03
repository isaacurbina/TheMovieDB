package com.mobileappsco.training.mymovies;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.mobileappsco.training.mymovies.adapters.RVAdapter;
import com.mobileappsco.training.mymovies.entities.Favorites;
import com.mobileappsco.training.mymovies.entities.PageResults;
import com.mobileappsco.training.mymovies.entities.Result;
import com.mobileappsco.training.mymovies.listeners.EndlessRecyclerOnScrollListener;
import com.mobileappsco.training.mymovies.listeners.RecyclerItemClickListener;
import com.mobileappsco.training.mymovies.retrofit.RetrofitInterface;
import com.orm.SugarRecord;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ResultsActivity extends AppCompatActivity {

    public static String API_JSON_URL, API_IMAGES_URL, API_KEY;
    String RESTAG = "RESTAG";
    RecyclerView recyclerView;
    LinearLayoutManager llm;
    RVAdapter adapter;
    Context context;
    StaggeredGridLayoutManager staggeredGridLayoutManager;
    boolean loading = false;
    int columns = 1;
    int page = 1;
    String language, search_title, search_year, show_favorites;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);
        context = this;
        Log.i("MYTAG", "oncreate activity resultsaactivity");

        // Set icon in toolbar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setLogo(R.mipmap.ic_launcher);
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        API_KEY = getResources().getString(R.string.API_KEY);
        API_JSON_URL = getResources().getString(R.string.API_JSON_URL);
        API_IMAGES_URL = getResources().getString(R.string.API_IMAGES_URL);

        if (savedInstanceState != null) {
            search_title = savedInstanceState.getString("search_title");
            search_year = savedInstanceState.getString("search_year");
            show_favorites = savedInstanceState.getString("show_favorites");
        }
        columns = getSupportedColumns();
        language = getResources().getString(R.string.language);

        // Recycler View in ResultsFragment
        recyclerView = (RecyclerView) findViewById(R.id.recycler_list_results);
        recyclerView.setHasFixedSize(true);
        staggeredGridLayoutManager = new StaggeredGridLayoutManager(columns, 1);
        recyclerView.setLayoutManager(staggeredGridLayoutManager);
        recyclerView.setOnScrollListener(new EndlessRecyclerOnScrollListener(staggeredGridLayoutManager) {
            @Override
            public void onLoadMore(int current_page) {
                // do something...
                if (!loading) {
                    loading = true;
                    page++;
                    if (isNetworkAvailable()) {
                        fetchContent();
                        //Toast.makeText(context, "Loading page " + page, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(context, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        TextView card_id = (TextView) view.findViewById(R.id.card_id);
                        String result_id = card_id.getText().toString();
                        //Toast.makeText(context, "click en :" + card_id.getText(), Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(context, DetailActivity.class);
                        i.putExtra("result_id", result_id);
                        startActivity(i);
                    }
                })
        );
        // fetch movies for the first page
        handleIntent(getIntent());
        fetchContent();
    }
    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    public boolean isNumeric(String q) {
        try {
            int i = Integer.parseInt(q);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private void handleIntent(Intent intent) {

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            if (isNumeric(query))
                search_year = query;
            else
                search_title = query;
        } else if (getIntent().hasExtra("show_favorites")) {
            show_favorites = getIntent().getExtras().getString("show_favorites");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.favorites:
                Log.i("MYTAG", "display favorites");
                displayFavorites();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void displayFavorites() {
        Intent i = new Intent(this, ResultsActivity.class);
        i.putExtra("show_favorites", "favorites");
        startActivity(i);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("search_title", search_title);
        outState.putString("search_year", search_year);
        outState.putString("show_favorites", show_favorites);
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.home_menu, menu);
        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.menu_search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));
        return true;
    }

    public void fetchContent() {
        Log.i("MYTAG", "fetch content");
        // If we have internet we fetch information from the REST service
        if (valueSet(show_favorites)) {
            Log.i("MYTAG", "show favorites");
            FetchFavoritesTask mytask = new FetchFavoritesTask();
            mytask.execute();
        } else if (isNetworkAvailable()) {
            Log.i("MYTAG", "fetch online");
            FetchMoviesOnlineTask mytask = new FetchMoviesOnlineTask();
            mytask.execute(
                    search_title,
                    search_year,
                    String.valueOf(page),
                    language,
                    getResources().getString(R.string.API_JSON_URL));
        } else { // if no internet, we try to load info from our database*/
            Log.i("MYTAG", "fetch offline");
            FetchMoviesOfflineTask mytask = new FetchMoviesOfflineTask();
            mytask.execute(
                    search_title,
                    search_year);
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    // Dynamically display different columns according to the device width
    public int getSupportedColumns() {
        if (findViewById(R.id.tablet) != null)
            return 3;
        else if (findViewById(R.id.tablet_land) != null)
            return 4;
        else if (findViewById(R.id.phone_land) != null)
            return 3;
        else
            return 2;
    }

    public void updateResult(Result res1, Result res2) {
        res2.setPosterPath(res1.getPosterPath());
        res2.setAdult(res1.getAdult());
        res2.setOverview(res1.getOverview());
        res2.setReleaseDate(res1.getReleaseDate());
        //res2.setGenreIds(res1.getGenreIds());
        res2.setOriginalTitle(res1.getOriginalTitle());
        res2.setOriginalLanguage(res1.getOriginalLanguage());
        res2.setTitle(res1.getTitle());
        res2.setBackdropPath(res1.getBackdropPath());
        res2.setPopularity(res1.getPopularity());
        res2.setVoteCount(res1.getVoteCount());
        res2.setVideo(res1.getVideo());
        res2.setVoteAverage(res1.getVoteAverage());
        SugarRecord.save(res2);
    }

    public boolean valueSet(String text) {
        if (text != null && text.length()>0)
            return true;
        else
            return false;
    }

    private class FetchMoviesOfflineTask extends AsyncTask<String, Integer, List<Result>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(List<Result> resultstask) {
            super.onPostExecute(resultstask);
            adapter = new RVAdapter(context, resultstask);
            recyclerView.setAdapter(adapter);
        }

        @Override
        protected List<Result> doInBackground(String... params) {
            String search_title = params[0];
            String search_year = params[1];
            boolean hasTitle = valueSet(search_title);
            boolean hasYear = valueSet(search_year);
            search_title = "%"+search_title+"%";
            List<Result> resultstask;
            if (hasTitle && !hasYear) {
                resultstask = SugarRecord.find(
                        Result.class,
                        "title LIKE ? OR original_title LIKE ? OR overview LIKE ?",
                        search_title, search_title, search_title);
                Log.i("MYTAG", "Offline search by title: "+resultstask.size());
            } else if (!hasTitle && hasYear) {
                resultstask = SugarRecord.find(
                        Result.class,
                        "release_date LIKE ?",
                        search_year+"%");
                Log.i("MYTAG", "Offline search by year: "+resultstask.size());
            } else if (hasTitle && hasYear) {
                resultstask = SugarRecord.find(
                        Result.class,
                        "(title LIKE ? OR original_title LIKE ? OR overview LIKE ?) AND release_date LIKE ?",
                        search_title, search_title, search_title, search_year+"%");
                Log.i("MYTAG", "Offline search by year: "+resultstask.size());
            } else {
                resultstask = SugarRecord.listAll(Result.class, "popularity DESC");
            }
            return resultstask;
        }
    }

    private class FetchMoviesOnlineTask extends AsyncTask<String, Integer, List<Result>> {

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(List<Result> resultstask) {
            super.onPostExecute(resultstask);
            try {
                if (resultstask==null) {
                    Toast.makeText(context, "No results found", Toast.LENGTH_SHORT).show();
                } else {
                    if (adapter != null) {
                        adapter.addResultList(resultstask);
                    } else {
                        adapter = new RVAdapter(context, resultstask);
                        recyclerView.setAdapter(adapter);
                    }
                    for (Result res1 : resultstask) {
                        // List<Result> res2 = Result.find(Result.class, "id = ?", res1.getId().toString());
                        List<Result> res2 = SugarRecord.find(Result.class, "id = ?", res1.getId().toString());
                        if (res2.size()==0) {
                            SugarRecord.save(res1);
                            Log.i("MYTAG", "saving a result " + res1.getId());
                        } else {
                            Log.i("MYTAG", "already had, updating result " + res1.getId());
                            updateResult(res1, res2.get(0));
                        }
                    }
                }
            } catch (Exception e) {
                Log.e("MYTAG", "ERROR Online for: " +e.getMessage()+e.getStackTrace().toString());
            }
            loading = false;
        }

        @Override
        protected List<Result> doInBackground(String... params) {

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(params[4])
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            RetrofitInterface rfInterface = retrofit.create(RetrofitInterface.class);

            boolean hasTitle = false;
            boolean hasYear = false;
            String search_title = params[0];
            String search_year = params[1];
            String api_key = API_KEY;
            String page = params[2];
            String language = params[3];
            hasTitle = valueSet(params[0]);
            hasYear = valueSet(params[1]);

            Call<PageResults> request;
            if (hasTitle && !hasYear) {
                Log.i("MYTAG", "search by title");
                request = rfInterface.searchMovieByTitle(
                        api_key,
                        search_title,
                        "popularity.desc",
                        page,
                        language);
            } else if (!hasTitle && hasYear) {
                Log.i("MYTAG", "search by year");
                request = rfInterface.searchMovieByYear(
                        api_key,
                        search_year,
                        "popularity.desc",
                        page,
                        language);
            } else if (hasTitle && hasYear) {
                Log.i("MYTAG", "search by title and year");
                request = rfInterface.searchMovieByTitleAndYear(
                        api_key,
                        search_title,
                        search_year,
                        "primary_release_year.desc",
                        page,
                        language);
            } else {
                Log.i("MYTAG", "search newest");
                request = rfInterface.discoverMovies(
                        api_key,
                        "popularity.desc",
                        page,
                        language);
            }

            PageResults pages = null;
            List<Result> resultsasync = new ArrayList<>();
            try {
                pages = request.execute().body();
                resultsasync = pages.getResults();
            } catch (Exception e) {
                Log.e("MYTAG", "Error getting results "+e.getMessage());
            }
            return resultsasync;

        }
    }

    private class FetchFavoritesTask extends AsyncTask<String, Integer, List<Result>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(List<Result> resultstask) {
            super.onPostExecute(resultstask);
            adapter = new RVAdapter(context, resultstask);
            recyclerView.setAdapter(adapter);
        }

        @Override
        protected List<Result> doInBackground(String... params) {

            List<Result> resultstask = new ArrayList<>();
            List<Favorites> favorites = SugarRecord.listAll(Favorites.class, "id DESC");
            for (Favorites fav1: favorites) {
                Result res1 = SugarRecord.findById(Result.class, fav1.getId());
                if (res1 != null)
                    resultstask.add(res1);
            }
            return resultstask;
        }
    }


}
