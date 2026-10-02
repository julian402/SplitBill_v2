package ue.edu.co.splitbill.controller;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
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

import com.google.android.material.progressindicator.LinearProgressIndicator;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ue.edu.co.splitbill.R;
import ue.edu.co.splitbill.entity.Expense;
import ue.edu.co.splitbill.entity.Group;
import ue.edu.co.splitbill.model.ExpenseRepository;
import ue.edu.co.splitbill.model.GroupRepository;
import ue.edu.co.splitbill.model.remote.RetrofitClient;
import ue.edu.co.splitbill.view.ExpenseAdapter;
import ue.edu.co.splitbill.view.Format;

// Detalle de un grupo: total, accesos a integrantes y liquidacion, y la lista de gastos
public class GroupDetailActivity extends AppCompatActivity {

    public static final String EXTRA_GROUP_ID = "extra_group_id";

    private ImageButton btnBack;
    private ImageButton btnEditGroup;
    private TextView tvGroupTitle;
    private TextView tvGroupDescription;
    private TextView tvGroupTotal;
    private TextView tvGroupMembers;
    private Button btnMembers;
    private Button btnSettle;
    private RecyclerView rvExpenses;
    private TextView tvEmptyExpenses;
    private Button btnAddExpense;
    private LinearProgressIndicator pbLoading;
    private ExpenseAdapter expenseAdapter;
    private GroupRepository groupRepository;
    private ExpenseRepository expenseRepository;
    private long groupId;
    private Group group;

    // Conecta los botones; los datos se cargan en onResume
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_group_detail);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        initObjects();
        this.btnBack.setOnClickListener(view -> finish());
        this.btnEditGroup.setOnClickListener(this::editGroup);
        this.btnMembers.setOnClickListener(this::openMembers);
        this.btnSettle.setOnClickListener(this::openSettlement);
        this.btnAddExpense.setOnClickListener(view -> openExpenseForm(null));
    }

    // Al volver de agregar gastos o integrantes se recargan los datos
    @Override
    protected void onResume() {
        super.onResume();
        loadGroup();
        loadExpenses();
    }

    // Pide al servidor los datos del grupo (nombre, total, integrantes)
    private void loadGroup() {
        this.groupRepository.getGroup(this.groupId).enqueue(new Callback<Group>() {
            @Override
            public void onResponse(@NonNull Call<Group> call, @NonNull Response<Group> response) {
                if (response.isSuccessful() && response.body() != null) {
                    showGroup(response.body());
                } else {
                    showToast(RetrofitClient.getErrorMessage(response, getString(R.string.msgLoadError)));
                    finish();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Group> call, @NonNull Throwable throwable) {
                showToast(getString(R.string.msgConnectionError));
            }
        });
    }

    // Pide al servidor la lista de gastos
    private void loadExpenses() {
        this.pbLoading.setVisibility(View.VISIBLE);
        this.expenseRepository.getExpenses(this.groupId).enqueue(new Callback<List<Expense>>() {
            @Override
            public void onResponse(@NonNull Call<List<Expense>> call, @NonNull Response<List<Expense>> response) {
                pbLoading.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    expenseAdapter.updateData(response.body());
                    tvEmptyExpenses.setVisibility(response.body().isEmpty() ? View.VISIBLE : View.GONE);
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Expense>> call, @NonNull Throwable throwable) {
                pbLoading.setVisibility(View.GONE);
            }
        });
    }

    // Pone nombre, descripcion y total en la tarjeta de arriba
    private void showGroup(Group group) {
        this.group = group;
        this.tvGroupTitle.setText(group.getName());
        boolean hasDescription = group.getDescription() != null && !group.getDescription().isEmpty();
        this.tvGroupDescription.setText(hasDescription ? group.getDescription() : getString(R.string.tvNoDescription));
        this.tvGroupTotal.setText(Format.money(group.getTotal() == null ? 0 : group.getTotal()));
        int members = group.getMemberCount() == null ? 0 : group.getMemberCount().intValue();
        this.tvGroupMembers.setText(getResources().getQuantityString(R.plurals.membersCount, members, members));
    }

    // Abre el formulario del grupo en modo edicion
    private void editGroup(View view) {
        if (this.group != null) {
            startActivity(GroupFormActivity.editIntent(this, this.group));
        }
    }

    // Abre la pantalla de integrantes
    private void openMembers(View view) {
        Intent intent = new Intent(this, MembersActivity.class);
        intent.putExtra(MembersActivity.EXTRA_GROUP_ID, this.groupId);
        startActivity(intent);
    }

    // Abre la liquidacion del grupo
    private void openSettlement(View view) {
        Intent intent = new Intent(this, SettlementActivity.class);
        intent.putExtra(SettlementActivity.EXTRA_GROUP_ID, this.groupId);
        intent.putExtra(SettlementActivity.EXTRA_GROUP_NAME, this.group != null ? this.group.getName() : "");
        startActivity(intent);
    }

    // expense en null = gasto nuevo; si no, se abre para editarlo
    private void openExpenseForm(Expense expense) {
        Intent intent = new Intent(this, ExpenseFormActivity.class);
        intent.putExtra(ExpenseFormActivity.EXTRA_GROUP_ID, this.groupId);
        if (expense != null) {
            intent.putExtra(ExpenseFormActivity.EXTRA_EXPENSE_ID, expense.getId());
            intent.putExtra(ExpenseFormActivity.EXTRA_DESCRIPTION, expense.getDescription());
            intent.putExtra(ExpenseFormActivity.EXTRA_AMOUNT, expense.getAmount());
            intent.putExtra(ExpenseFormActivity.EXTRA_PAYER_ID, expense.getPayerId());
        }
        startActivity(intent);
    }

    // Mensajito corto en la parte de abajo
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    // Conecta las variables con las vistas del XML (findViewById)
    private void initObjects() {
        this.btnBack = findViewById(R.id.btnBack);
        this.btnEditGroup = findViewById(R.id.btnEditGroup);
        this.tvGroupTitle = findViewById(R.id.tvGroupTitle);
        this.tvGroupDescription = findViewById(R.id.tvGroupDescription);
        this.tvGroupTotal = findViewById(R.id.tvGroupTotal);
        this.tvGroupMembers = findViewById(R.id.tvGroupMembers);
        this.btnMembers = findViewById(R.id.btnMembers);
        this.btnSettle = findViewById(R.id.btnSettle);
        this.rvExpenses = findViewById(R.id.rvExpenses);
        this.tvEmptyExpenses = findViewById(R.id.tvEmptyExpenses);
        this.btnAddExpense = findViewById(R.id.btnAddExpense);
        this.pbLoading = findViewById(R.id.pbLoading);
        this.groupRepository = new GroupRepository();
        this.expenseRepository = new ExpenseRepository();
        this.groupId = getIntent().getLongExtra(EXTRA_GROUP_ID, -1);
        this.expenseAdapter = new ExpenseAdapter(this::openExpenseForm);
        this.rvExpenses.setLayoutManager(new LinearLayoutManager(this));
        this.rvExpenses.setAdapter(this.expenseAdapter);
    }
}
