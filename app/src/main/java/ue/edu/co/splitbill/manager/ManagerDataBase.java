package ue.edu.co.splitbill.manager;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

// Base de datos local (SQLite) del celular. Solo guarda los recibos; lo demas vive en el servidor
public class ManagerDataBase extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "splitbill.db";
    private static final int DATABASE_VERSION = 1;

    // Crea o abre el archivo splitbill.db
    public ManagerDataBase(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Solo corre la primera vez: crea la tabla de recibos
    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(ReceiptContract.CREATE_TABLE_RECEIPT);
    }

    // Si sube la version se borra la tabla y se crea otra vez
    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        database.execSQL(ReceiptContract.DROP_TABLE);
        onCreate(database);
    }
}
