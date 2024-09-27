package com.vomelaj.spotifycash2;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import android.content.Context;

@Database(entities = {Dluznik.class, Payment.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract DluznikDAO dluznikDao();
    public abstract PaymentDAO paymentDao();

    private static volatile AppDatabase INSTANCE;

    public static AppDatabase getInstance(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "app_database")
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            // Vytvoření nové tabulky 'payments'
            database.execSQL("CREATE TABLE IF NOT EXISTS `payments` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `dluznikId` INTEGER NOT NULL, `amount` REAL NOT NULL, `datum` TEXT, FOREIGN KEY(`dluznikId`) REFERENCES `dluznici`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        }
    };
}
