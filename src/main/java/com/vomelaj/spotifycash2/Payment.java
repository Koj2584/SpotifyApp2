package com.vomelaj.spotifycash2;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.ForeignKey;

@Entity(tableName = "payments",
        foreignKeys = @ForeignKey(entity = Dluznik.class,
                parentColumns = "id",
                childColumns = "dluznikId",
                onDelete = ForeignKey.CASCADE))

public class Payment {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public int dluznikId;

    public int amount;
    public String datum;

    public String poznamka;

    // Konstruktor
    public Payment(int dluznikId, int amount, String datum) {
        this.dluznikId = dluznikId;
        this.amount = amount;
        this.datum = datum;
        poznamka = "-";
    }

    // Gettery a Settery
    public int getId() {
        return id;
    }

    public int getDebtorId() {
        return dluznikId;
    }

    public double getAmount() {
        return amount;
    }

    public String getDueDate() {
        return datum;
    }
}
