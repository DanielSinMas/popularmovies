package com.example.daniel.popularmovies;


import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Parcelable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.daniel.popularmovies.Provider.FavoriteColumns;
import com.example.daniel.popularmovies.Provider.MoviesProvider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

public class MainFragment extends Fragment implements OnTaskCompleted, LoaderManager.LoaderCallbacks<Cursor> {

    private MovieAdapter movieAdapter;
    private JSONArray array;
    private RecyclerView recyclerView;
    private MovieItem[] list;

    public MainFragment() {}

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment, menu);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.movies_recycler);

        if(savedInstanceState != null){
            list=(MovieItem[])savedInstanceState.get("arrayList");
        }

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArray("arrayList", list);
    }

    private void addImages() {
        if(list==null) {
            AsyncTask task;
            if (Utility.updateSortCriteria(getActivity()).equals("favorites")) {
                task = new getFavoriteMovies(this);
                task.execute();
            } else {
                task = new getMovies(this);
                task.execute();
            }
        }
        else{
            onTaskCompleted();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        movieAdapter = new MovieAdapter(getActivity(), null);
        recyclerView.setAdapter(movieAdapter);
        addImages();
    }

    @Override
    public void onTaskCompleted() {
        movieAdapter = new MovieAdapter(getActivity(), Arrays.asList(list));
        recyclerView.setAdapter(movieAdapter);
        if(MainActivity.twoPane) recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        else recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        recyclerView.setHasFixedSize(true);
    }

    private class getMovies extends AsyncTask {
        private OnTaskCompleted listener;

        public getMovies(OnTaskCompleted listener) {
            super();
            this.listener = listener;
        }

        @Override
        protected Object doInBackground(Object[] params) {
            HttpURLConnection connection = null;
            try {
                URL url = new URL(Utility.getDiscoverUrl());
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setDoInput(true);

                //Get Response
                InputStream is = connection.getInputStream();
                BufferedReader rd = new BufferedReader(new InputStreamReader(is));
                StringBuilder response = new StringBuilder(); // or StringBuffer if not Java 5+
                String line;
                while ((line = rd.readLine()) != null) {
                    response.append(line);
                    response.append('\r');
                }
                rd.close();
                String s = response.toString();
                JSONObject obj = new org.json.JSONObject(s);
                array = obj.getJSONArray("results");
                return array;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
        }

        @Override
        protected void onPostExecute(Object o) {
            if(array!=null && array.length()>0) {
                list = new MovieItem[10];
                MovieItem movie_item;
                for (int i = 0; i < 10; i++) {
                    try {
                        JSONObject obj = (JSONObject) array.get(i);
                        movie_item = new MovieItem(
                                obj.get("id").toString(),
                                obj.get("original_title").toString(),
                                obj.get("poster_path").toString(),
                                obj.get("overview").toString(),
                                Float.parseFloat(obj.get("vote_average").toString()),
                                obj.get("release_date").toString()
                        );
                        list[i] = movie_item;

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                listener.onTaskCompleted();
            }
        }
    }

    private class getFavoriteMovies extends AsyncTask {
        private OnTaskCompleted listener;

        public getFavoriteMovies(OnTaskCompleted listener) {
            super();
            this.listener = listener;
        }

        @Override
        protected Object doInBackground(Object[] params) {
            Cursor c = getActivity().getContentResolver().query(MoviesProvider.Favorites.FAVORITES_URI, null, null, null, FavoriteColumns.TITLE);
            if (c != null && c.getCount() > 0) {
                list = new MovieItem[c.getCount()];
                c.moveToFirst();
                MovieItem item;
                for (int i = 0; i < c.getCount(); i++) {
                    item = new MovieItem(
                            c.getString(0),
                            c.getString(1),
                            c.getString(2),
                            c.getString(5),
                            Float.parseFloat(c.getString(4)),
                            c.getString(3));
                    list[i] = item;
                    c.moveToNext();
                }
                c.close();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            if(list != null && list.length>0) listener.onTaskCompleted();
            else{
                movieAdapter = new MovieAdapter(getActivity(), new ArrayList<MovieItem>());
                recyclerView.setAdapter(movieAdapter);
            }
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(), MoviesProvider.Favorites.FAVORITES_URI,
                null,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

}
