package ue.edu.co.splitbill.service;

import org.springframework.stereotype.Service;

import java.util.List;

import ue.edu.co.splitbill.dto.BalanceResponse;
import ue.edu.co.splitbill.dto.QuickSplitRequest;
import ue.edu.co.splitbill.dto.QuickSplitResponse;
import ue.edu.co.splitbill.dto.SettlementResponse;
import ue.edu.co.splitbill.dto.TransferResponse;
import ue.edu.co.splitbill.entity.Expense;
import ue.edu.co.splitbill.entity.Member;
import ue.edu.co.splitbill.repository.ExpenseRepository;
import ue.edu.co.splitbill.repository.MemberRepository;

// Lee los datos del grupo y le pide los calculos a SplitCalculator
@Service
public class SettlementService {

    private static final int STATUS_ACTIVE = 1;
    private final MemberRepository memberRepository;
    private final ExpenseRepository expenseRepository;
    private final GroupService groupService;
    private final SplitCalculator splitCalculator;

    public SettlementService(MemberRepository memberRepository, ExpenseRepository expenseRepository,
                             GroupService groupService, SplitCalculator splitCalculator) {
        this.memberRepository = memberRepository;
        this.expenseRepository = expenseRepository;
        this.groupService = groupService;
        this.splitCalculator = splitCalculator;
    }

    // Liquidacion del grupo: saldos, transferencias y cuanto le toca a cada uno
    public SettlementResponse settle(Long groupId) {
        this.groupService.findActive(groupId);
        List<Member> members = this.memberRepository.findByGroupIdAndStatusOrderByIdAsc(groupId, STATUS_ACTIVE);
        List<Expense> expenses = this.expenseRepository.findByGroupIdAndStatusOrderByDateDescIdDesc(groupId, STATUS_ACTIVE);

        List<BalanceResponse> balances = this.splitCalculator.calculateBalances(members, expenses);
        List<TransferResponse> transfers = this.splitCalculator.calculateTransfers(balances);
        long total = 0;
        for (Expense expense : expenses) {
            total += expense.getAmount();
        }
        long perPerson = this.splitCalculator.perPerson(total, members.size());
        return new SettlementResponse(total, members.size(), perPerson, balances, transfers);
    }

    // La cuenta rapida no lee la base, solo le pasa los datos a la calculadora
    public QuickSplitResponse quickSplit(QuickSplitRequest request) {
        return this.splitCalculator.quickSplit(request.subtotal(), request.tipPercent(), request.people());
    }
}
