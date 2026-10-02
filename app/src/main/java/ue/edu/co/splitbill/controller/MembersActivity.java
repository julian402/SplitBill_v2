package ue.edu.co.splitbill.controller;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.progressindicator.LinearProgressIndicator;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ue.edu.co.splitbill.R;
import ue.edu.co.splitbill.entity.ApiMessage;
import ue.edu.co.splitbill.entity.Member;
import ue.edu.co.splitbill.model.MemberRepository;
import ue.edu.co.splitbill.model.remote.RetrofitClient;
import ue.edu.co.splitbill.view.MemberAdapter;

/*
 * Integrantes de un grupo (CRUD 2 contra la API).
 * Recurso del dispositivo: CONTACTOS. Se pide el permiso y se elige a alguien de la agenda.
 */
public class MembersActivity extends AppCompatActivity {

    public static final String EXTRA_GROUP_ID = "extra_group_id";
    private static final String TAG = "MembersActivity";
    private static final int REQUEST_CODE_CONTACTS = 100;

    private ImageButton btnBack;
    private EditText etMemberName;
    private EditText etMemberPhone;
    private Button btnSaveMember;
    private Button btnCancelEdit;
    private Button btnFromContacts;
    private RecyclerView rvMembers;
    private LinearProgressIndicator pbLoading;
    private MemberAdapter memberAdapter;
    private MemberRepository memberRepository;
    private long groupId;
    // Integrante que se esta editando; null cuando el formulario es para agregar uno nuevo
    private Member editingMember;
    private Member member;

