package com.example.daniel.popularmovies;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.example.daniel.popularmovies.Provider.ExpandableAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ReviewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        ArrayList<ReviewItem> listReviews = (ArrayList<ReviewItem>) getIntent().getExtras().get("listReviews");

        ExpandableListView expandableListView = (ExpandableListView) findViewById(R.id.expandableListReviews);
        HashMap<String, String> listExpandableReviews = new HashMap<>();
        final List<String> listHeaders = new ArrayList<>();

        for(int i=0;i<listReviews.size();i++) {
            listHeaders.add(listReviews.get(i).getAuthor());
            listExpandableReviews.put(listHeaders.get(i), reduceContent(listReviews.get(i).getContent()));
        }

        ExpandableListAdapter adapter = new ExpandableAdapter(this, listHeaders, listExpandableReviews);
        expandableListView.setAdapter(adapter);
    }

    private String reduceContent(String content){
        return content.substring(content.lastIndexOf('>') + 1);
    }
}
