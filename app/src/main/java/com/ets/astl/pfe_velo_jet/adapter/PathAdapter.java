package com.ets.astl.pfe_velo_jet.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.ets.astl.pfe_velo_jet.R;
import com.ets.astl.pfe_velo_jet.entity.Path;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class PathAdapter extends ArrayAdapter<Path>{

    private Context context;
    private int resource;
    private Path data[] = null;

    public PathAdapter(Context context, int resource, Path[] objects) {
        super(context, resource, objects);

        this.resource = resource;
        this.context = context;
        this.data = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //return super.getView(position, convertView, parent);

        View row = convertView;
        PathHolder holder = null;

        if (row == null) {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(resource, parent, false);

            holder = new PathHolder();
            holder.path_name = (TextView)row.findViewById(R.id.path_name);
            holder.path_date = (TextView)row.findViewById(R.id.path_date);
            holder.path_distance = (TextView)row.findViewById(R.id.path_distance);

            row.setTag(holder);
        } else {
            holder = (PathHolder)row.getTag();
        }

        Path path = data[position];

        SimpleDateFormat formatter = new SimpleDateFormat("dd MMMM, yyyy", Locale.CANADA_FRENCH);

        holder.path_name.setText(path.getName());
        holder.path_date.setText(formatter.format(path.getDate()));
        holder.path_distance.setText(String.format(Locale.CANADA_FRENCH,"%.2f", path.getDistance() / 1000.0f) + " km");

        return row;
    }

    static class PathHolder {
        TextView path_name;
        TextView path_date;
        TextView path_distance;
    }
}