    // Conecta los botones y carga los integrantes
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_members);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        initObjects();
        this.btnBack.setOnClickListener(view -> finish());
        this.btnSaveMember.setOnClickListener(this::saveMember);
        this.btnCancelEdit.setOnClickListener(view -> clearFields());
        this.btnFromContacts.setOnClickListener(this::pickFromContacts);
        loadMembers();
    }

    // Pide al servidor los integrantes del grupo
    private void loadMembers() {
        this.pbLoading.setVisibility(View.VISIBLE);
        this.memberRepository.getMembers(this.groupId).enqueue(new Callback<List<Member>>() {
            @Override
            public void onResponse(@NonNull Call<List<Member>> call, @NonNull Response<List<Member>> response) {
                pbLoading.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    memberAdapter.updateData(response.body());
                } else {
                    showToast(RetrofitClient.getErrorMessage(response, getString(R.string.msgLoadError)));
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Member>> call, @NonNull Throwable throwable) {
                pbLoading.setVisibility(View.GONE);
                showToast(getString(R.string.msgConnectionError));
            }
        });
    }

    // Agrega uno nuevo o actualiza el que se esta editando
    private void saveMember(View view) {
        if (!getData()) {
            return;
        }
        showLoading(true);
        Call<Member> call = this.editingMember == null
                ? this.memberRepository.createMember(this.groupId, this.member)
                : this.memberRepository.updateMember(this.editingMember.getId(), this.member);
        call.enqueue(new Callback<Member>() {
            @Override
            public void onResponse(@NonNull Call<Member> call, @NonNull Response<Member> response) {
                showLoading(false);
                if (response.isSuccessful()) {
                    showToast(getString(editingMember == null ? R.string.msgMemberAdded : R.string.msgMemberUpdated));
                    clearFields();
                    loadMembers();
                } else {
                    showToast(RetrofitClient.getErrorMessage(response, getString(R.string.msgSaveError)));
                }
            }

            @Override
            public void onFailure(@NonNull Call<Member> call, @NonNull Throwable throwable) {
                showLoading(false);
                showToast(getString(R.string.msgConnectionError));
            }
        });
    }

    // Tocar un integrante lo carga en el formulario para editarlo
    private void editMember(Member member) {
        this.editingMember = member;
        this.etMemberName.setText(member.getName());
        this.etMemberPhone.setText(member.getPhone());
        this.btnSaveMember.setText(R.string.btnUpdate);
        this.btnCancelEdit.setVisibility(View.VISIBLE);
        this.etMemberName.requestFocus();
    }

    // Pregunta antes de eliminar
    private void confirmDelete(Member member) {
        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.dlgDeleteMemberTitle)
                .setMessage(getString(R.string.dlgDeleteMemberMessage, member.getName()))
                .setNegativeButton(R.string.btnCancel, null)
                .setPositiveButton(R.string.btnDelete, (dialog, which) -> deleteMember(member))
                .show();
    }

    // Elimina el integrante en el servidor
    private void deleteMember(Member member) {
        showLoading(true);
        this.memberRepository.deleteMember(member.getId()).enqueue(new Callback<ApiMessage>() {
            @Override
            public void onResponse(@NonNull Call<ApiMessage> call, @NonNull Response<ApiMessage> response) {
                showLoading(false);
                if (response.isSuccessful()) {
                    showToast(getString(R.string.msgMemberDeleted));
                    clearFields();
                    loadMembers();
                } else {
                    // Por ejemplo: no se puede eliminar a quien pago gastos
                    showToast(RetrofitClient.getErrorMessage(response, getString(R.string.msgDeleteError)));
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiMessage> call, @NonNull Throwable throwable) {
                showLoading(false);
                showToast(getString(R.string.msgConnectionError));
            }
        });
    }

    // ---------------- Contactos ----------------

    // Si ya hay permiso de contactos los muestra; si no, lo pide
    private void pickFromContacts(View view) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
                == PackageManager.PERMISSION_GRANTED) {
            showContacts();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_CONTACTS}, REQUEST_CODE_CONTACTS);
        }
    }

    //Respuesta del usuario al permiso
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_CONTACTS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showContacts();
            } else {
                showToast(getString(R.string.msgContactsDenied));
            }
        }
    }

    // Lee nombre y telefono de la agenda y los muestra en una lista para escoger
    private void showContacts() {
        List<String> names = new ArrayList<>();
        List<String> phones = new ArrayList<>();
        String[] projection = {
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER
        };
        try (Cursor cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                projection, null, null,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC")) {
            while (cursor != null && cursor.moveToNext()) {
                String name = cursor.getString(0);
                // Un contacto con varios numeros sale una sola vez
                if (name != null && !names.contains(name)) {
                    names.add(name);
                    phones.add(cursor.getString(1));
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "ERROR AL LEER LOS CONTACTOS", e);
        }
        if (names.isEmpty()) {
            showToast(getString(R.string.msgNoContacts));
            return;
        }
        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.dlgPickContact)
                .setItems(names.toArray(new String[0]), (dialog, which) -> {
                    this.etMemberName.setText(names.get(which));
                    this.etMemberPhone.setText(phones.get(which));
                })
                .setNegativeButton(R.string.btnCancel, null)
                .show();
    }

    // ---------------- Formulario ----------------

    //metodo para capturar la data de la pantalla y validarla
    private boolean getData() {
        String name = this.etMemberName.getText().toString().trim();
        String phone = this.etMemberPhone.getText().toString().trim();
        if (name.isEmpty()) {
            this.etMemberName.setError(getString(R.string.errMemberName));
            return false;
        }
        this.member = new Member(name, phone);
        return true;
    }

    // Limpia el formulario y vuelve al modo agregar
    private void clearFields() {
        this.editingMember = null;
        this.etMemberName.setText("");
        this.etMemberPhone.setText("");
        this.etMemberName.setError(null);
        this.btnSaveMember.setText(R.string.btnAddMember);
        this.btnCancelEdit.setVisibility(View.GONE);
    }

    // Muestra la ruedita de carga y bloquea el boton mientras responde el servidor
    private void showLoading(boolean loading) {
        this.pbLoading.setVisibility(loading ? View.VISIBLE : View.GONE);
        this.btnSaveMember.setEnabled(!loading);
    }

    // Mensajito corto en la parte de abajo
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    // Conecta las variables con las vistas del XML (findViewById)
    private void initObjects() {
        this.btnBack = findViewById(R.id.btnBack);
        this.etMemberName = findViewById(R.id.etMemberName);
        this.etMemberPhone = findViewById(R.id.etMemberPhone);
        this.btnSaveMember = findViewById(R.id.btnSaveMember);
        this.btnCancelEdit = findViewById(R.id.btnCancelEdit);
        this.btnFromContacts = findViewById(R.id.btnFromContacts);
        this.rvMembers = findViewById(R.id.rvMembers);
        this.pbLoading = findViewById(R.id.pbLoading);
        this.memberRepository = new MemberRepository();
        this.groupId = getIntent().getLongExtra(EXTRA_GROUP_ID, -1);
        this.memberAdapter = new MemberAdapter(new MemberAdapter.OnMemberListener() {
            @Override
            public void onMemberClick(Member member) {
                editMember(member);
            }

            @Override
            public void onMemberDelete(Member member) {
                confirmDelete(member);
            }
        });
        this.rvMembers.setLayoutManager(new LinearLayoutManager(this));
        this.rvMembers.setAdapter(this.memberAdapter);
    }
}
