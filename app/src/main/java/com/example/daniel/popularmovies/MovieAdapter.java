package com.example.daniel.popularmovies;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Daniel on 23/10/2015.
 */
public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.ViewHolder> {

    private List<MovieItem> list;
    private Context context;

    public interface Callback{
        void onItemSelected(MovieItem item);
    }

    public MovieAdapter(Activity context, List<MovieItem> list) {
        this.context=context;
        this.list=list;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public ViewHolder(View view) {
            super(view);
            imageView = (ImageView) view.findViewById(R.id.movie_image);
        }
    }

    @Override
    public MovieAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.movie_item, parent, false);

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(MovieAdapter.ViewHolder holder, final int position) {
        Picasso.with(context).load("http://image.tmdb.org/t/p/w185/" + list.get(position).poster_path).into(holder.imageView);
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((Callback) context).onItemSelected(list.get(position));
            }
        });
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        if (list!=null) return list.size();
        else return 0;
    }
}
