package ue.edu.co.splitbill.model;

import java.util.List;

import retrofit2.Call;
import ue.edu.co.splitbill.entity.ApiMessage;
import ue.edu.co.splitbill.entity.Member;
import ue.edu.co.splitbill.model.remote.ApiService;
import ue.edu.co.splitbill.model.remote.RetrofitClient;

// CRUD de integrantes contra la API
public class MemberRepository {

    private final ApiService service;

    // Toma el servicio de Retrofit que ya esta armado
    public MemberRepository() {
        this.service = RetrofitClient.getService();
    }

    // Integrantes del grupo
    public Call<List<Member>> getMembers(long groupId) {
        return this.service.getMembers(groupId);
    }

    // Agregar integrante
    public Call<Member> createMember(long groupId, Member member) {
        return this.service.createMember(groupId, member);
    }

    // Editar integrante
    public Call<Member> updateMember(long memberId, Member member) {
        return this.service.updateMember(memberId, member);
    }

    // Eliminar integrante
    public Call<ApiMessage> deleteMember(long memberId) {
        return this.service.deleteMember(memberId);
    }
}
