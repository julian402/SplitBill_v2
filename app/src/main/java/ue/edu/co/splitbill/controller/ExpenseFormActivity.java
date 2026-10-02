package ue.edu.co.splitbill.controller;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
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

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ue.edu.co.splitbill.R;
import ue.edu.co.splitbill.entity.ApiMessage;
import ue.edu.co.splitbill.entity.Expense;
import ue.edu.co.splitbill.entity.Member;
import ue.edu.co.splitbill.model.ExpenseRepository;
import ue.edu.co.splitbill.model.MemberRepository;
import ue.edu.co.splitbill.model.remote.RetrofitClient;

// Registrar un gasto (CRUD 3), o editarlo y eliminarlo si llega EXTRA_EXPENSE_ID
public class ExpenseFormActivity extends AppCompatActivity {

    public static final String EXTRA_GROUP_ID = "extra_group_id";
    public static final String EXTRA_EXPENSE_ID = "extra_expense_id";
    public static final String EXTRA_DESCRIPTION = "extra_description";
    public static final String EXTRA_AMOUNT = "extra_amount";
    public static final String EXTRA_PAYER_ID = "extra_payer_id";
    private static final long NO_EXPENSE = -1;

    private ImageButton btnBack;
    private TextView tvFormTitle;
    private EditText etDescription;
    private EditText etAmount;
    private Spinner spPayer;
    private TextView tvSplitInfo;
    private Button btnSaveExpense;
    private Button btnDeleteExpense;
    private LinearProgressIndicator pbLoading;
    private ExpenseRepository expenseRepository;
    private MemberRepository memberRepository;
    private final List<Member> members = new ArrayList<>();
    private long groupId;
    private long expenseId;
    private Expense expense;

