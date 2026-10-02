package ue.edu.co.splitbill.model;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ue.edu.co.splitbill.entity.QuickSplitHistory;
import ue.edu.co.splitbill.manager.AppDatabase;
import ue.edu.co.splitbill.manager.QuickSplitHistoryDao;

/*
 * Historial de la cuenta rapida con Room.
 * Room no deja consultar en el hilo principal (congelaria la pantalla): cada operacion corre en un
 * hilo aparte (executor) y el resultado vuelve al hilo principal con un Handler.
 */
public class QuickSplitHistoryRepository {

    private static final String TAG = "QuickSplitHistoryRepo";
    private static final int HISTORY_LIMIT = 5;
    private static final ExecutorService EXECUTOR = Executors.newSingleThreadExecutor();
    private final QuickSplitHistoryDao dao;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    // Le avisa a la pantalla cuando la lista ya esta lista
    public interface OnHistoryLoaded {
        void onLoaded(List<QuickSplitHistory> history);
    }

    // Saca el DAO de la base de Room
    public QuickSplitHistoryRepository(Context context) {
        this.dao = AppDatabase.getInstance(context).quickSplitHistoryDao();
    }

    // Guarda el calculo y devuelve el historial actualizado
    public void insertAndLoad(QuickSplitHistory history, OnHistoryLoaded callback) {
        EXECUTOR.execute(() -> {
            try {
                this.dao.insert(history);
            } catch (Exception e) {
                Log.e(TAG, "ERROR AL GUARDAR EL HISTORIAL", e);
            }
            loadInBackground(history.getUserId(), callback);
        });
    }

    // Solo carga el historial, sin guardar nada
    public void getRecent(long userId, OnHistoryLoaded callback) {
        EXECUTOR.execute(() -> loadInBackground(userId, callback));
    }

    // Borra todo el historial del usuario
    public void deleteAll(long userId, OnHistoryLoaded callback) {
        EXECUTOR.execute(() -> {
            try {
                this.dao.deleteAll(userId);
            } catch (Exception e) {
                Log.e(TAG, "ERROR AL BORRAR EL HISTORIAL", e);
            }
            loadInBackground(userId, callback);
        });
    }

    // Consulta en el hilo aparte y manda la lista al hilo principal
    private void loadInBackground(long userId, OnHistoryLoaded callback) {
        List<QuickSplitHistory> history = new ArrayList<>();
        try {
            history = this.dao.getRecent(userId, HISTORY_LIMIT);
        } catch (Exception e) {
            Log.e(TAG, "ERROR AL CONSULTAR EL HISTORIAL", e);
        }
        List<QuickSplitHistory> result = history;
        this.mainHandler.post(() -> callback.onLoaded(result));
    }
}
