package com.toddev.bechdel;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by David Margolin on 5/9/2016.
 * Handles Listview creation
 */
public class MovieAdapter extends BaseAdapter {
    private static LayoutInflater inflater = null;
    Context context;
    ArrayList<OmdbResultItems> items = new ArrayList<OmdbResultItems>();

    public MovieAdapter(MainActivity mainActivity, ArrayList<OmdbResultItems> p) {
        items = p;
        context = mainActivity;
        inflater = (LayoutInflater) context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        Holder holder = new Holder();
        View rowView;
        rowView = inflater.inflate(R.layout.movielist_item, null);
        holder.titletextview = (TextView) rowView.findViewById(R.id.title);
        holder.coverimageview = (ImageView) rowView.findViewById(R.id.cover);
        holder.titletextview.setText(items.get(position).getTitle() + " (" + items.get(position).getYear() + ")");
        //jsoup parsing


        //load image
        Picasso.with(context).load(items.get(position).getPoster()).placeholder(R.drawable.placeholder).into(holder.coverimageview);

        if (!(items.get(0).getPoster() == "404error")) {
            rowView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent detailactivity = new Intent(context, IndiActivity.class);
                    detailactivity.putExtra("id", items.get(position).getImdbID());
                    detailactivity.putExtra("title", items.get(position).getTitle());
                    detailactivity.putExtra("poster", items.get(position).getPoster());
                    detailactivity.putExtra("rating", items.get(position).getImdbRating());
                    detailactivity.putExtra("plot", items.get(position).getPlot());
                    detailactivity.putExtra("year", items.get(position).getYear());
                    detailactivity.putExtra("awards", items.get(position).getAwards());
                    context.startActivity(detailactivity);
                }
            });
        }

        return rowView;
    }

    public class Holder {
        TextView titletextview;
        ImageView coverimageview;
    }

}
