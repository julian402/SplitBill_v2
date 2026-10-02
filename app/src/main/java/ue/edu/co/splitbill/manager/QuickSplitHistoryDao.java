package ue.edu.co.splitbill.manager;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import ue.edu.co.splitbill.entity.QuickSplitHistory;

// DAO de Room: se declaran los metodos y Room genera el codigo que ejecuta el SQL
@Dao
public interface QuickSplitHistoryDao {

    // Guarda un calculo nuevo y devuelve su id
    @Insert
    long insert(QuickSplitHistory history);

    // Los ultimos calculos del usuario, del mas nuevo al mas viejo
    @Query("SELECT * FROM quick_split_history WHERE qsh_user_id = :userId ORDER BY qsh_id DESC LIMIT :limit")
    List<QuickSplitHistory> getRecent(long userId, int limit);

    // Borra un solo calculo
    @Delete
    int delete(QuickSplitHistory history);

    // Limpia todo el historial del usuario
    @Query("DELETE FROM quick_split_history WHERE qsh_user_id = :userId")
    int deleteAll(long userId);
}
