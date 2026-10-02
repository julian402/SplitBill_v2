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
import ue.edu.co.splitbill.entity.Expense;

// Llena la lista de gastos de un grupo
public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder> {

    // Lo implementa la Activity para saber que gasto se toco
    public interface OnExpenseClickListener {
        void onExpenseClick(Expense expense);
    }

    private final List<Expense> expenseList = new ArrayList<>();
    private final OnExpenseClickListener listener;

    public ExpenseAdapter(OnExpenseClickListener listener) {
        this.listener = listener;
    }

    // Cambia la lista y vuelve a pintar el RecyclerView
    public void updateData(List<Expense> newExpenses) {
        expenseList.clear();
        if (newExpenses != null) {
            expenseList.addAll(newExpenses);
        }
        notifyDataSetChanged();
    }

    // Infla el XML de una fila
    @NonNull
    @Override
    public ExpenseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_expense, parent, false);
        return new ExpenseViewHolder(view);
    }

    // Pone los datos de esa posicion en la fila
    @Override
    public void onBindViewHolder(@NonNull ExpenseViewHolder holder, int position) {
        holder.bind(expenseList.get(position));
    }

    // Cuantas filas hay que mostrar
    @Override
    public int getItemCount() {
        return expenseList.size();
    }

    // Guarda las vistas de una fila para no buscarlas cada vez
    class ExpenseViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvExpenseDescription;
        private final TextView tvExpensePayer;
        private final TextView tvExpenseAmount;
        private final TextView tvExpensePerPerson;

        ExpenseViewHolder(@NonNull View itemView) {
            super(itemView);
            tvExpenseDescription = itemView.findViewById(R.id.tvExpenseDescription);
            tvExpensePayer = itemView.findViewById(R.id.tvExpensePayer);
            tvExpenseAmount = itemView.findViewById(R.id.tvExpenseAmount);
            tvExpensePerPerson = itemView.findViewById(R.id.tvExpensePerPerson);
        }

        // Llena la fila con los datos de un gasto
        void bind(Expense expense) {
            tvExpenseDescription.setText(expense.getDescription());
            tvExpensePayer.setText(itemView.getContext().getString(R.string.tvPaidBy,
                    expense.getPayerName(), expense.getDate()));
            tvExpenseAmount.setText(Format.money(expense.getAmount()));
            tvExpensePerPerson.setText(itemView.getContext().getString(R.string.tvPerPersonShort,
                    Format.money(expense.getPerPerson())));
            itemView.setOnClickListener(view -> listener.onExpenseClick(expense));
        }
    }
}
