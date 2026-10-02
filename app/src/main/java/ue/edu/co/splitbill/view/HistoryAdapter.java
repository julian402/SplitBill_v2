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
import ue.edu.co.splitbill.entity.QuickSplitHistory;

// Ultimos calculos de la cuenta rapida (guardados con Room)
public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {

    private final List<QuickSplitHistory> historyList = new ArrayList<>();

    // Cambia la lista y vuelve a pintar el RecyclerView
    public void updateData(List<QuickSplitHistory> newHistory) {
        historyList.clear();
        if (newHistory != null) {
            historyList.addAll(newHistory);
        }
        notifyDataSetChanged();
    }

    // Infla el XML de una fila
    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history, parent, false);
        return new HistoryViewHolder(view);
    }

    // Pone los datos de esa posicion en la fila
    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        holder.bind(historyList.get(position));
    }

    // Cuantas filas hay que mostrar
    @Override
    public int getItemCount() {
        return historyList.size();
    }

    // Guarda las vistas de una fila para no buscarlas cada vez
    class HistoryViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvHistoryDetail;
        private final TextView tvHistoryDate;
        private final TextView tvHistoryPerPerson;

        HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvHistoryDetail = itemView.findViewById(R.id.tvHistoryDetail);
            tvHistoryDate = itemView.findViewById(R.id.tvHistoryDate);
            tvHistoryPerPerson = itemView.findViewById(R.id.tvHistoryPerPerson);
        }

        // Llena la fila con los datos de un calculo guardado
        void bind(QuickSplitHistory history) {
            tvHistoryDetail.setText(itemView.getContext().getString(R.string.tvHistoryDetail,
                    Format.money(history.getSubtotal()), history.getTipPercent(), history.getPeople()));
            tvHistoryDate.setText(history.getDate());
            tvHistoryPerPerson.setText(itemView.getContext().getString(R.string.tvPerPersonShort,
                    Format.money(history.getPerPerson())));
        }
    }
}
