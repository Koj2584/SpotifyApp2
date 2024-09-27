package com.vomelaj.spotifycash2;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class PrehledAdapter extends ArrayAdapter<String> {
    ArrayList<String> jmena;
    Context context;

    TextView jmeno;
    TextView konto;
    TextView kc;
    ImageView dot;
    TextView stav;

    ArrayList<Dluznik> dluznici;



    public PrehledAdapter(Context context, ArrayList<String> jmena, ArrayList<Dluznik> dluznici) {
        super(context, R.layout.prehled_row, jmena);
        this.context = context;
        this.jmena = jmena;
        this.dluznici = dluznici;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if(convertView == null){
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.prehled_row, parent, false);

            jmeno = convertView.findViewById(R.id.jmenoPrehled);
            konto = convertView.findViewById(R.id.kontoPrehled);
            kc = convertView.findViewById(R.id.kcPrehled);
            dot = convertView.findViewById(R.id.dotPrehled);
            stav = convertView.findViewById(R.id.stavPrehled);

            Dluznik dluznik = dluznici.get(position);

            jmeno.setText(jmena.get(position));

            int kontoValue = dluznik.konto;
            konto.setText(String.valueOf(kontoValue));

            if(kontoValue > 0){
                konto.setTextColor(context.getResources().getColor(R.color.plusMoney));
                kc.setTextColor(context.getResources().getColor(R.color.plusMoneyKc));

                Drawable drawable = dot.getDrawable();
                if (drawable != null) {
                    drawable.setTint(context.getResources().getColor(R.color.plusMoney));
                }

                stav.setTextColor(context.getResources().getColor(R.color.plusMoneyKc));
                stav.setText("Předplaceno");
            } else if (kontoValue < 0) {
                konto.setTextColor(context.getResources().getColor(R.color.minusMoney));
                kc.setTextColor(context.getResources().getColor(R.color.minusMoneyKc));

                Drawable drawable = dot.getDrawable();
                if (drawable != null) {
                    drawable.setTint(context.getResources().getColor(R.color.minusMoney));
                }

                stav.setTextColor(context.getResources().getColor(R.color.minusMoneyKc));
                stav.setText("Nesplaceno");
            } else {
                konto.setTextColor(context.getResources().getColor(R.color.zeroMoney));
                kc.setTextColor(context.getResources().getColor(R.color.zeroMoneyKc));

                Drawable drawable = dot.getDrawable();
                if (drawable != null) {
                    drawable.setTint(context.getResources().getColor(R.color.zeroMoney));
                }

                stav.setTextColor(context.getResources().getColor(R.color.zeroMoneyKc));
                stav.setText("Zaplaceno");
            }
        }
        return convertView;
    }
}
