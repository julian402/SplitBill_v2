package ue.edu.co.splitbill.model;

import java.util.List;

import retrofit2.Call;
import ue.edu.co.splitbill.entity.ApiMessage;
import ue.edu.co.splitbill.entity.Expense;
import ue.edu.co.splitbill.model.remote.ApiService;
import ue.edu.co.splitbill.model.remote.RetrofitClient;

// CRUD de gastos contra la API
public class ExpenseRepository {

    private final ApiService service;

    // Toma el servicio de Retrofit que ya esta armado
    public ExpenseRepository() {
        this.service = RetrofitClient.getService();
    }

    // Gastos del grupo
    public Call<List<Expense>> getExpenses(long groupId) {
        return this.service.getExpenses(groupId);
    }

    // Registrar gasto
    public Call<Expense> createExpense(long groupId, Expense expense) {
        return this.service.createExpense(groupId, expense);
    }

    // Editar gasto
    public Call<Expense> updateExpense(long expenseId, Expense expense) {
        return this.service.updateExpense(expenseId, expense);
    }

    // Eliminar gasto
    public Call<ApiMessage> deleteExpense(long expenseId) {
        return this.service.deleteExpense(expenseId);
    }
}
