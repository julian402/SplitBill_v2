package ue.edu.co.splitbill.manager;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import ue.edu.co.splitbill.entity.QuickSplitHistory;

/*
 * Base de datos de Room. Es un archivo distinto al de ManagerDataBase (SQLite a mano):
 * los recibos van por SQLite directo y el historial de la cuenta rapida por Room.
 */
@Database(entities = {QuickSplitHistory.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static final String DATABASE_NAME = "splitbill_room.db";
    private static AppDatabase instance;

    // Room implementa este metodo y nos entrega el DAO
    public abstract QuickSplitHistoryDao quickSplitHistoryDao();

    // Una sola instancia para toda la app
    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, DATABASE_NAME)
                    .build();
        }
        return instance;
    }
}
