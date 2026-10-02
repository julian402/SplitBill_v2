package ue.edu.co.splitbill.model;

import java.util.List;

import retrofit2.Call;
import ue.edu.co.splitbill.entity.ApiMessage;
import ue.edu.co.splitbill.entity.Group;
import ue.edu.co.splitbill.entity.Settlement;
import ue.edu.co.splitbill.model.remote.ApiService;
import ue.edu.co.splitbill.model.remote.RetrofitClient;

// CRUD de grupos contra la API, mas la liquidacion de cada grupo
public class GroupRepository {

    private final ApiService service;

    // Toma el servicio de Retrofit que ya esta armado
    public GroupRepository() {
        this.service = RetrofitClient.getService();
    }

    // Grupos del usuario
    public Call<List<Group>> getGroups(long userId) {
        return this.service.getGroups(userId);
    }

    // Un solo grupo
    public Call<Group> getGroup(long groupId) {
        return this.service.getGroup(groupId);
    }

    // Crear grupo
    public Call<Group> createGroup(Group group) {
        return this.service.createGroup(group);
    }

    // Editar grupo
    public Call<Group> updateGroup(long groupId, Group group) {
        return this.service.updateGroup(groupId, group);
    }

    // Eliminar grupo
    public Call<ApiMessage> deleteGroup(long groupId) {
        return this.service.deleteGroup(groupId);
    }

    // Saldos y transferencias calculados en el servidor
    public Call<Settlement> getSettlement(long groupId) {
        return this.service.getSettlement(groupId);
    }
}
