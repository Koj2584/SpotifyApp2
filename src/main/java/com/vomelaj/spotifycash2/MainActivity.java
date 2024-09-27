package com.vomelaj.spotifycash2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.android.material.resources.TextAppearance;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    Button btn1;
    Button btn2;
    private AppDatabase db;

    ListView body;

    ArrayList<String> list;
    ArrayList<Dluznik> dluznici;

    PrehledAdapter adapter;

    TextView stav;
    TextView stavKc;
    TextView predplaceno;
    TextView dluzeno;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = AppDatabase.getInstance(this);

        preferences = getSharedPreferences("Settings", MODE_PRIVATE);
        setContentView(R.layout.activity_main);

        btn1 = findViewById(R.id.nastaveni);
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,Nastaveni.class));
            }
        });

        btn2 = findViewById(R.id.platba);
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,Platba.class));
            }
        });

        String lastOpenDate = preferences.getString("posledniDatum", null);

        Date currentDate = new Date();

        if (lastOpenDate == null) {
            saveCurrentDate(currentDate);
        } else {
            int n = preferences.getInt("payDay", 1);
            List<String> fifteenthDates = getNthDaysSinceLastOpen(lastOpenDate, currentDate, n);

            int castka;
            if(preferences.getBoolean("ch1",true)){
                float castkaP = preferences.getInt("castka", 259)/6;
                castka = Math.round(castkaP);
            }else{
                float castkaP = preferences.getInt("castka", 259)/5;
                castka = Math.round(castkaP);
            }
            new Thread(() -> {
                List<Dluznik> dluznici = db.dluznikDao().getAllDluznici();
                for(Dluznik d : dluznici){
                    for (String date : fifteenthDates) {
                        Payment payment = new Payment(d.getId(), -castka, date);
                        payment.poznamka = "Měsíční platba";
                        db.paymentDao().insert(payment);
                        d.addToKonto(Integer.parseInt(String.valueOf(-castka)));
                        db.dluznikDao().update(d);
                    }
                }

                runOnUiThread(() -> onResume());
            }).start();

            saveCurrentDate(currentDate);
        }
    }

    private void saveCurrentDate(Date currentDate) {
        SharedPreferences.Editor editor = preferences.edit();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        editor.putString("posledniDatum", dateFormat.format(currentDate));
        editor.apply();
    }

    private List<String> getNthDaysSinceLastOpen(String lastOpenDate, Date currentDate, int n) {
        List<String> fifteenthDays = new ArrayList<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

        try {
            Date lastOpen = dateFormat.parse(lastOpenDate);

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(lastOpen);

            while (calendar.getTime().before(currentDate)) {
                int maxDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

                if (n <= maxDay) {
                    calendar.set(Calendar.DAY_OF_MONTH, n);
                } else {
                    calendar.set(Calendar.DAY_OF_MONTH, maxDay);
                }

                calendar.set(Calendar.HOUR_OF_DAY, 0);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);

                if (calendar.getTime().after(lastOpen) && calendar.getTime().before(currentDate)) {
                    fifteenthDays.add(dateFormat.format(calendar.getTime()));
                }
                calendar.add(Calendar.MONTH, 1);
            }
        } catch (Exception e) {
        }

        return fifteenthDays;
    }

    @Override
    protected void onResume() {
        super.onResume();

        db = AppDatabase.getInstance(this);
        body = findViewById(R.id.body);
        list = new ArrayList<>();
        dluznici = new ArrayList<>();
        adapter = new PrehledAdapter(getBaseContext(), list, dluznici);

        stav = findViewById(R.id.stavPenez);
        stavKc = findViewById(R.id.stavKc);
        predplaceno = findViewById(R.id.predplaceno);
        dluzeno = findViewById(R.id.dluzeno);

        body.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String jmeno = ((TextView)(view.findViewById(R.id.jmenoPrehled))).getText().toString();

                Intent intent = new Intent(MainActivity.this, Platby.class);
                intent.putExtra("jmeno", jmeno);
                startActivity(intent);
            }
        });


        list.clear();
        ViewGroup.LayoutParams paramss = body.getLayoutParams();
        paramss.height = 0;
        body.setLayoutParams(paramss);

        new Thread(() -> {
            List<Dluznik> debtors = db.dluznikDao().getAllDluznici();
            if(debtors.size() > 0) {
                runOnUiThread(() -> {
                    int dluzenoHodnota = 0, predplacenoHodnota = 0;
                    for(Dluznik d : debtors){
                        ViewGroup.LayoutParams params = body.getLayoutParams();
                        params.height += (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 21, getResources().getDisplayMetrics());
                        body.setLayoutParams(params);

                        if(d.konto > 0)
                            predplacenoHodnota += d.konto;
                        if(d.konto < 0)
                            dluzenoHodnota += d.konto;

                        list.add(d.getName());
                        dluznici.add(d);
                        body.setAdapter(adapter);
                    }

                    stav.setText(String.valueOf(dluzenoHodnota+predplacenoHodnota));
                    if(dluzenoHodnota+predplacenoHodnota >= 0)
                        stavKc.setTextColor(getColor(R.color.plusMoneyKc));
                    else if(dluzenoHodnota+predplacenoHodnota < 0)
                        stavKc.setTextColor(getColor(R.color.minusMoneyKc));

                    predplaceno.setText(String.valueOf(predplacenoHodnota) + "Kč");
                    dluzeno.setText(String.valueOf(dluzenoHodnota) + "Kč");


                    if(predplacenoHodnota > 0)
                        predplaceno.setTextColor(getColor(R.color.plusMoneyKc));
                    else if(predplacenoHodnota < 0)
                        predplaceno.setTextColor(getColor(R.color.minusMoneyKc));
                    else
                        predplaceno.setTextColor(getColor(R.color.zeroMoneyKc));


                    if(dluzenoHodnota > 0)
                        dluzeno.setTextColor(getColor(R.color.plusMoneyKc));
                    else if(dluzenoHodnota < 0)
                        dluzeno.setTextColor(getColor(R.color.minusMoneyKc));
                    else
                        dluzeno.setTextColor(getColor(R.color.zeroMoneyKc));

                    findViewById(R.id.hlaska).setVisibility(View.GONE);
                    findViewById(R.id.scroll).setVisibility(View.VISIBLE);
                });
            } else {
                runOnUiThread(() -> {
                    findViewById(R.id.hlaska).setVisibility(View.VISIBLE);
                    findViewById(R.id.scroll).setVisibility(View.GONE);
                });
            }
        }).start();
    }
}