package ue.edu.co.splitbill.manager;

// Nombres de la tabla de recibos y sus consultas, en un solo lugar
public final class ReceiptContract {
    private ReceiptContract() {
        //impide crear objetos de esta clase
    }

    public static final String TABLE_NAME = "receipts";
    public static final String COLUMN_ID = "rec_id";
    public static final String COLUMN_USER_ID = "rec_user_id";
    public static final String COLUMN_DESCRIPTION = "rec_description";
    public static final String COLUMN_AMOUNT = "rec_amount";
    public static final String COLUMN_PHOTO_PATH = "rec_photo_path";
    public static final String COLUMN_DATE = "rec_date";
    public static final String COLUMN_STATUS = "rec_status";

    // SQL para crear la tabla; el CHECK hace que status solo pueda ser 0 o 1
    public static final String CREATE_TABLE_RECEIPT = "CREATE TABLE " + TABLE_NAME +
            " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_USER_ID + " INTEGER NOT NULL, " +
            COLUMN_DESCRIPTION + " TEXT NOT NULL, " +
            COLUMN_AMOUNT + " INTEGER NOT NULL, " +
            COLUMN_PHOTO_PATH + " TEXT, " +
            COLUMN_DATE + " TEXT NOT NULL, " +
            COLUMN_STATUS + " INTEGER NOT NULL DEFAULT 1 " +
            "CHECK (" + COLUMN_STATUS + " IN (0,1)))";

    // Para borrar la tabla en onUpgrade
    public static final String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;

    // Busca un recibo activo por su id
    public static final String SEARCH_RECEIPT = "SELECT * FROM " + TABLE_NAME +
            " WHERE " + COLUMN_ID + " =? AND " + COLUMN_STATUS + " = 1";
}
