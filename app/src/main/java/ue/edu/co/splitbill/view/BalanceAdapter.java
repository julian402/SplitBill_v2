package ue.edu.co.splitbill.view;

import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import ue.edu.co.splitbill.R;
import ue.edu.co.splitbill.entity.Balance;

// Saldo de cada integrante en la liquidacion: verde si le deben, rojo si debe
public class BalanceAdapter extends RecyclerView.Adapter<BalanceAdapter.BalanceViewHolder> {

    private final List<Balance> balanceList = new ArrayList<>();

    // Cambia la lista y vuelve a pintar el RecyclerView
    public void updateData(List<Balance> newBalances) {
        balanceList.clear();
        if (newBalances != null) {
            balanceList.addAll(newBalances);
        }
        notifyDataSetChanged();
    }

    // Infla el XML de una fila
    @NonNull
    @Override
    public BalanceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_balance, parent, false);
        return new BalanceViewHolder(view);
    }

    // Pone los datos de esa posicion en la fila
    @Override
    public void onBindViewHolder(@NonNull BalanceViewHolder holder, int position) {
        holder.bind(balanceList.get(position));
    }

    // Cuantas filas hay que mostrar
    @Override
    public int getItemCount() {
        return balanceList.size();
    }

    // Guarda las vistas de una fila para no buscarlas cada vez
    class BalanceViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvBalanceAvatar;
        private final TextView tvBalanceName;
        private final TextView tvBalanceDetail;
        private final TextView tvBalanceStatus;

        BalanceViewHolder(@NonNull View itemView) {
            super(itemView);
            tvBalanceAvatar = itemView.findViewById(R.id.tvBalanceAvatar);
            tvBalanceName = itemView.findViewById(R.id.tvBalanceName);
            tvBalanceDetail = itemView.findViewById(R.id.tvBalanceDetail);
            tvBalanceStatus = itemView.findViewById(R.id.tvBalanceStatus);
        }

        // Llena la fila con los datos de un saldo
        void bind(Balance balance) {
            Format.avatar(itemView.getContext(), tvBalanceAvatar, balance.getName());
            tvBalanceName.setText(balance.getName());
            tvBalanceDetail.setText(itemView.getContext().getString(R.string.tvBalanceDetail,
                    Format.money(balance.getPaid()), Format.money(balance.getShare())));
            int background;
            int textColor;
            String status;
            if (balance.getBalance() > 0) {
                background = R.color.colorCreditorContainer;
                textColor = R.color.colorCreditor;
                status = itemView.getContext().getString(R.string.tvOwedToYou, Format.money(balance.getBalance()));
            } else if (balance.getBalance() < 0) {
                background = R.color.colorDebtorContainer;
                textColor = R.color.colorDebtor;
                status = itemView.getContext().getString(R.string.tvOwes, Format.money(-balance.getBalance()));
            } else {
                background = R.color.colorSettledContainer;
                textColor = R.color.colorTextPrimary;
                status = itemView.getContext().getString(R.string.tvSettled);
            }
            tvBalanceStatus.setText(status);
            tvBalanceStatus.setBackgroundTintList(ColorStateList.valueOf(
                    ContextCompat.getColor(itemView.getContext(), background)));
            tvBalanceStatus.setTextColor(ContextCompat.getColor(itemView.getContext(), textColor));
        }
    }
}
