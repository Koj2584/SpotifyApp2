package com.vomelaj.spotifycash2;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class Nastaveni extends AppCompatActivity {

    Button btn;
    ListView lv;
    ArrayList<String> list;
    ListViewAdapter adapter;
    Button pridat;

    EditText et;

    Integer countNum = 0;

    TextView countNumText;

    NumberPicker numPick;

    private AppDatabase db;

    SharedPreferences sp;

    EditText castka;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nastaveni);

        btn = findViewById(R.id.zpetNastaveni);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        lv = findViewById(R.id.listView);
        list = new ArrayList<>();

        adapter = new ListViewAdapter(getBaseContext(), list, Nastaveni.this);

        countNumText = findViewById(R.id.countNum);
        et = findViewById(R.id.editTextText);
        pridat = findViewById(R.id.pridat);
        pridat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!et.getText().toString().equals("")){
                    addItem(et.getText().toString());
                } else {
                    Toast.makeText(Nastaveni.this, "Je potřeba napsat jméno!!", Toast.LENGTH_SHORT).show();
                }
            }
        });


        db = AppDatabase.getInstance(this);
        new Thread(() -> {
            List<Dluznik> debtors = db.dluznikDao().getAllDluznici();
            for (Dluznik d : debtors) {
                runOnUiThread(() -> {
                    displayName(d.getName());
                });
            }
        }).start();


        numPick = findViewById(R.id.numPick);
        numPick.setMaxValue(31);
        numPick.setMinValue(1);
        numPick.setWrapSelectorWheel(false);

        sp = getApplicationContext().getSharedPreferences("Settings", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();

        try {
            numPick.setValue(sp.getInt("payDay", 1));
        } catch (Exception x){}

        numPick.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                editor.putInt("payDay",i1);
                editor.apply();
            }
        });

        castka = findViewById(R.id.castka);
        castka.setText(String.valueOf(sp.getInt("castka", 269)));
        castka.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String str = castka.getText().toString();

                if(str.length() > 0) {
                    editor.putInt("castka", Integer.parseInt(str));
                    editor.apply();
                } else {
                    editor.remove("castka");
                    editor.apply();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });


        CheckBox ch = findViewById(R.id.checkBox);
        ch.setChecked(sp.getBoolean("ch1",true));
        ch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                editor.putBoolean("ch1", ch.isChecked());
                editor.apply();
            }
        });

        CheckBox ch2 = findViewById(R.id.checkBox2);
        ch2.setChecked(sp.getBoolean("ch2",false));
        ch2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                editor.putBoolean("ch2", ch2.isChecked());
                editor.apply();
            }
        });

        CheckBox ch3 = findViewById(R.id.checkBox3);
        ch3.setChecked(sp.getBoolean("ch3",false));
        ch3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                editor.putBoolean("ch3", ch3.isChecked());
                editor.apply();
            }
        });
    }


    public void addItem(String name)
    {
        new Thread(() -> {
            List<Dluznik> debtors = db.dluznikDao().getAllDluznici();
            Boolean existuje = false;
            for (Dluznik d : debtors) {
                if(d.getName().equals(name)){
                    existuje = true;
                    break;
                }
            }

            if(!existuje){
                Dluznik dluznik = new Dluznik(name);
                db.dluznikDao().insert(dluznik);
            }

            Boolean finalExistuje = existuje;
            runOnUiThread(() -> {
                if(!finalExistuje) {
                    displayName(name);
                } else {
                    Toast.makeText(this,"Dlužník s tímto jménem už existuje!!",Toast.LENGTH_SHORT).show();
                }
            });
        }).start();


    }

    public void displayName(String name)
    {
        list.add(name);
        lv.setAdapter(adapter);
        et.setText("");
        countNum++;
        ListView listView = findViewById(R.id.listView);
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height += (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40, getResources().getDisplayMetrics());
        listView.setLayoutParams(params);
        setCountNumColor(countNumText);
        countNumText.setText(countNum.toString());
        if (countNum == 5) {
            pridat.setEnabled(false);
            pridat.setBackgroundColor(getColor(R.color.disable));
        }
    }

    public void removeItem(int pozice)
    {
        Dialog dialog = new Dialog(Nastaveni.this);
        dialog.setContentView(R.layout.dialog_smazat_jmeno);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);
        dialog.show();
        ((TextView)dialog.findViewById(R.id.textView6)).setText("Smazat "+list.get(pozice));

        Button ano = dialog.findViewById(R.id.button3);
        Button ne = dialog.findViewById(R.id.button2);

        ano.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = list.get(pozice);
                new Thread(() -> {
                    db.dluznikDao().deleteByName(name);
                }).start();

                list.remove(pozice);
                lv.setAdapter(adapter);
                countNum--;
                setCountNumColor(countNumText);
                countNumText.setText(countNum.toString());
                pridat.setEnabled(true);
                pridat.setBackgroundColor(getColor(R.color.button));

                dialog.dismiss();
            }
        });

        ne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }

    public void setCountNumColor(TextView text){
        switch (countNum){
            case 0:
                text.setTextColor(getColor(R.color.danger));
                break;
            case 1:
            case 2:
            case 3:
            case 4:
                text.setTextColor(getColor(R.color.warning));
                break;
            case 5:
                text.setTextColor(getColor(R.color.good));
                break;
        }
    }
}