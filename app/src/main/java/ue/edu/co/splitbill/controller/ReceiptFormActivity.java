package ue.edu.co.splitbill.controller;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import ue.edu.co.splitbill.R;
import ue.edu.co.splitbill.entity.Receipt;
import ue.edu.co.splitbill.manager.SessionManager;
import ue.edu.co.splitbill.model.ReceiptRepository;
import ue.edu.co.splitbill.view.PhotoLoader;

/*
 * Crear, editar o eliminar un recibo (CRUD local en SQLite).
 * Recurso del dispositivo: CAMARA. Se pide el permiso y la foto se guarda como archivo dentro de la app.
 */
public class ReceiptFormActivity extends AppCompatActivity {

    public static final String EXTRA_RECEIPT_ID = "extra_receipt_id";
    private static final String TAG = "ReceiptFormActivity";
    private static final int REQUEST_CODE_CAMERA = 200;
    private static final long NO_RECEIPT = -1;
    private static final int PHOTO_SIZE = 900;

    private ImageButton btnBack;
    private TextView tvFormTitle;
    private ImageView ivPhoto;
    private TextView tvPhotoHint;
    private Button btnTakePhoto;
    private EditText etDescription;
    private EditText etAmount;
    private Button btnSaveReceipt;
    private Button btnDeleteReceipt;
    private ReceiptRepository receiptRepository;
    private SessionManager sessionManager;
    private ActivityResultLauncher<Uri> takePictureLauncher;
    private long receiptId;
    private Receipt receipt;
    // Foto que se muestra y se guardara; pendingPhotoPath es la que la camara esta tomando
    private String photoPath;
    private String pendingPhotoPath;

