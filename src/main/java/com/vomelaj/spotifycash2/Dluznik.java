package com.vomelaj.spotifycash2;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "dluznici")
public class Dluznik {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String name;

    public int konto;

    public Dluznik(String name) {
        this.name = name;
        konto = 0;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getKonto() {
        return konto;
    }

    public void addToKonto(int mnozstvy) {
        konto += mnozstvy;
    }
}
