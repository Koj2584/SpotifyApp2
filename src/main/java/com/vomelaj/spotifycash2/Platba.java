package com.vomelaj.spotifycash2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Toast;

import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Platba extends AppCompatActivity {

    NumberPicker dluznici;
    private AppDatabase db;
    SharedPreferences sp;

    Button platba;

    EditText castka;

    EditText poznamka;


    ArrayList<String> jmenaList = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_platba);

        ((Button)findViewById(R.id.zpetPlatba)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        dluznici = findViewById(R.id.dluznikPicker);
        dluznici.setMinValue(0);
        dluznici.setWrapSelectorWheel(false);

        db = AppDatabase.getInstance(this);
        new Thread(() -> {
            List<Dluznik> dluzniciList;
            dluzniciList = db.dluznikDao().getAllDluznici();
            if(dluzniciList.size() > 0) {
                for (Dluznik d : dluzniciList) {
                    jmenaList.add(d.getName());
                }

                runOnUiThread(() -> {
                    dluznici.setMaxValue(jmenaList.size() - 1);
                    dluznici.setDisplayedValues(jmenaList.toArray(new String[0]));
                });
            }else {
                dluznici.setMaxValue(0);
                dluznici.setDisplayedValues(new String[]{"-"});
            }
        }).start();

        sp = getApplicationContext().getSharedPreferences("Settings", Context.MODE_PRIVATE);
        poznamka = findViewById(R.id.poznamka);

        if(sp.getBoolean("ch2", false)){
            ((EditText)findViewById(R.id.castkaPlatba)).setInputType(InputType.TYPE_NUMBER_FLAG_SIGNED | InputType.TYPE_CLASS_NUMBER);
        }
        if(sp.getBoolean("ch3", false)){
            poznamka.setVisibility(View.VISIBLE);
        }

        castka = findViewById(R.id.castkaPlatba);

        platba = findViewById(R.id.platbaBtn);
        platba.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!(dluznici.getDisplayedValues()[dluznici.getValue()].equals("-")||castka.getText().length()<1||castka.getText().toString().equals("0"))) {
                    String jmeno = jmenaList.toArray(new String[0])[dluznici.getValue()];
                    new Thread(() -> {
                        Dluznik dluznik = db.dluznikDao().getDluznikByName(jmeno);
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
                        Payment payment = new Payment(dluznik.getId(), Integer.parseInt(String.valueOf(castka.getText())), formatter.format(LocalDateTime.now()));
                        if(sp.getBoolean("ch3", false) && !poznamka.getText().toString().equals(""))
                            payment.poznamka = poznamka.getText().toString();
                        db.paymentDao().insert(payment);
                        dluznik.addToKonto(Integer.parseInt(String.valueOf(castka.getText())));
                        db.dluznikDao().update(dluznik);
                    }).start();
                    finish();
                } else {
                    Toast.makeText(Platba.this, "Je potřeba zadat částku!!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}