    // Si es edicion busca el recibo en SQLite y llena los campos
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_receipt_form);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        initObjects();
        if (isEditing()) {
            searchReceiptDB();
        }
        this.btnBack.setOnClickListener(view -> finish());
        this.btnTakePhoto.setOnClickListener(this::takePhoto);
        this.btnSaveReceipt.setOnClickListener(this::saveReceiptDB);
        this.btnDeleteReceipt.setOnClickListener(this::confirmDelete);
    }

    // Si llego un id es porque se esta editando
    private boolean isEditing() {
        return this.receiptId != NO_RECEIPT;
    }

    // Busca el recibo en SQLite y pone sus datos en pantalla
    private void searchReceiptDB() {
        Receipt found = this.receiptRepository.searchReceiptById(this.receiptId);
        if (found == null) {
            showToast(getString(R.string.msgReceiptNotFound));
            finish();
            return;
        }
        this.tvFormTitle.setText(R.string.tvTitleEditReceipt);
        this.etDescription.setText(found.getDescription());
        this.etAmount.setText(String.valueOf(found.getAmount()));
        this.photoPath = found.getPhotoPath();
        showPhoto();
        this.btnDeleteReceipt.setVisibility(View.VISIBLE);
    }

    // ---------------- Camara ----------------

    // Si ya hay permiso de camara la abre; si no, lo pide
    private void takePhoto(View view) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            openCamera();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA}, REQUEST_CODE_CAMERA);
        }
    }

    //Respuesta del usuario al permiso
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_CAMERA) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                showToast(getString(R.string.msgCameraDenied));
            }
        }
    }

    // Crea el archivo donde la camara va a escribir la foto y abre la camara
    private void openCamera() {
        try {
            File folder = new File(getFilesDir(), "receipts");
            if (!folder.exists() && !folder.mkdirs()) {
                showToast(getString(R.string.msgPhotoError));
                return;
            }
            File photoFile = new File(folder, "receipt_" + System.currentTimeMillis() + ".jpg");
            this.pendingPhotoPath = photoFile.getAbsolutePath();
            // La camara es otra app: FileProvider le da permiso de escribir solo en este archivo
            Uri photoUri = FileProvider.getUriForFile(this, getPackageName() + ".fileprovider", photoFile);
            this.takePictureLauncher.launch(photoUri);
        } catch (Exception e) {
            Log.e(TAG, "ERROR AL ABRIR LA CAMARA", e);
            showToast(getString(R.string.msgPhotoError));
        }
    }

    // Aqui llega la respuesta de la camara
    private void onPhotoTaken(Boolean success) {
        if (Boolean.TRUE.equals(success)) {
            this.photoPath = this.pendingPhotoPath;
            showPhoto();
        } else if (this.pendingPhotoPath != null) {
            // Se cancelo la foto: se borra el archivo vacio
            File unused = new File(this.pendingPhotoPath);
            if (unused.exists() && !unused.delete()) {
                Log.w(TAG, "No se pudo borrar " + this.pendingPhotoPath);
            }
        }
        this.pendingPhotoPath = null;
    }

    // Muestra la foto reducida en el formulario
    private void showPhoto() {
        Bitmap photo = PhotoLoader.load(this.photoPath, PHOTO_SIZE);
        if (photo != null) {
            this.ivPhoto.setImageBitmap(photo);
            // Quita el color lila del icono para que la foto se vea con sus colores
            this.ivPhoto.setImageTintList(null);
            this.ivPhoto.setScaleType(ImageView.ScaleType.CENTER_CROP);
            this.ivPhoto.setPadding(0, 0, 0, 0);
            this.tvPhotoHint.setVisibility(View.GONE);
            this.btnTakePhoto.setText(R.string.btnRetakePhoto);
        }
    }

    // ---------------- CRUD en SQLite ----------------

    // Inserta o actualiza el recibo en SQLite
    private void saveReceiptDB(View view) {
        if (!getData()) {
            return;
        }
        if (isEditing()) {
            this.receipt.setId(this.receiptId);
            int rowsAffected = this.receiptRepository.updateReceipt(this.receipt);
            if (rowsAffected > 0) {
                showToast(getString(R.string.msgReceiptUpdated));
                finish();
            } else {
                showToast(getString(R.string.msgSaveError));
            }
        } else {
            long response = this.receiptRepository.insertReceipt(this.receipt);
            if (response > 0) {
                showToast(getString(R.string.msgReceiptSaved));
                finish();
            } else {
                showToast(getString(R.string.msgSaveError));
            }
        }
    }

    // Pregunta antes de eliminar
    private void confirmDelete(View view) {
        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.dlgDeleteReceiptTitle)
                .setMessage(R.string.dlgDeleteReceiptMessage)
                .setNegativeButton(R.string.btnCancel, null)
                .setPositiveButton(R.string.btnDelete, (dialog, which) -> deleteReceiptDB())
                .show();
    }

    // Borrado logico del recibo en SQLite
    private void deleteReceiptDB() {
        int rowsAffected = this.receiptRepository.deleteReceipt(this.receiptId);
        if (rowsAffected > 0) {
            showToast(getString(R.string.msgReceiptDeleted));
            finish();
        } else {
            showToast(getString(R.string.msgDeleteError));
        }
    }

    //metodo para capturar la data de la pantalla y validarla
    private boolean getData() {
        String description = this.etDescription.getText().toString().trim();
        long amount;
        try {
            amount = Long.parseLong(this.etAmount.getText().toString().trim());
        } catch (NumberFormatException e) {
            amount = 0;
        }
        if (description.isEmpty()) {
            this.etDescription.setError(getString(R.string.errDescription));
            return false;
        }
        if (amount <= 0) {
            this.etAmount.setError(getString(R.string.errAmount));
            return false;
        }
        String date = new SimpleDateFormat("yyyy-MM-dd", Locale.ROOT).format(new Date());
        this.receipt = new Receipt(this.sessionManager.getUserId(), description, amount, this.photoPath, date);
        return true;
    }

    // Mensajito corto en la parte de abajo
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    // Conecta las variables con las vistas del XML (findViewById)
    private void initObjects() {
        this.btnBack = findViewById(R.id.btnBack);
        this.tvFormTitle = findViewById(R.id.tvFormTitle);
        this.ivPhoto = findViewById(R.id.ivPhoto);
        this.tvPhotoHint = findViewById(R.id.tvPhotoHint);
        this.btnTakePhoto = findViewById(R.id.btnTakePhoto);
        this.etDescription = findViewById(R.id.etDescription);
        this.etAmount = findViewById(R.id.etAmount);
        this.btnSaveReceipt = findViewById(R.id.btnSaveReceipt);
        this.btnDeleteReceipt = findViewById(R.id.btnDeleteReceipt);
        this.receiptRepository = new ReceiptRepository(this);
        this.sessionManager = new SessionManager(this);
        this.receiptId = getIntent().getLongExtra(EXTRA_RECEIPT_ID, NO_RECEIPT);
        // Abre la camara del celular y avisa si la foto quedo guardada
        this.takePictureLauncher = registerForActivityResult(new ActivityResultContracts.TakePicture(),
                this::onPhotoTaken);
    }
}