    // Si es edicion llena los campos con los datos que llegaron en el Intent
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_expense_form);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        initObjects();
        if (isEditing()) {
            this.tvFormTitle.setText(R.string.tvTitleEditExpense);
            this.etDescription.setText(getIntent().getStringExtra(EXTRA_DESCRIPTION));
            this.etAmount.setText(String.valueOf(getIntent().getLongExtra(EXTRA_AMOUNT, 0)));
            this.btnDeleteExpense.setVisibility(View.VISIBLE);
        }
        this.btnBack.setOnClickListener(view -> finish());
        this.btnSaveExpense.setOnClickListener(this::saveExpense);
        this.btnDeleteExpense.setOnClickListener(this::confirmDelete);
        loadMembers();
    }

    // Si llego un id es porque se esta editando
    private boolean isEditing() {
        return this.expenseId != NO_EXPENSE;
    }

    // Los integrantes llenan la lista de "Quien pago"
    private void loadMembers() {
        showLoading(true);
        this.memberRepository.getMembers(this.groupId).enqueue(new Callback<List<Member>>() {
            @Override
            public void onResponse(@NonNull Call<List<Member>> call, @NonNull Response<List<Member>> response) {
                showLoading(false);
                if (response.isSuccessful() && response.body() != null) {
                    showMembers(response.body());
                } else {
                    showToast(RetrofitClient.getErrorMessage(response, getString(R.string.msgLoadError)));
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Member>> call, @NonNull Throwable throwable) {
                showLoading(false);
                showToast(getString(R.string.msgConnectionError));
            }
        });
    }

    // Llena el Spinner con los nombres y deja seleccionado a quien pago
    private void showMembers(List<Member> memberList) {
        this.members.clear();
        this.members.addAll(memberList);
        List<String> names = new ArrayList<>();
        int selected = 0;
        long payerId = getIntent().getLongExtra(EXTRA_PAYER_ID, -1);
        for (int i = 0; i < memberList.size(); i++) {
            names.add(memberList.get(i).getName());
            if (memberList.get(i).getId() == payerId) {
                selected = i;
            }
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.item_spinner, names);
        adapter.setDropDownViewResource(R.layout.item_spinner_dropdown);
        this.spPayer.setAdapter(adapter);
        this.spPayer.setSelection(selected);
        this.tvSplitInfo.setText(getResources().getQuantityString(R.plurals.splitInfo,
                memberList.size(), memberList.size()));
    }

    // Crea o actualiza el gasto en el servidor
    private void saveExpense(View view) {
        if (!getData()) {
            return;
        }
        showLoading(true);
        Call<Expense> call = isEditing()
                ? this.expenseRepository.updateExpense(this.expenseId, this.expense)
                : this.expenseRepository.createExpense(this.groupId, this.expense);
        call.enqueue(new Callback<Expense>() {
            @Override
            public void onResponse(@NonNull Call<Expense> call, @NonNull Response<Expense> response) {
                showLoading(false);
                if (response.isSuccessful()) {
                    showToast(getString(isEditing() ? R.string.msgExpenseUpdated : R.string.msgExpenseAdded));
                    finish();
                } else {
                    showToast(RetrofitClient.getErrorMessage(response, getString(R.string.msgSaveError)));
                }
            }

            @Override
            public void onFailure(@NonNull Call<Expense> call, @NonNull Throwable throwable) {
                showLoading(false);
                showToast(getString(R.string.msgConnectionError));
            }
        });
    }

    // Pregunta antes de eliminar
    private void confirmDelete(View view) {
        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.dlgDeleteExpenseTitle)
                .setMessage(R.string.dlgDeleteExpenseMessage)
                .setNegativeButton(R.string.btnCancel, null)
                .setPositiveButton(R.string.btnDelete, (dialog, which) -> deleteExpense())
                .show();
    }

    // Elimina el gasto en el servidor
    private void deleteExpense() {
        showLoading(true);
        this.expenseRepository.deleteExpense(this.expenseId).enqueue(new Callback<ApiMessage>() {
            @Override
            public void onResponse(@NonNull Call<ApiMessage> call, @NonNull Response<ApiMessage> response) {
                showLoading(false);
                if (response.isSuccessful()) {
                    showToast(getString(R.string.msgExpenseDeleted));
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
        String description = this.etDescription.getText().toString().trim();
        String amountText = this.etAmount.getText().toString().trim();
        if (description.isEmpty()) {
            this.etDescription.setError(getString(R.string.errDescription));
            return false;
        }
        long amount;
        try {
            amount = Long.parseLong(amountText);
        } catch (NumberFormatException e) {
            amount = 0;
        }
        if (amount <= 0) {
            this.etAmount.setError(getString(R.string.errAmount));
            return false;
        }
        int position = this.spPayer.getSelectedItemPosition();
        if (position < 0 || position >= this.members.size()) {
            showToast(getString(R.string.errPayer));
            return false;
        }
        this.expense = new Expense(description, amount, this.members.get(position).getId());
        return true;
    }

    // Muestra la ruedita de carga y bloquea los botones mientras responde el servidor
    private void showLoading(boolean loading) {
        this.pbLoading.setVisibility(loading ? View.VISIBLE : View.GONE);
        this.btnSaveExpense.setEnabled(!loading);
        this.btnDeleteExpense.setEnabled(!loading);
    }

    // Mensajito corto en la parte de abajo
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    // Conecta las variables con las vistas del XML (findViewById)
    private void initObjects() {
        this.btnBack = findViewById(R.id.btnBack);
        this.tvFormTitle = findViewById(R.id.tvFormTitle);
        this.etDescription = findViewById(R.id.etDescription);
        this.etAmount = findViewById(R.id.etAmount);
        this.spPayer = findViewById(R.id.spPayer);
        this.tvSplitInfo = findViewById(R.id.tvSplitInfo);
        this.btnSaveExpense = findViewById(R.id.btnSaveExpense);
        this.btnDeleteExpense = findViewById(R.id.btnDeleteExpense);
        this.pbLoading = findViewById(R.id.pbLoading);
        this.expenseRepository = new ExpenseRepository();
        this.memberRepository = new MemberRepository();
        this.groupId = getIntent().getLongExtra(EXTRA_GROUP_ID, -1);
        this.expenseId = getIntent().getLongExtra(EXTRA_EXPENSE_ID, NO_EXPENSE);
    }
}
