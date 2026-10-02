package ue.edu.co.splitbill.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ue.edu.co.splitbill.dto.ExpenseRequest;
import ue.edu.co.splitbill.dto.ExpenseResponse;
import ue.edu.co.splitbill.entity.Expense;
import ue.edu.co.splitbill.entity.Member;
import ue.edu.co.splitbill.exception.ApiException;
import ue.edu.co.splitbill.repository.ExpenseRepository;
import ue.edu.co.splitbill.repository.MemberRepository;

// Logica de los gastos (CRUD 3)
@Service
public class ExpenseService {

    private static final int STATUS_ACTIVE = 1;
    private static final int STATUS_INACTIVE = 0;
    private final ExpenseRepository expenseRepository;
    private final MemberRepository memberRepository;
    private final GroupService groupService;
    private final SplitCalculator splitCalculator;

    public ExpenseService(ExpenseRepository expenseRepository, MemberRepository memberRepository,
                          GroupService groupService, SplitCalculator splitCalculator) {
        this.expenseRepository = expenseRepository;
        this.memberRepository = memberRepository;
        this.groupService = groupService;
        this.splitCalculator = splitCalculator;
    }

    // Gastos del grupo con el nombre de quien pago
    public List<ExpenseResponse> listByGroup(Long groupId) {
        this.groupService.findActive(groupId);
        List<Member> members = this.memberRepository.findByGroupIdAndStatusOrderByIdAsc(groupId, STATUS_ACTIVE);
        // Nombres de los integrantes por id, para no consultar la base una vez por gasto.
        // Quien pago siempre sigue activo: MemberService no deja eliminarlo
        Map<Long, String> names = new HashMap<>();
        for (Member member : members) {
            names.put(member.getId(), member.getName());
        }
        List<ExpenseResponse> expenses = new ArrayList<>();
        for (Expense expense : this.expenseRepository.findByGroupIdAndStatusOrderByDateDescIdDesc(groupId, STATUS_ACTIVE)) {
            expenses.add(toResponse(expense, names.get(expense.getPayerId()), members.size()));
        }
        return expenses;
    }

    // Guarda un gasto nuevo revisando que quien pago sea del grupo
    @Transactional
    public ExpenseResponse create(Long groupId, ExpenseRequest request) {
        this.groupService.findActive(groupId);
        Member payer = findPayer(groupId, request.payerId());
        Expense expense = new Expense(groupId, payer.getId(), request.description().trim(), request.amount());
        return toResponse(this.expenseRepository.save(expense), payer.getName(), countMembers(groupId));
    }

    // Edita descripcion, monto y quien pago
    @Transactional
    public ExpenseResponse update(Long expenseId, ExpenseRequest request) {
        Expense expense = findActive(expenseId);
        Member payer = findPayer(expense.getGroupId(), request.payerId());
        expense.setDescription(request.description().trim());
        expense.setAmount(request.amount());
        expense.setPayerId(payer.getId());
        return toResponse(this.expenseRepository.save(expense), payer.getName(), countMembers(expense.getGroupId()));
    }

    // Borrado logico: el gasto queda con status 0
    @Transactional
    public void delete(Long expenseId) {
        Expense expense = findActive(expenseId);
        expense.setStatus(STATUS_INACTIVE);
        this.expenseRepository.save(expense);
    }

    // Busca el gasto o responde 404
    private Expense findActive(Long expenseId) {
        return this.expenseRepository.findByIdAndStatus(expenseId, STATUS_ACTIVE)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "El gasto no existe"));
    }

    // Quien pago debe ser un integrante activo del mismo grupo
    private Member findPayer(Long groupId, Long payerId) {
        Member payer = this.memberRepository.findByIdAndStatus(payerId, STATUS_ACTIVE).orElse(null);
        if (payer == null || !payer.getGroupId().equals(groupId)) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Quien pagó debe ser integrante del grupo");
        }
        return payer;
    }

    // Cuantos integrantes hay para dividir
    private int countMembers(Long groupId) {
        return (int) this.memberRepository.countByGroupIdAndStatus(groupId, STATUS_ACTIVE);
    }

    // Arma la respuesta y calcula cuanto le toca a cada uno
    private ExpenseResponse toResponse(Expense expense, String payerName, int memberCount) {
        long perPerson = this.splitCalculator.perPerson(expense.getAmount(), memberCount);
        return new ExpenseResponse(expense.getId(), expense.getGroupId(), expense.getDescription(),
                expense.getAmount(), expense.getPayerId(), payerName, expense.getDate(), perPerson);
    }
}
