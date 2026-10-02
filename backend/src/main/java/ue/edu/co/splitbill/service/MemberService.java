package ue.edu.co.splitbill.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import ue.edu.co.splitbill.dto.MemberRequest;
import ue.edu.co.splitbill.dto.MemberResponse;
import ue.edu.co.splitbill.entity.Member;
import ue.edu.co.splitbill.exception.ApiException;
import ue.edu.co.splitbill.repository.ExpenseRepository;
import ue.edu.co.splitbill.repository.MemberRepository;

// Logica de los integrantes (CRUD 2)
@Service
public class MemberService {

    private static final int STATUS_ACTIVE = 1;
    private static final int STATUS_INACTIVE = 0;
    private final MemberRepository memberRepository;
    private final ExpenseRepository expenseRepository;
    private final GroupService groupService;

    public MemberService(MemberRepository memberRepository, ExpenseRepository expenseRepository,
                         GroupService groupService) {
        this.memberRepository = memberRepository;
        this.expenseRepository = expenseRepository;
        this.groupService = groupService;
    }

    // Integrantes activos del grupo
    public List<MemberResponse> listByGroup(Long groupId) {
        this.groupService.findActive(groupId);
        List<MemberResponse> members = new ArrayList<>();
        for (Member member : this.memberRepository.findByGroupIdAndStatusOrderByIdAsc(groupId, STATUS_ACTIVE)) {
            members.add(toResponse(member));
        }
        return members;
    }

    // Agrega un integrante (el telefono puede venir de los contactos del celular)
    @Transactional
    public MemberResponse create(Long groupId, MemberRequest request) {
        this.groupService.findActive(groupId);
        Member member = new Member(groupId, request.name().trim(), clean(request.phone()));
        return toResponse(this.memberRepository.save(member));
    }

    // Edita nombre y telefono
    @Transactional
    public MemberResponse update(Long memberId, MemberRequest request) {
        Member member = findActive(memberId);
        member.setName(request.name().trim());
        member.setPhone(clean(request.phone()));
        return toResponse(this.memberRepository.save(member));
    }

    // No se puede retirar a quien pago gastos: la liquidacion quedaria sin cuadrar
    @Transactional
    public void delete(Long memberId) {
        Member member = findActive(memberId);
        if (this.expenseRepository.existsByPayerIdAndStatus(memberId, STATUS_ACTIVE)) {
            throw new ApiException(HttpStatus.CONFLICT,
                    "No se puede eliminar a " + member.getName() + " porque pagó gastos del grupo");
        }
        member.setStatus(STATUS_INACTIVE);
        this.memberRepository.save(member);
    }

    // Busca el integrante o responde 404
    public Member findActive(Long memberId) {
        return this.memberRepository.findByIdAndStatus(memberId, STATUS_ACTIVE)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "El integrante no existe"));
    }

    // Pasa la entidad al DTO
    private MemberResponse toResponse(Member member) {
        return new MemberResponse(member.getId(), member.getGroupId(), member.getName(), member.getPhone());
    }

    // Si el texto viene vacio se guarda null
    private String clean(String text) {
        return text == null || text.isBlank() ? null : text.trim();
    }
}
