package ue.edu.co.splitbill.view;

import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import ue.edu.co.splitbill.R;
import ue.edu.co.splitbill.entity.Receipt;

// Lista de recibos guardados en SQLite, con la miniatura de la foto
public class ReceiptAdapter extends RecyclerView.Adapter<ReceiptAdapter.ReceiptViewHolder> {

    // Lo implementa la Activity para saber que recibo se toco
    public interface OnReceiptClickListener {
        void onReceiptClick(Receipt receipt);
    }

    private final List<Receipt> receiptList = new ArrayList<>();
    private final OnReceiptClickListener listener;

    public ReceiptAdapter(OnReceiptClickListener listener) {
        this.listener = listener;
    }

    // Cambia la lista y vuelve a pintar el RecyclerView
    public void updateData(List<Receipt> newReceipts) {
        receiptList.clear();
        if (newReceipts != null) {
            receiptList.addAll(newReceipts);
        }
        notifyDataSetChanged();
    }

    // Infla el XML de una fila
    @NonNull
    @Override
    public ReceiptViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_receipt, parent, false);
        return new ReceiptViewHolder(view);
    }

    // Pone los datos de esa posicion en la fila
    @Override
    public void onBindViewHolder(@NonNull ReceiptViewHolder holder, int position) {
        holder.bind(receiptList.get(position));
    }

    // Cuantas filas hay que mostrar
    @Override
    public int getItemCount() {
        return receiptList.size();
    }

    // Guarda las vistas de una fila para no buscarlas cada vez
    class ReceiptViewHolder extends RecyclerView.ViewHolder {
        private final ImageView ivReceiptPhoto;
        private final TextView tvReceiptDescription;
        private final TextView tvReceiptDate;
        private final TextView tvReceiptAmount;

        ReceiptViewHolder(@NonNull View itemView) {
            super(itemView);
            ivReceiptPhoto = itemView.findViewById(R.id.ivReceiptPhoto);
            tvReceiptDescription = itemView.findViewById(R.id.tvReceiptDescription);
            tvReceiptDate = itemView.findViewById(R.id.tvReceiptDate);
            tvReceiptAmount = itemView.findViewById(R.id.tvReceiptAmount);
        }

        // Llena la fila con los datos de un recibo
        void bind(Receipt receipt) {
            tvReceiptDescription.setText(receipt.getDescription());
            tvReceiptDate.setText(receipt.getDate());
            tvReceiptAmount.setText(Format.money(receipt.getAmount()));
            Bitmap photo = PhotoLoader.load(receipt.getPhotoPath(), 160);
            if (photo != null) {
                // Foto real: sin relleno ni color encima
                ivReceiptPhoto.setImageBitmap(photo);
                ivReceiptPhoto.setImageTintList(null);
                ivReceiptPhoto.setPadding(0, 0, 0, 0);
            } else {
                // Sin foto: icono lila con margen
                int padding = (int) (14 * itemView.getResources().getDisplayMetrics().density);
                ivReceiptPhoto.setImageResource(R.drawable.ic_receipt);
                ivReceiptPhoto.setImageTintList(ColorStateList.valueOf(
                        ContextCompat.getColor(itemView.getContext(), R.color.colorPrimary)));
                ivReceiptPhoto.setPadding(padding, padding, padding, padding);
            }
            itemView.setOnClickListener(view -> listener.onReceiptClick(receipt));
        }
    }
}
