package ue.edu.co.splitbill.controller;

import android.os.Bundle;
import android.view.View;
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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ue.edu.co.splitbill.R;
import ue.edu.co.splitbill.entity.Settlement;
import ue.edu.co.splitbill.model.GroupRepository;
import ue.edu.co.splitbill.model.remote.RetrofitClient;
import ue.edu.co.splitbill.view.BalanceAdapter;
import ue.edu.co.splitbill.view.Format;
import ue.edu.co.splitbill.view.TransferAdapter;

// Liquidacion: la app solo muestra lo que calcula el servidor (saldos y quien le paga a quien)
public class SettlementActivity extends AppCompatActivity {

    public static final String EXTRA_GROUP_ID = "extra_group_id";
    public static final String EXTRA_GROUP_NAME = "extra_group_name";

    private ImageButton btnBack;
    private TextView tvSettleSubtitle;
    private TextView tvSettleTotal;
    private TextView tvSettleMembers;
    private TextView tvSettlePerPerson;
    private RecyclerView rvBalances;
    private RecyclerView rvTransfers;
    private TextView tvNoTransfers;
    private LinearProgressIndicator pbLoading;
    private BalanceAdapter balanceAdapter;
    private TransferAdapter transferAdapter;
    private GroupRepository groupRepository;
    private long groupId;

    // Pone el nombre del grupo y pide la liquidacion
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_settlement);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        initObjects();
        this.tvSettleSubtitle.setText(getIntent().getStringExtra(EXTRA_GROUP_NAME));
        this.btnBack.setOnClickListener(view -> finish());
        loadSettlement();
    }

    // Pide al servidor la liquidacion ya calculada
    private void loadSettlement() {
        this.pbLoading.setVisibility(View.VISIBLE);
        this.groupRepository.getSettlement(this.groupId).enqueue(new Callback<Settlement>() {
            @Override
            public void onResponse(@NonNull Call<Settlement> call, @NonNull Response<Settlement> response) {
                pbLoading.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    showSettlement(response.body());
                } else {
                    showToast(RetrofitClient.getErrorMessage(response, getString(R.string.msgLoadError)));
                }
            }

            @Override
            public void onFailure(@NonNull Call<Settlement> call, @NonNull Throwable throwable) {
                pbLoading.setVisibility(View.GONE);
                showToast(getString(R.string.msgConnectionError));
            }
        });
    }

    // Pinta el total, los saldos y las transferencias
    private void showSettlement(Settlement settlement) {
        this.tvSettleTotal.setText(Format.money(settlement.getTotal()));
        this.tvSettleMembers.setText(String.valueOf(settlement.getMemberCount()));
        this.tvSettlePerPerson.setText(Format.money(settlement.getPerPerson()));
        this.balanceAdapter.updateData(settlement.getBalances());
        this.transferAdapter.updateData(settlement.getTransfers());
        boolean noTransfers = settlement.getTransfers() == null || settlement.getTransfers().isEmpty();
        this.tvNoTransfers.setVisibility(noTransfers ? View.VISIBLE : View.GONE);
    }

    // Mensajito corto en la parte de abajo
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    // Conecta las variables con las vistas del XML (findViewById)
    private void initObjects() {
        this.btnBack = findViewById(R.id.btnBack);
        this.tvSettleSubtitle = findViewById(R.id.tvSettleSubtitle);
        this.tvSettleTotal = findViewById(R.id.tvSettleTotal);
        this.tvSettleMembers = findViewById(R.id.tvSettleMembers);
        this.tvSettlePerPerson = findViewById(R.id.tvSettlePerPerson);
        this.rvBalances = findViewById(R.id.rvBalances);
        this.rvTransfers = findViewById(R.id.rvTransfers);
        this.tvNoTransfers = findViewById(R.id.tvNoTransfers);
        this.pbLoading = findViewById(R.id.pbLoading);
        this.groupRepository = new GroupRepository();
        this.groupId = getIntent().getLongExtra(EXTRA_GROUP_ID, -1);
        this.balanceAdapter = new BalanceAdapter();
        this.transferAdapter = new TransferAdapter();
        this.rvBalances.setLayoutManager(new LinearLayoutManager(this));
        this.rvBalances.setAdapter(this.balanceAdapter);
        this.rvTransfers.setLayoutManager(new LinearLayoutManager(this));
        this.rvTransfers.setAdapter(this.transferAdapter);
    }
}
