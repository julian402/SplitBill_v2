package ue.edu.co.splitbill.controller;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

import com.google.android.material.progressindicator.LinearProgressIndicator;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ue.edu.co.splitbill.R;
import ue.edu.co.splitbill.entity.QuickSplit;
import ue.edu.co.splitbill.entity.QuickSplitHistory;
import ue.edu.co.splitbill.manager.SessionManager;
import ue.edu.co.splitbill.model.CalculationRepository;
import ue.edu.co.splitbill.model.QuickSplitHistoryRepository;
import ue.edu.co.splitbill.model.remote.RetrofitClient;
import ue.edu.co.splitbill.view.Format;
import ue.edu.co.splitbill.view.HistoryAdapter;

// Cuenta rapida: dividir una cuenta con propina sin crear un grupo. El calculo lo hace el servidor
// y cada resultado queda en el historial local, guardado con Room
public class QuickSplitActivity extends AppCompatActivity {

    private static final int MAX_PEOPLE = 50;
    private ImageButton btnBack;
    private EditText etSubtotal;
    private EditText etTipPercent;
    private EditText etPeople;
    private Button btnCalculate;
    private LinearLayout cardResult;
    private TextView tvQuickPerPerson;
    private TextView tvQuickTip;
    private TextView tvQuickTotal;
    private TextView tvQuickRemainder;
    private LinearProgressIndicator pbLoading;
    private TextView tvClearHistory;
    private TextView tvEmptyHistory;
    private RecyclerView rvHistory;
    private HistoryAdapter historyAdapter;
    private CalculationRepository calculationRepository;
    private QuickSplitHistoryRepository historyRepository;
    private SessionManager sessionManager;
    private long subtotal;
    private int tipPercent;
    private int people;

    // Conecta los botones y carga el historial guardado con Room
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_quick_split);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        initObjects();
        this.btnBack.setOnClickListener(view -> finish());
        this.btnCalculate.setOnClickListener(this::calculate);
        this.tvClearHistory.setOnClickListener(this::clearHistory);
        this.historyRepository.getRecent(this.sessionManager.getUserId(), this::showHistory);
    }

    // Manda los datos al servidor para que haga la cuenta
    private void calculate(View view) {
        if (!getData()) {
            return;
        }
        showLoading(true);
        this.calculationRepository.quickSplit(this.subtotal, this.tipPercent, this.people).enqueue(new Callback<QuickSplit>() {
            @Override
            public void onResponse(@NonNull Call<QuickSplit> call, @NonNull Response<QuickSplit> response) {
                showLoading(false);
                if (response.isSuccessful() && response.body() != null) {
                    showResult(response.body());
                    saveHistory(response.body());
                } else {
                    showToast(RetrofitClient.getErrorMessage(response, getString(R.string.msgCalculateError)));
                }
            }

            @Override
            public void onFailure(@NonNull Call<QuickSplit> call, @NonNull Throwable throwable) {
                showLoading(false);
                showToast(getString(R.string.msgConnectionError));
            }
        });
    }

    // Muestra el resultado que devolvio el servidor
    private void showResult(QuickSplit result) {
        this.cardResult.setVisibility(View.VISIBLE);
        this.tvQuickPerPerson.setText(Format.money(result.getPerPerson()));
        this.tvQuickTip.setText(Format.money(result.getTip()));
        this.tvQuickTotal.setText(Format.money(result.getTotal()));
        // Si no da exacto, los primeros pagan 1 peso mas
        if (result.getRemainder() != null && result.getRemainder() > 0) {
            this.tvQuickRemainder.setVisibility(View.VISIBLE);
            this.tvQuickRemainder.setText(getString(R.string.tvQuickRemainder,
                    result.getRemainder(), Format.money(result.getPerPerson() + 1)));
        } else {
            this.tvQuickRemainder.setVisibility(View.GONE);
        }
    }

    // Room: guarda el calculo en el celular y vuelve a mostrar los ultimos
    private void saveHistory(QuickSplit result) {
        String date = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.ROOT).format(new Date());
        QuickSplitHistory history = new QuickSplitHistory(this.sessionManager.getUserId(), result.getSubtotal(),
                this.tipPercent, this.people, result.getTotal(), result.getPerPerson(), date);
        this.historyRepository.insertAndLoad(history, this::showHistory);
    }

    // Borra el historial de Room
    private void clearHistory(View view) {
        this.historyRepository.deleteAll(this.sessionManager.getUserId(), this::showHistory);
    }

    // Pinta el historial en la lista
    private void showHistory(List<QuickSplitHistory> history) {
        this.historyAdapter.updateData(history);
        this.tvEmptyHistory.setVisibility(history.isEmpty() ? View.VISIBLE : View.GONE);
        this.tvClearHistory.setVisibility(history.isEmpty() ? View.GONE : View.VISIBLE);
    }

    //metodo para capturar la data de la pantalla y validarla
    private boolean getData() {
        this.subtotal = parseNumber(this.etSubtotal);
        this.tipPercent = (int) parseNumber(this.etTipPercent);
        this.people = (int) parseNumber(this.etPeople);
        if (this.subtotal <= 0) {
            this.etSubtotal.setError(getString(R.string.errAmount));
            return false;
        }
        if (this.tipPercent < 0 || this.tipPercent > 100) {
            this.etTipPercent.setError(getString(R.string.errTip));
            return false;
        }
        if (this.people < 1 || this.people > MAX_PEOPLE) {
            this.etPeople.setError(getString(R.string.errPeople));
            return false;
        }
        return true;
    }

    // Un campo vacio o invalido cuenta como -1 para que la validacion lo rechace
    private long parseNumber(EditText editText) {
        try {
            return Long.parseLong(editText.getText().toString().trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    // Muestra la ruedita de carga y bloquea el boton mientras responde el servidor
    private void showLoading(boolean loading) {
        this.pbLoading.setVisibility(loading ? View.VISIBLE : View.GONE);
        this.btnCalculate.setEnabled(!loading);
    }

    // Mensajito corto en la parte de abajo
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    // Conecta las variables con las vistas del XML (findViewById)
    private void initObjects() {
        this.btnBack = findViewById(R.id.btnBack);
        this.etSubtotal = findViewById(R.id.etSubtotal);
        this.etTipPercent = findViewById(R.id.etTipPercent);
        this.etPeople = findViewById(R.id.etPeople);
        this.btnCalculate = findViewById(R.id.btnCalculate);
        this.cardResult = findViewById(R.id.cardResult);
        this.tvQuickPerPerson = findViewById(R.id.tvQuickPerPerson);
        this.tvQuickTip = findViewById(R.id.tvQuickTip);
        this.tvQuickTotal = findViewById(R.id.tvQuickTotal);
        this.tvQuickRemainder = findViewById(R.id.tvQuickRemainder);
        this.pbLoading = findViewById(R.id.pbLoading);
        this.tvClearHistory = findViewById(R.id.tvClearHistory);
        this.tvEmptyHistory = findViewById(R.id.tvEmptyHistory);
        this.rvHistory = findViewById(R.id.rvHistory);
        this.calculationRepository = new CalculationRepository();
        this.historyRepository = new QuickSplitHistoryRepository(this);
        this.sessionManager = new SessionManager(this);
        this.historyAdapter = new HistoryAdapter();
        this.rvHistory.setLayoutManager(new LinearLayoutManager(this));
        this.rvHistory.setAdapter(this.historyAdapter);
    }
}
