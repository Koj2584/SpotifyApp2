package com.vomelaj.spotifycash2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

class ListViewAdapter extends ArrayAdapter<String> {
    ArrayList<String> list;
    Context context;
    Nastaveni trida;

    public ListViewAdapter(Context context, ArrayList<String> items, Nastaveni trida) {
        super(context, R.layout.item, items);
        this.context = context;
        list = items;
        this.trida = trida;
    }

    // The method we override to provide our own layout for each View (row) in the ListView
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater mInflater = LayoutInflater.from(context);
            convertView = mInflater.inflate(R.layout.item, null);
            TextView name = convertView.findViewById(R.id.name);
            ImageButton remove = convertView.findViewById(R.id.remove);

            name.setText(list.get(position));

            remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    trida.removeItem(position);
                }
            });
        }
        return convertView;
    }

}