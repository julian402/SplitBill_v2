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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import ue.edu.co.splitbill.dto.MemberRequest;
import ue.edu.co.splitbill.dto.MemberResponse;
import ue.edu.co.splitbill.dto.MessageResponse;
import ue.edu.co.splitbill.service.MemberService;

// CRUD 2: integrantes de un grupo
@RestController
@RequestMapping("/api")
public class MemberController {

    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    // GET: integrantes del grupo
    @GetMapping("/groups/{groupId}/members")
    public List<MemberResponse> list(@PathVariable Long groupId) {
        return this.memberService.listByGroup(groupId);
    }

    // POST: agregar integrante al grupo
    @PostMapping("/groups/{groupId}/members")
    @ResponseStatus(HttpStatus.CREATED)
    public MemberResponse create(@PathVariable Long groupId, @Valid @RequestBody MemberRequest request) {
        return this.memberService.create(groupId, request);
    }

    // PUT: editar integrante
    @PutMapping("/members/{memberId}")
    public MemberResponse update(@PathVariable Long memberId, @Valid @RequestBody MemberRequest request) {
        return this.memberService.update(memberId, request);
    }

    // DELETE: eliminar integrante (si no ha pagado gastos)
    @DeleteMapping("/members/{memberId}")
    public MessageResponse delete(@PathVariable Long memberId) {
        this.memberService.delete(memberId);
        return new MessageResponse("Integrante eliminado");
    }
}
