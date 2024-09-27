package com.vomelaj.spotifycash2;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

public class Platby extends AppCompatActivity {

    String jmeno;

    List<Payment> platby;

    AppDatabase db;

    TextView nadpis;
    Button zpet;

    LinearLayout platbyBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_platby);

        jmeno = getIntent().getStringExtra("jmeno");
        nadpis = findViewById(R.id.nazev);
        zpet = findViewById(R.id.zpetPlatby);
        platbyBox = findViewById(R.id.platbyBox);

        nadpis.setText(jmeno);

        zpet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        db = AppDatabase.getInstance(this);


    }

    @Override
    protected void onResume() {
        super.onResume();
        platbyBox.removeAllViews();
        new Thread(() -> {
            Dluznik d = db.dluznikDao().getDluznikByName(jmeno);
            platby = db.paymentDao().getPaymentsForDebtor(d.id);

            runOnUiThread(() -> {
                int i = 0;
                int posledniCastka = 0;
                for (Payment p : platby) {
                    View row;
                    if(i%2==0)
                        row = View.inflate(this, R.layout.platby_row_right, null);
                    else
                        row = View.inflate(this, R.layout.platby_row_left, null);
                    ((TextView) row.findViewById(R.id.id)).setText(String.valueOf(p.id));
                    ((TextView) row.findViewById(R.id.castka)).setText(String.valueOf(p.amount));
                    posledniCastka = p.amount;
                    ((TextView) row.findViewById(R.id.poznamka)).setText(String.valueOf(p.poznamka));
                    ((TextView) row.findViewById(R.id.datum)).setText(String.valueOf(p.datum));
                    ImageButton button = row.findViewById(R.id.delete);
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            new Thread(()->{
                                Payment p = db.paymentDao().getPaymentById(Integer.parseInt(((TextView)((View)view.getParent()).findViewById(R.id.id)).getText().toString()));
                                Dluznik d = db.dluznikDao().getDluznikById(p.dluznikId);
                                d.konto -= p.amount;
                                db.dluznikDao().update(d);
                                db.paymentDao().delete(p);
                                runOnUiThread(() -> onResume());
                            }).start();
                        }
                    });

                    if(p.amount > 0){
                        if(i==0) {
                            Drawable drawable = ((ImageView) findViewById(R.id.sipka)).getDrawable();
                            if (drawable != null) {
                                drawable.setTint(getColor(R.color.plusMoneyKc));
                            }
                        }
                        ((TextView) row.findViewById(R.id.kc)).setTextColor(getColor(R.color.plusMoneyKc));
                        row.findViewById(R.id.line1).setBackgroundColor(getColor(R.color.plusMoneyKc));
                        row.findViewById(R.id.line2).setBackgroundColor(getColor(R.color.plusMoneyKc));
                        row.findViewById(R.id.line3).setBackgroundColor(getColor(R.color.plusMoneyKc));
                        row.findViewById(R.id.line4).setBackgroundColor(getColor(R.color.plusMoneyKc));
                    } else if(p.amount < 0){
                        if(i==0) {
                            Drawable drawable = ((ImageView) findViewById(R.id.sipka)).getDrawable();
                            if (drawable != null) {
                                drawable.setTint(getColor(R.color.minusMoneyKc));
                            }
                        }
                        ((TextView) row.findViewById(R.id.kc)).setTextColor(getColor(R.color.minusMoneyKc));
                        row.findViewById(R.id.line1).setBackgroundColor(getColor(R.color.minusMoneyKc));
                        row.findViewById(R.id.line2).setBackgroundColor(getColor(R.color.minusMoneyKc));
                        row.findViewById(R.id.line3).setBackgroundColor(getColor(R.color.minusMoneyKc));
                        row.findViewById(R.id.line4).setBackgroundColor(getColor(R.color.minusMoneyKc));
                    }
                    platbyBox.addView(row);
                    i++;
                }
                if(posledniCastka>0)
                    findViewById(R.id.line6).setBackgroundColor(getColor(R.color.plusMoneyKc));
                else if(posledniCastka<0)
                    findViewById(R.id.line6).setBackgroundColor(getColor(R.color.minusMoneyKc));
            });
        }).start();
    }
}