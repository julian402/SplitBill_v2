package ue.edu.co.splitbill.service;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ue.edu.co.splitbill.dto.BalanceResponse;
import ue.edu.co.splitbill.dto.QuickSplitResponse;
import ue.edu.co.splitbill.dto.TransferResponse;
import ue.edu.co.splitbill.entity.Expense;
import ue.edu.co.splitbill.entity.Member;

/*
 * Toda la matematica de SplitBill. No toca la base de datos: recibe listas y devuelve resultados,
 * por eso se puede probar con JUnit sin levantar nada (ver SplitCalculatorTest).
 * Los montos son pesos enteros (long): asi no hay errores de redondeo de los double.
 */
@Component
public class SplitCalculator {

    // Divide en partes iguales. Si no da exacto, los primeros pagan 1 peso mas: 100 / 3 = [34, 33, 33]
    public long[] splitEqually(long total, int people) {
        if (people <= 0) {
            throw new IllegalArgumentException("Debe haber al menos una persona");
        }
        long[] parts = new long[people];
        long base = total / people;
        long remainder = total % people;
        for (int i = 0; i < people; i++) {
            parts[i] = base + (i < remainder ? 1 : 0);
        }
        return parts;
    }

    // Lo que le toca a cada uno de un gasto, para mostrarlo en la lista (sin repartir el sobrante)
    public long perPerson(long amount, int people) {
        return people <= 0 ? 0 : amount / people;
    }

    // Saldo de cada integrante: lo que pago menos su parte del total
    public List<BalanceResponse> calculateBalances(List<Member> members, List<Expense> expenses) {
        List<BalanceResponse> balances = new ArrayList<>();
        if (members.isEmpty()) {
            return balances;
        }
        Map<Long, Long> paidByMember = new HashMap<>();
        long total = 0;
        for (Expense expense : expenses) {
            paidByMember.merge(expense.getPayerId(), expense.getAmount(), Long::sum);
            total += expense.getAmount();
        }
        long[] shares = splitEqually(total, members.size());
        for (int i = 0; i < members.size(); i++) {
            Member member = members.get(i);
            long paid = paidByMember.getOrDefault(member.getId(), 0L);
            balances.add(new BalanceResponse(member.getId(), member.getName(), paid, shares[i], paid - shares[i]));
        }
        return balances;
    }

    /*
     * Quien le paga a quien. Algoritmo voraz: el que mas debe le paga al que mas le deben, se descuenta
     * y se repite. Asi salen como maximo (integrantes - 1) transferencias.
     */
    public List<TransferResponse> calculateTransfers(List<BalanceResponse> balances) {
        List<Person> debtors = new ArrayList<>();
        List<Person> creditors = new ArrayList<>();
        for (BalanceResponse balance : balances) {
            if (balance.balance() < 0) {
                debtors.add(new Person(balance.memberId(), balance.name(), -balance.balance()));
            } else if (balance.balance() > 0) {
                creditors.add(new Person(balance.memberId(), balance.name(), balance.balance()));
            }
        }
        // De mayor a menor
        debtors.sort((a, b) -> Long.compare(b.amount, a.amount));
        creditors.sort((a, b) -> Long.compare(b.amount, a.amount));

        List<TransferResponse> transfers = new ArrayList<>();
        int i = 0;
        int j = 0;
        while (i < debtors.size() && j < creditors.size()) {
            Person debtor = debtors.get(i);
            Person creditor = creditors.get(j);
            long amount = Math.min(debtor.amount, creditor.amount);
            transfers.add(new TransferResponse(debtor.id, debtor.name, creditor.id, creditor.name, amount));
            debtor.amount -= amount;
            creditor.amount -= amount;
            if (debtor.amount == 0) {
                i++;
            }
            if (creditor.amount == 0) {
                j++;
            }
        }
        return transfers;
    }

    // Cuenta rapida: propina sobre el subtotal (redondeada al peso) y division entre las personas
    public QuickSplitResponse quickSplit(long subtotal, int tipPercent, int people) {
        long tip = Math.round(subtotal * tipPercent / 100.0);
        long total = subtotal + tip;
        return new QuickSplitResponse(subtotal, tip, total, people, total / people, total % people);
    }

    // Saldo pendiente de una persona mientras se arman las transferencias
    private static class Person {
        private final Long id;
        private final String name;
        private long amount;

        private Person(Long id, String name, long amount) {
            this.id = id;
            this.name = name;
            this.amount = amount;
        }
    }
}
