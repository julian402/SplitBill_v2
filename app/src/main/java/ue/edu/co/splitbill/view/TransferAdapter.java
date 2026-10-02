package ue.edu.co.splitbill.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import ue.edu.co.splitbill.R;
import ue.edu.co.splitbill.entity.Transfer;

// Pagos sugeridos para quedar a paz y salvo
public class TransferAdapter extends RecyclerView.Adapter<TransferAdapter.TransferViewHolder> {

    private final List<Transfer> transferList = new ArrayList<>();

    // Cambia la lista y vuelve a pintar el RecyclerView
    public void updateData(List<Transfer> newTransfers) {
        transferList.clear();
        if (newTransfers != null) {
            transferList.addAll(newTransfers);
        }
        notifyDataSetChanged();
    }

    // Infla el XML de una fila
    @NonNull
    @Override
    public TransferViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_transfer, parent, false);
        return new TransferViewHolder(view);
    }

    // Pone los datos de esa posicion en la fila
    @Override
    public void onBindViewHolder(@NonNull TransferViewHolder holder, int position) {
        holder.bind(transferList.get(position));
    }

    // Cuantas filas hay que mostrar
    @Override
    public int getItemCount() {
        return transferList.size();
    }

    // Guarda las vistas de una fila para no buscarlas cada vez
    class TransferViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvTransferNames;
        private final TextView tvTransferAmount;

        TransferViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTransferNames = itemView.findViewById(R.id.tvTransferNames);
            tvTransferAmount = itemView.findViewById(R.id.tvTransferAmount);
        }

        // Llena la fila con los datos de una transferencia
        void bind(Transfer transfer) {
            tvTransferNames.setText(itemView.getContext().getString(R.string.tvTransferNames,
                    transfer.getFromName(), transfer.getToName()));
            tvTransferAmount.setText(Format.money(transfer.getAmount()));
        }
    }
}
