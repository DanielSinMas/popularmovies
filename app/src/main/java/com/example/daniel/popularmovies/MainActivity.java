package com.example.daniel.popularmovies;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity implements MovieAdapter.Callback{

    public static boolean twoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        if (findViewById(R.id.movies_detail_container) != null) {
            twoPane = true;

            if (savedInstanceState == null) {

            }
        } else {
            twoPane = false;
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected(MovieItem item) {
        if(twoPane){
            Bundle args = new Bundle();
            args.putParcelable("item", item);
            if(Utility.getSortCriteria().equals("favorites")){
                args.putBoolean("Favorite", true);
            }
            else args.putBoolean("Favorite", false);
            DetailFragment fragment = new DetailFragment();
            fragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.movies_detail_container, fragment, null)
                    .commit();
        }
        else{
            Intent intent = new Intent(this, DetailActivity.class);
            intent.putExtra("item", item);
            if(Utility.getSortCriteria().equals("favorites")){
                intent.putExtra("Favorite", true);
            }
            else intent.putExtra("Favorite", false);
            startActivity(intent);
        }
    }
}
