package com.example.daniel.popularmovies;

import android.app.ActionBar;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.daniel.popularmovies.Provider.ExpandableAdapter;
import com.example.daniel.popularmovies.Provider.FavoriteColumns;
import com.example.daniel.popularmovies.Provider.MoviesProvider;
import com.example.daniel.popularmovies.Provider.ReviewColumns;
import com.example.daniel.popularmovies.Provider.VideoColumns;
import com.google.android.youtube.player.YouTubeApiServiceUtil;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeStandalonePlayer;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DetailFragment extends Fragment implements OnTaskCompleted{

    private MovieItem item;
    private JSONArray videosArray;
    private JSONArray reviewsArray;
    private String id;
    private JSONObject obj;
    private Button favoriteButton;
    private ArrayList<VideoItem> listVideos;
    private ArrayList<ReviewItem> listReviews;
    private ViewGroup container;
    private LayoutInflater inflater;
    private int COLOR_RED;
    private  int COLOR_GREEN;

    public DetailFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.inflater=inflater;
        this.container=container;
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        COLOR_RED = ContextCompat.getColor(getActivity(), R.color.red);
        COLOR_GREEN = ContextCompat.getColor(getActivity(), R.color.green);

        item = getActivity().getIntent().getParcelableExtra("item");
        if(item==null){
            Bundle args = getArguments();
            item=args.getParcelable("item");
        }

        ImageView image = (ImageView) rootView.findViewById(R.id.imageDetail);
        TextView text = (TextView) rootView.findViewById(R.id.originalTitle);
        Picasso.with(getActivity()).load("http://image.tmdb.org/t/p/w185/" + item.poster_path).into(image);
        getActivity().setTitle(item.title);
        text.setText(item.title);
        text = (TextView) rootView.findViewById(R.id.releaseDate);
        text.setText(item.release_date);
        text = (TextView) rootView.findViewById(R.id.voteAverage);
        text.setText(item.vote_average+" / 10");
        text = (TextView) rootView.findViewById(R.id.synopsis);
        text.setText(item.synopsis);

        favoriteButton = (Button) rootView.findViewById(R.id.buttonFavorite);
        favoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getTag().toString().equals("R.drawable.favoff")) {
                    addFavorite();
                    favoriteButton.setBackgroundColor(COLOR_GREEN);
                    v.setTag("R.drawable.favon");
                } else {
                    removeFavorite();
                    favoriteButton.setBackgroundColor(COLOR_RED);
                    v.setTag("R.drawable.favoff");
                }
            }
        });

        setFavoriteButton(rootView);

        obj = null;
        if(!getActivity().getIntent().getBooleanExtra("Favorite", false)) {
            getDataMovies g = new getDataMovies(this);
            g.execute();
        }
        else{
            getDataMoviesFromFavorites g = new getDataMoviesFromFavorites(this);
            g.execute();
        }

        return rootView;
    }

    private void removeFavorite() {
        getActivity().getContentResolver().delete(MoviesProvider.Favorites.FAVORITES_URI, "title = ?", new String[]{item.title});
        Toast.makeText(getActivity(), item.title + " deleted from Favorites", Toast.LENGTH_SHORT).show();
        getActivity().getContentResolver().delete(MoviesProvider.Videos.VIDEOS_URI, "movie_id = ?", new String[]{item.id});
        getActivity().getContentResolver().delete(MoviesProvider.Reviews.REVIEWS_URI, "movie_id = ?", new String[]{item.id});
    }

    private void addFavorite() {
        ContentValues cv = new ContentValues();
        cv.put(FavoriteColumns._ID, item.id);
        cv.put(FavoriteColumns.TITLE, item.title);
        cv.put(FavoriteColumns.POSTER_PATH, item.poster_path);
        cv.put(FavoriteColumns.RELEASE_DATE, item.release_date);
        cv.put(FavoriteColumns.VOTE_AVERAGE, item.vote_average);
        cv.put(FavoriteColumns.SYNOPSIS, item.synopsis);

        getActivity().getContentResolver().insert(MoviesProvider.Favorites.FAVORITES_URI, cv);
        Toast.makeText(getActivity(), item.title + " added to Favorites", Toast.LENGTH_SHORT).show();

        saveData();
    }

    private void saveData(){
        ContentValues cv = new ContentValues();
        if(listVideos!=null && listVideos.size()>0) {
            for (int i = 0; i < listVideos.size(); i++) {
                cv.clear();
                cv.put(VideoColumns.MOVIE_ID, listVideos.get(i).getMovie_id());
                cv.put(VideoColumns.KEY, listVideos.get(i).getKey());
                cv.put(VideoColumns.NAME, listVideos.get(i).getName());
                getActivity().getContentResolver().insert(MoviesProvider.Videos.VIDEOS_URI, cv);
            }
        }

        if(listReviews!=null && listReviews.size()>0){
            cv = new ContentValues();
            for(int i=0;i<listReviews.size();i++){
                cv.clear();
                cv.put(ReviewColumns.MOVIE_ID, listReviews.get(i).getMovie_id());
                cv.put(ReviewColumns.AUTHOR, listReviews.get(i).getAuthor());
                cv.put(ReviewColumns.CONTENT, listReviews.get(i).getContent());
                getActivity().getContentResolver().insert(MoviesProvider.Reviews.REVIEWS_URI, cv);
            }
        }
    }

    public void setFavoriteButton(View rootView) {
        Cursor c = getActivity().getContentResolver().query(MoviesProvider.Favorites.FAVORITES_URI, null, "title = ?", new String[]{item.title}, FavoriteColumns.TITLE);
        if(c.getCount()>0){
            favoriteButton.setBackgroundColor(COLOR_GREEN);
            favoriteButton.setTag("R.drawable.favon");
        }
        else{
            favoriteButton.setBackgroundColor(COLOR_RED);
            favoriteButton.setTag("R.drawable.favoff");
        }
        c.close();
    }

    public class getDataMoviesFromFavorites extends AsyncTask{

        private OnTaskCompleted listener;

        public getDataMoviesFromFavorites(OnTaskCompleted listener){
            this.listener=listener;
        }

        @Override
        protected void onPostExecute(Object o) {
            listener.onTaskCompleted();
        }

        @Override
        protected Object doInBackground(Object[] params) {
            VideoItem video;
            listVideos = new ArrayList<>();
            Cursor c = getActivity().getContentResolver().query(MoviesProvider.Videos.VIDEOS_URI, null, VideoColumns.MOVIE_ID+" = ?", new String[]{item.id}, VideoColumns._ID);
            if(c.getCount()>0) {
                while (c.moveToNext()) {
                    video = new VideoItem(
                            c.getString(0),
                            c.getString(1),
                            c.getString(2),
                            c.getString(3)
                    );
                    listVideos.add(video);
                }
            }

            ReviewItem review;
            c.close();
            listReviews = new ArrayList<>();
            c = getActivity().getContentResolver().query(MoviesProvider.Reviews.REVIEWS_URI, null, ReviewColumns.MOVIE_ID+" = ?", new String[]{item.id}, ReviewColumns._ID);
            if(c.getCount()>0){
                while (c.moveToNext()){
                    review = new ReviewItem(
                            c.getString(0),
                            c.getString(1),
                            c.getString(2),
                            c.getString(3)
                    );
                    listReviews.add(review);
                }
            }
            return null;
        }
    }

    @Override
    public void onTaskCompleted() {
        if(listVideos!=null && listVideos.size()>0) addTrailersToLayout();
        if(listReviews!=null && listReviews.size()>0) addReviewsToLayout();
    }

    private void addTrailersToLayout(){
        LinearLayout layout = (LinearLayout) getActivity().findViewById(R.id.trailersLayout);

        TextView label = (TextView) layout.findViewById(R.id.trailersLabel);
        label.setText(R.string.trailers_label);

        View v;

        for(int i=0;i<listVideos.size();i++) {
            v = inflater.inflate(R.layout.trailer_item, container, false);

            TextView text = (TextView) v.findViewById(R.id.textTrailerITem);
            text.setText("Trailer " + (i + 1));

            Button button = (Button) v.findViewById(R.id.buttonTrailerItem);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = YouTubeStandalonePlayer.createVideoIntent(getActivity(), Utility.DEV_KEY, id);
                    getActivity().startActivity(intent);
                }
            });

            layout.addView(v);
            View line = new View(getActivity());
            line.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1));
            line.setBackgroundColor(Color.LTGRAY);
            line.setVisibility(View.VISIBLE);
            layout.addView(line);
        }
    }

    private void addReviewsToLayout(){
        Button reviewsButton = (Button) getActivity().findViewById(R.id.reviewsButton);
        TextView label = (TextView) getActivity().findViewById(R.id.reviewsLabel);
        label.setVisibility(View.GONE);
        reviewsButton.setVisibility(View.VISIBLE);
        reviewsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ReviewActivity.class);
                intent.putExtra("item", item);
                intent.putExtra("listReviews", listReviews);
                startActivity(intent);
            }
        });
    }

    private class getDataMovies extends AsyncTask<Void, Void, Void>{
        private OnTaskCompleted listener;

        public getDataMovies(OnTaskCompleted listener){
            this.listener=listener;
        }

        @Override
        protected void onPostExecute(Void o) {
            super.onPostExecute(o);

            if(videosArray != null && videosArray.length()>0){
                listVideos = new ArrayList<VideoItem>();
                try {
                    VideoItem videoItem;
                    for(int i=0;i< videosArray.length();i++){
                        obj = (JSONObject) videosArray.get(0);
                        id = obj.get("id").toString();
                        videoItem = new VideoItem(
                                obj.get("id").toString(),
                                item.id,
                                obj.get("key").toString(),
                                obj.get("name").toString()
                        );
                        listVideos.add(videoItem);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            if(reviewsArray != null && reviewsArray.length()>0){
                listReviews = new ArrayList<ReviewItem>();
                try{
                    ReviewItem reviewItem;
                    for(int i=0;i<reviewsArray.length();i++){
                        obj = (JSONObject) reviewsArray.get(0);
                        id = obj.get("id").toString();
                        reviewItem = new ReviewItem(
                                id,
                                item.id,
                                obj.get("author").toString(),
                                obj.get("content").toString()
                        );
                        listReviews.add(reviewItem);
                    }
                } catch (JSONException e){
                    e.printStackTrace();
                }
            }
            listener.onTaskCompleted();
        }


        @Override
        protected Void doInBackground(Void... params) {
            HttpURLConnection connection = null;
            try {
                URL url = new URL(Utility.getTrailersUrl(item.id));
                connection = (HttpURLConnection)url.openConnection();
                connection.setRequestMethod("GET");
                connection.setDoInput(true);

                InputStream is = connection.getInputStream();
                BufferedReader rd = new BufferedReader(new InputStreamReader(is));
                StringBuilder response = new StringBuilder();
                String line;
                while((line = rd.readLine()) != null) {
                    response.append(line);
                    response.append('\r');
                }
                rd.close();
                String s = response.toString();
                JSONObject obj = new org.json.JSONObject(s);
                videosArray = obj.getJSONArray("results");

                url = new URL(Utility.getReviewsUrl(item.id));
                connection = (HttpURLConnection)url.openConnection();
                connection.setRequestMethod("GET");
                connection.setDoInput(true);

                is = connection.getInputStream();
                rd = new BufferedReader(new InputStreamReader(is));
                response = new StringBuilder();
                while((line = rd.readLine()) != null){
                    response
                            .append(line);
                    response.append('\r');
                }
                rd.close();
                s = response.toString();
                obj = new JSONObject(s);
                reviewsArray = obj.getJSONArray("results");
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if(connection != null) {
                    connection.disconnect();
                }
            }
            return null;
        }
    }

    private Intent createShareIntent(){
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
        intent.setType("text/plain");
        if(listVideos!=null && listVideos.size()>0) intent.putExtra(Intent.EXTRA_TEXT, "youtube.com/watch?v="+listVideos.get(0).getId());
        else  intent.putExtra(Intent.EXTRA_TEXT, "No trailers available");

        return intent;
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.detail_fragment, menu);

        MenuItem item = menu.findItem(R.id.menu_item_share);

        ShareActionProvider actionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);

        if(actionProvider != null){
            actionProvider.setShareIntent(createShareIntent());
        }
    }
}
