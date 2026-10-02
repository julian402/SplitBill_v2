package ue.edu.co.splitbill.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;

import ue.edu.co.splitbill.entity.Receipt;
import ue.edu.co.splitbill.manager.ManagerDataBase;
import ue.edu.co.splitbill.manager.ReceiptContract;

// CRUD local con SQLite: los recibos solo se guardan en el celular
public class ReceiptRepository {
    private static final String TAG = "ReceiptRepository";
    private static final int STATUS_ACTIVE = 1;
    private static final int STATUS_INACTIVE = 0;
    private final ManagerDataBase managerDataBase;

    // Abre la base local
    public ReceiptRepository(Context context) {
        managerDataBase = new ManagerDataBase(context.getApplicationContext());
    }

    //metodo crud para insertar recibos
    public long insertReceipt(Receipt receipt) {
        ContentValues values = new ContentValues();
        values.put(ReceiptContract.COLUMN_USER_ID, receipt.getUserId());
        values.put(ReceiptContract.COLUMN_DESCRIPTION, receipt.getDescription());
        values.put(ReceiptContract.COLUMN_AMOUNT, receipt.getAmount());
        values.put(ReceiptContract.COLUMN_PHOTO_PATH, receipt.getPhotoPath());
        values.put(ReceiptContract.COLUMN_DATE, receipt.getDate());
        values.put(ReceiptContract.COLUMN_STATUS, STATUS_ACTIVE);
        try {
            SQLiteDatabase database = managerDataBase.getWritableDatabase();
            return database.insert(ReceiptContract.TABLE_NAME, null, values);
        } catch (Exception e) {
            Log.e(TAG, "ERROR AL REGISTRAR EL RECIBO", e);
        }
        return -1;
    }

    // Recibos activos del usuario, del mas nuevo al mas viejo
    public ArrayList<Receipt> getActiveReceipts(long userId) {
        ArrayList<Receipt> listReceipts = new ArrayList<>();
        String selection = ReceiptContract.COLUMN_USER_ID + " =? AND " + ReceiptContract.COLUMN_STATUS + " =?";
        String[] selectionArgs = {
                String.valueOf(userId), String.valueOf(STATUS_ACTIVE)
        };
        try {
            SQLiteDatabase database = managerDataBase.getReadableDatabase();
            try (Cursor cursor = database.query(ReceiptContract.TABLE_NAME,
                    null,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    ReceiptContract.COLUMN_ID + " DESC")) {
                while (cursor.moveToNext()) {
                    listReceipts.add(readReceipt(cursor));
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "ERROR AL CONSULTAR LOS RECIBOS", e);
        }
        return listReceipts;
    }

    // Busca un recibo por su id (para editarlo)
    public Receipt searchReceiptById(long id) {
        Receipt receipt = null;
        try {
            SQLiteDatabase database = managerDataBase.getReadableDatabase();
            try (Cursor cursor = database.rawQuery(ReceiptContract.SEARCH_RECEIPT,
                    new String[]{String.valueOf(id)})) {
                if (cursor.moveToFirst()) {
                    receipt = readReceipt(cursor);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "ERROR AL BUSCAR EL RECIBO", e);
        }
        return receipt;
    }

    // Actualiza descripcion, valor y foto
    public int updateReceipt(Receipt receipt) {
        ContentValues values = new ContentValues();
        values.put(ReceiptContract.COLUMN_DESCRIPTION, receipt.getDescription());
        values.put(ReceiptContract.COLUMN_AMOUNT, receipt.getAmount());
        values.put(ReceiptContract.COLUMN_PHOTO_PATH, receipt.getPhotoPath());

        String whereClause = ReceiptContract.COLUMN_ID + " =?";
        String[] whereArgs = {String.valueOf(receipt.getId())};
        try {
            SQLiteDatabase database = managerDataBase.getWritableDatabase();
            return database.update(ReceiptContract.TABLE_NAME, values, whereClause, whereArgs);
        } catch (Exception e) {
            Log.e(TAG, "ERROR AL ACTUALIZAR EL RECIBO", e);
        }
        return -1;
    }

    // Borrado logico: el registro queda con status 0
    public int deleteReceipt(long id) {
        ContentValues values = new ContentValues();
        values.put(ReceiptContract.COLUMN_STATUS, STATUS_INACTIVE);

        String whereClause = ReceiptContract.COLUMN_ID + " =?";
        String[] whereArgs = {String.valueOf(id)};
        try {
            SQLiteDatabase database = managerDataBase.getWritableDatabase();
            return database.update(ReceiptContract.TABLE_NAME, values, whereClause, whereArgs);
        } catch (Exception e) {
            Log.e(TAG, "ERROR AL ELIMINAR EL RECIBO", e);
        }
        return -1;
    }

    // Pasa la fila del Cursor a un objeto Receipt
    private Receipt readReceipt(Cursor cursor) {
        Receipt receipt = new Receipt();
        receipt.setId(cursor.getLong(cursor.getColumnIndexOrThrow(ReceiptContract.COLUMN_ID)));
        receipt.setUserId(cursor.getLong(cursor.getColumnIndexOrThrow(ReceiptContract.COLUMN_USER_ID)));
        receipt.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(ReceiptContract.COLUMN_DESCRIPTION)));
        receipt.setAmount(cursor.getLong(cursor.getColumnIndexOrThrow(ReceiptContract.COLUMN_AMOUNT)));
        receipt.setPhotoPath(cursor.getString(cursor.getColumnIndexOrThrow(ReceiptContract.COLUMN_PHOTO_PATH)));
        receipt.setDate(cursor.getString(cursor.getColumnIndexOrThrow(ReceiptContract.COLUMN_DATE)));
        receipt.setStatus((byte) cursor.getInt(cursor.getColumnIndexOrThrow(ReceiptContract.COLUMN_STATUS)));
        return receipt;
    }
}
