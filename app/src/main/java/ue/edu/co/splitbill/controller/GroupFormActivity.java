package ue.edu.co.splitbill.controller;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.progressindicator.LinearProgressIndicator;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ue.edu.co.splitbill.R;
import ue.edu.co.splitbill.entity.ApiMessage;
import ue.edu.co.splitbill.entity.Group;
import ue.edu.co.splitbill.manager.SessionManager;
import ue.edu.co.splitbill.model.GroupRepository;
import ue.edu.co.splitbill.model.remote.RetrofitClient;

// Crear un grupo, o editarlo y eliminarlo si llega EXTRA_GROUP_ID
public class GroupFormActivity extends AppCompatActivity {

    public static final String EXTRA_GROUP_ID = "extra_group_id";
    public static final String EXTRA_GROUP_NAME = "extra_group_name";
    public static final String EXTRA_GROUP_DESCRIPTION = "extra_group_description";
    private static final long NO_GROUP = -1;

    private ImageButton btnBack;
    private TextView tvFormTitle;
    private EditText etGroupName;
    private EditText etGroupDescription;
    private Button btnSaveGroup;
    private Button btnDeleteGroup;
    private LinearProgressIndicator pbLoading;
    private GroupRepository groupRepository;
    private SessionManager sessionManager;
    private long groupId;
    private Group group;

    // Abre la pantalla en modo edicion con los datos actuales del grupo
    public static Intent editIntent(Context context, Group group) {
        Intent intent = new Intent(context, GroupFormActivity.class);
        intent.putExtra(EXTRA_GROUP_ID, group.getId());
        intent.putExtra(EXTRA_GROUP_NAME, group.getName());
        intent.putExtra(EXTRA_GROUP_DESCRIPTION, group.getDescription());
        return intent;
    }

    // Si es edicion llena los campos con los datos que llegaron en el Intent
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_group_form);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        initObjects();
        if (isEditing()) {
            this.tvFormTitle.setText(R.string.tvTitleEditGroup);
            this.etGroupName.setText(getIntent().getStringExtra(EXTRA_GROUP_NAME));
            this.etGroupDescription.setText(getIntent().getStringExtra(EXTRA_GROUP_DESCRIPTION));
            this.btnDeleteGroup.setVisibility(View.VISIBLE);
        }
        this.btnBack.setOnClickListener(view -> finish());
        this.btnSaveGroup.setOnClickListener(this::saveGroup);
        this.btnDeleteGroup.setOnClickListener(this::confirmDelete);
    }

    // Si llego un id es porque se esta editando
    private boolean isEditing() {
        return this.groupId != NO_GROUP;
    }

    // Crea o actualiza el grupo en el servidor
    private void saveGroup(View view) {
        if (!getData()) {
            return;
        }
        showLoading(true);
        Call<Group> call = isEditing()
                ? this.groupRepository.updateGroup(this.groupId, this.group)
                : this.groupRepository.createGroup(this.group);
        call.enqueue(new Callback<Group>() {
            @Override
            public void onResponse(@NonNull Call<Group> call, @NonNull Response<Group> response) {
                showLoading(false);
                if (response.isSuccessful() && response.body() != null) {
                    if (isEditing()) {
                        showToast(getString(R.string.msgGroupUpdated));
                    } else {
                        // Grupo nuevo: se abre su detalle para empezar a agregar integrantes y gastos
                        showToast(getString(R.string.msgGroupCreated));
                        Intent intent = new Intent(GroupFormActivity.this, GroupDetailActivity.class);
                        intent.putExtra(GroupDetailActivity.EXTRA_GROUP_ID, response.body().getId());
                        startActivity(intent);
                    }
                    finish();
                } else {
                    showToast(RetrofitClient.getErrorMessage(response, getString(R.string.msgSaveError)));
                }
            }

            @Override
            public void onFailure(@NonNull Call<Group> call, @NonNull Throwable throwable) {
                showLoading(false);
                showToast(getString(R.string.msgConnectionError));
            }
        });
    }

    // Pregunta antes de eliminar
    private void confirmDelete(View view) {
        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.dlgDeleteGroupTitle)
                .setMessage(R.string.dlgDeleteGroupMessage)
                .setNegativeButton(R.string.btnCancel, null)
                .setPositiveButton(R.string.btnDelete, (dialog, which) -> deleteGroup())
                .show();
    }

    // Elimina el grupo en el servidor
    private void deleteGroup() {
        showLoading(true);
        this.groupRepository.deleteGroup(this.groupId).enqueue(new Callback<ApiMessage>() {
            @Override
            public void onResponse(@NonNull Call<ApiMessage> call, @NonNull Response<ApiMessage> response) {
                showLoading(false);
                if (response.isSuccessful()) {
                    showToast(getString(R.string.msgGroupDeleted));
                    // Vuelve al inicio cerrando el detalle del grupo que ya no existe
                    Intent intent = new Intent(GroupFormActivity.this, HomeActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                    finish();
                } else {
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

    //metodo para capturar la data de la pantalla y validarla
    private boolean getData() {
        String name = this.etGroupName.getText().toString().trim();
        String description = this.etGroupDescription.getText().toString().trim();
        if (name.isEmpty()) {
            this.etGroupName.setError(getString(R.string.errGroupName));
            return false;
        }
        this.group = new Group(name, description, this.sessionManager.getUserId());
        return true;
    }

    // Muestra la ruedita de carga y bloquea los botones mientras responde el servidor
    private void showLoading(boolean loading) {
        this.pbLoading.setVisibility(loading ? View.VISIBLE : View.GONE);
        this.btnSaveGroup.setEnabled(!loading);
        this.btnDeleteGroup.setEnabled(!loading);
    }

    // Mensajito corto en la parte de abajo
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    // Conecta las variables con las vistas del XML (findViewById)
    private void initObjects() {
        this.btnBack = findViewById(R.id.btnBack);
        this.tvFormTitle = findViewById(R.id.tvFormTitle);
        this.etGroupName = findViewById(R.id.etGroupName);
        this.etGroupDescription = findViewById(R.id.etGroupDescription);
        this.btnSaveGroup = findViewById(R.id.btnSaveGroup);
        this.btnDeleteGroup = findViewById(R.id.btnDeleteGroup);
        this.pbLoading = findViewById(R.id.pbLoading);
        this.groupRepository = new GroupRepository();
        this.sessionManager = new SessionManager(this);
        this.groupId = getIntent().getLongExtra(EXTRA_GROUP_ID, NO_GROUP);
    }
}
