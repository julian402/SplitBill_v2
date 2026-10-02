package ue.edu.co.splitbill.controller;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import ue.edu.co.splitbill.dto.GroupRequest;
import ue.edu.co.splitbill.dto.GroupResponse;
import ue.edu.co.splitbill.dto.MessageResponse;
import ue.edu.co.splitbill.dto.SettlementResponse;
import ue.edu.co.splitbill.service.GroupService;
import ue.edu.co.splitbill.service.SettlementService;

// CRUD 1: grupos. Tambien expone la liquidacion del grupo
@RestController
@RequestMapping("/api/groups")
public class GroupController {

    private final GroupService groupService;
    private final SettlementService settlementService;

    public GroupController(GroupService groupService, SettlementService settlementService) {
        this.groupService = groupService;
        this.settlementService = settlementService;
    }

    // GET /api/groups?userId=1: grupos del usuario
    @GetMapping
    public List<GroupResponse> list(@RequestParam Long userId) {
        return this.groupService.listByOwner(userId);
    }

    // GET: un solo grupo
    @GetMapping("/{groupId}")
    public GroupResponse get(@PathVariable Long groupId) {
        return this.groupService.get(groupId);
    }

    // POST: crear grupo
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public GroupResponse create(@Valid @RequestBody GroupRequest request) {
        return this.groupService.create(request);
    }

    // PUT: editar grupo
    @PutMapping("/{groupId}")
    public GroupResponse update(@PathVariable Long groupId, @Valid @RequestBody GroupRequest request) {
        return this.groupService.update(groupId, request);
    }

    // DELETE: eliminar grupo (borrado logico)
    @DeleteMapping("/{groupId}")
    public MessageResponse delete(@PathVariable Long groupId) {
        this.groupService.delete(groupId);
        return new MessageResponse("Grupo eliminado");
    }

    // Saldos y transferencias, calculados aqui en el servidor
    @GetMapping("/{groupId}/settlement")
    public SettlementResponse settlement(@PathVariable Long groupId) {
        return this.settlementService.settle(groupId);
    }
}
