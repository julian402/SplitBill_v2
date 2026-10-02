package ue.edu.co.splitbill.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import ue.edu.co.splitbill.dto.GroupRequest;
import ue.edu.co.splitbill.dto.GroupResponse;
import ue.edu.co.splitbill.entity.Group;
import ue.edu.co.splitbill.entity.Member;
import ue.edu.co.splitbill.entity.User;
import ue.edu.co.splitbill.exception.ApiException;
import ue.edu.co.splitbill.repository.ExpenseRepository;
import ue.edu.co.splitbill.repository.GroupRepository;
import ue.edu.co.splitbill.repository.MemberRepository;

// Logica de los grupos (CRUD 1)
@Service
public class GroupService {

    private static final int STATUS_ACTIVE = 1;
    private static final int STATUS_INACTIVE = 0;
    private final GroupRepository groupRepository;
    private final MemberRepository memberRepository;
    private final ExpenseRepository expenseRepository;
    private final AuthService authService;

    public GroupService(GroupRepository groupRepository, MemberRepository memberRepository,
                        ExpenseRepository expenseRepository, AuthService authService) {
        this.groupRepository = groupRepository;
        this.memberRepository = memberRepository;
        this.expenseRepository = expenseRepository;
        this.authService = authService;
    }

    // Grupos del usuario que inicio sesion
    public List<GroupResponse> listByOwner(Long ownerId) {
        List<GroupResponse> groups = new ArrayList<>();
        for (Group group : this.groupRepository.findByOwnerIdAndStatusOrderByIdDesc(ownerId, STATUS_ACTIVE)) {
            groups.add(toResponse(group));
        }
        return groups;
    }

    // Un grupo por su id
    public GroupResponse get(Long groupId) {
        return toResponse(findActive(groupId));
    }

    // Quien crea el grupo entra como su primer integrante
    @Transactional
    public GroupResponse create(GroupRequest request) {
        if (request.ownerId() == null) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Falta el usuario que crea el grupo");
        }
        User owner = this.authService.findActive(request.ownerId());
        Group group = this.groupRepository.save(
                new Group(request.name().trim(), clean(request.description()), owner.getId()));
        this.memberRepository.save(new Member(group.getId(), owner.getName(), null));
        return toResponse(group);
    }

    // Cambia nombre y descripcion
    @Transactional
    public GroupResponse update(Long groupId, GroupRequest request) {
        Group group = findActive(groupId);
        group.setName(request.name().trim());
        group.setDescription(clean(request.description()));
        return toResponse(this.groupRepository.save(group));
    }

    // Borrado logico: el grupo queda con status 0 y deja de aparecer
    @Transactional
    public void delete(Long groupId) {
        Group group = findActive(groupId);
        group.setStatus(STATUS_INACTIVE);
        this.groupRepository.save(group);
    }

    // Busca el grupo o responde 404 si no existe o esta borrado
    public Group findActive(Long groupId) {
        return this.groupRepository.findByIdAndStatus(groupId, STATUS_ACTIVE)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "El grupo no existe"));
    }

    // Arma la respuesta con cuantos integrantes tiene y el total gastado
    private GroupResponse toResponse(Group group) {
        long memberCount = this.memberRepository.countByGroupIdAndStatus(group.getId(), STATUS_ACTIVE);
        long total = this.expenseRepository.sumActiveAmountByGroupId(group.getId());
        return new GroupResponse(group.getId(), group.getName(), group.getDescription(), group.getOwnerId(),
                memberCount, total);
    }

    // Si el texto viene vacio se guarda null
    private String clean(String text) {
        return text == null || text.isBlank() ? null : text.trim();
    }
}
