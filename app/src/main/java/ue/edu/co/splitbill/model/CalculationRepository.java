package ue.edu.co.splitbill.model;

import retrofit2.Call;
import ue.edu.co.splitbill.entity.QuickSplit;
import ue.edu.co.splitbill.model.remote.ApiService;
import ue.edu.co.splitbill.model.remote.RetrofitClient;

// Calculos que hace el servidor y no se guardan
public class CalculationRepository {

    private final ApiService service;

    // Toma el servicio de Retrofit que ya esta armado
    public CalculationRepository() {
        this.service = RetrofitClient.getService();
    }

    // Manda los datos y el servidor devuelve el resultado
    public Call<QuickSplit> quickSplit(long subtotal, int tipPercent, int people) {
        return this.service.quickSplit(new QuickSplit(subtotal, tipPercent, people));
    }
}
