package ue.edu.co.splitbill.controller;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import ue.edu.co.splitbill.R;
import ue.edu.co.splitbill.entity.Receipt;
import ue.edu.co.splitbill.manager.SessionManager;
import ue.edu.co.splitbill.model.ReceiptRepository;
import ue.edu.co.splitbill.view.Format;
import ue.edu.co.splitbill.view.ReceiptAdapter;

// Mis recibos: lista los recibos guardados en SQLite (funciona sin internet)
public class ReceiptsActivity extends AppCompatActivity {

    private ImageButton btnBack;
    private TextView tvReceiptsSummary;
    private RecyclerView rvReceipts;
    private TextView tvEmptyReceipts;
    private Button btnAddReceipt;
    private ReceiptAdapter receiptAdapter;
    private ReceiptRepository receiptRepository;
    private SessionManager sessionManager;

    // Conecta los botones de volver y agregar recibo
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_receipts);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        initObjects();
        this.btnBack.setOnClickListener(view -> finish());
        this.btnAddReceipt.setOnClickListener(view -> startActivity(new Intent(this, ReceiptFormActivity.class)));
    }

    // Se recarga al volver, por si se agrego o edito un recibo
    @Override
    protected void onResume() {
        super.onResume();
        listReceiptsDB();
    }

    // Lee los recibos de SQLite y los muestra
    private void listReceiptsDB() {
        ArrayList<Receipt> receipts = this.receiptRepository.getActiveReceipts(this.sessionManager.getUserId());
        this.receiptAdapter.updateData(receipts);
        this.tvEmptyReceipts.setVisibility(receipts.isEmpty() ? View.VISIBLE : View.GONE);
        long total = 0;
        for (Receipt receipt : receipts) {
            total += receipt.getAmount();
        }
        this.tvReceiptsSummary.setText(getResources().getQuantityString(R.plurals.receiptsSummary,
                receipts.size(), receipts.size(), Format.money(total)));
    }

    // Abre el recibo que se toco para editarlo
    private void openReceipt(Receipt receipt) {
        Intent intent = new Intent(this, ReceiptFormActivity.class);
        intent.putExtra(ReceiptFormActivity.EXTRA_RECEIPT_ID, receipt.getId());
        startActivity(intent);
    }

    // Conecta las variables con las vistas del XML (findViewById)
    private void initObjects() {
        this.btnBack = findViewById(R.id.btnBack);
        this.tvReceiptsSummary = findViewById(R.id.tvReceiptsSummary);
        this.rvReceipts = findViewById(R.id.rvReceipts);
        this.tvEmptyReceipts = findViewById(R.id.tvEmptyReceipts);
        this.btnAddReceipt = findViewById(R.id.btnAddReceipt);
        this.receiptRepository = new ReceiptRepository(this);
        this.sessionManager = new SessionManager(this);
        this.receiptAdapter = new ReceiptAdapter(this::openReceipt);
        this.rvReceipts.setLayoutManager(new LinearLayoutManager(this));
        this.rvReceipts.setAdapter(this.receiptAdapter);
    }
}
