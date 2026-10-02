package ue.edu.co.splitbill.controller;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.progressindicator.LinearProgressIndicator;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ue.edu.co.splitbill.R;
import ue.edu.co.splitbill.entity.Group;
import ue.edu.co.splitbill.manager.SessionManager;
import ue.edu.co.splitbill.model.GroupRepository;
import ue.edu.co.splitbill.model.remote.RetrofitClient;
import ue.edu.co.splitbill.view.Format;
import ue.edu.co.splitbill.view.GroupAdapter;

// Inicio: resumen, accesos a cuenta rapida y recibos, y la lista de grupos del usuario
public class HomeActivity extends AppCompatActivity {

    private TextView tvGreeting;
    private ImageButton btnLogout;
    private TextView tvHomeTotal;
    private TextView tvHomeCounts;
    private LinearLayout cardQuickSplit;
    private LinearLayout cardReceipts;
    private RecyclerView rvGroups;
    private TextView tvEmptyGroups;
    private MaterialButton btnNewGroup;
    private LinearProgressIndicator pbLoading;
    private GroupAdapter groupAdapter;
    private GroupRepository groupRepository;
    private SessionManager sessionManager;

    // Saludo con el nombre del usuario y los accesos a las demas pantallas
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        initObjects();
        this.tvGreeting.setText(getString(R.string.tvGreeting, this.sessionManager.getUserName()));
        this.btnLogout.setOnClickListener(this::confirmLogout);
        this.btnNewGroup.setOnClickListener(view -> startActivity(new Intent(this, GroupFormActivity.class)));
        this.cardQuickSplit.setOnClickListener(view -> startActivity(new Intent(this, QuickSplitActivity.class)));
        this.cardReceipts.setOnClickListener(view -> startActivity(new Intent(this, ReceiptsActivity.class)));
    }

    // Se recarga cada vez que se vuelve a esta pantalla, por si se creo o cambio un grupo
    @Override
    protected void onResume() {
        super.onResume();
        loadGroups();
    }

    // Pide al servidor los grupos del usuario
    private void loadGroups() {
        this.pbLoading.setVisibility(View.VISIBLE);
        this.groupRepository.getGroups(this.sessionManager.getUserId()).enqueue(new Callback<List<Group>>() {
            @Override
            public void onResponse(@NonNull Call<List<Group>> call, @NonNull Response<List<Group>> response) {
                pbLoading.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    showGroups(response.body());
                } else {
                    showToast(RetrofitClient.getErrorMessage(response, getString(R.string.msgLoadError)));
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Group>> call, @NonNull Throwable throwable) {
                pbLoading.setVisibility(View.GONE);
                showToast(getString(R.string.msgConnectionError));
            }
        });
    }

    // Pinta la lista y el resumen de arriba
    private void showGroups(List<Group> groups) {
        this.groupAdapter.updateData(groups);
        this.tvEmptyGroups.setVisibility(groups.isEmpty() ? View.VISIBLE : View.GONE);
        // El total de cada grupo ya viene calculado por el servidor; aqui solo se suman para el resumen
        long total = 0;
        for (Group group : groups) {
            total += group.getTotal() == null ? 0 : group.getTotal();
        }
        this.tvHomeTotal.setText(Format.money(total));
        this.tvHomeCounts.setText(getResources().getQuantityString(R.plurals.groupsCount, groups.size(), groups.size()));
    }

    // Abre el detalle del grupo que se toco
    private void openGroup(Group group) {
        Intent intent = new Intent(this, GroupDetailActivity.class);
        intent.putExtra(GroupDetailActivity.EXTRA_GROUP_ID, group.getId());
        startActivity(intent);
    }

    // Pregunta antes de cerrar sesion
    private void confirmLogout(View view) {
        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.dlgLogoutTitle)
                .setMessage(R.string.dlgLogoutMessage)
                .setNegativeButton(R.string.btnCancel, null)
                .setPositiveButton(R.string.btnLogout, (dialog, which) -> logout())
                .show();
    }

    // Borra la sesion y vuelve al login
    private void logout() {
        this.sessionManager.logout();
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    // Mensajito corto en la parte de abajo
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    // Conecta las variables con las vistas del XML (findViewById)
    private void initObjects() {
        this.tvGreeting = findViewById(R.id.tvGreeting);
        this.btnLogout = findViewById(R.id.btnLogout);
        this.tvHomeTotal = findViewById(R.id.tvHomeTotal);
        this.tvHomeCounts = findViewById(R.id.tvHomeCounts);
        this.cardQuickSplit = findViewById(R.id.cardQuickSplit);
        this.cardReceipts = findViewById(R.id.cardReceipts);
        this.rvGroups = findViewById(R.id.rvGroups);
        this.tvEmptyGroups = findViewById(R.id.tvEmptyGroups);
        this.btnNewGroup = findViewById(R.id.btnNewGroup);
        this.pbLoading = findViewById(R.id.pbLoading);
        this.groupRepository = new GroupRepository();
        this.sessionManager = new SessionManager(this);
        this.groupAdapter = new GroupAdapter(this::openGroup);
        this.rvGroups.setLayoutManager(new LinearLayoutManager(this));
        this.rvGroups.setAdapter(this.groupAdapter);
    }
}
