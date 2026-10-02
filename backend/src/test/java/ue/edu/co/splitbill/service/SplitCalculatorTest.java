package ue.edu.co.splitbill.service;

import org.junit.jupiter.api.Test;

import java.util.List;

import ue.edu.co.splitbill.dto.BalanceResponse;
import ue.edu.co.splitbill.dto.QuickSplitResponse;
import ue.edu.co.splitbill.dto.TransferResponse;
import ue.edu.co.splitbill.entity.Expense;
import ue.edu.co.splitbill.entity.Member;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

// Pruebas de la matematica, sin base de datos ni servidor
class SplitCalculatorTest {

    private final SplitCalculator calculator = new SplitCalculator();

    // 100 entre 3 da 34, 33 y 33
    @Test
    void splitEquallyGivesTheRemainderToTheFirstPeople() {
        assertArrayEquals(new long[]{34, 33, 33}, this.calculator.splitEqually(100, 3));
        assertArrayEquals(new long[]{50, 50}, this.calculator.splitEqually(100, 2));
    }

    // Con 0 personas tiene que lanzar error
    @Test
    void splitEquallyRejectsZeroPeople() {
        assertThrows(IllegalArgumentException.class, () -> this.calculator.splitEqually(100, 0));
    }

    // Los saldos siempre tienen que sumar 0
    @Test
    void balancesAddUpToZero() {
        List<Member> members = List.of(member(1L, "Ana"), member(2L, "Beto"), member(3L, "Caro"));
        List<Expense> expenses = List.of(
                new Expense(1L, 1L, "Almuerzo", 90_000L),
                new Expense(1L, 2L, "Taxi", 30_000L));

        List<BalanceResponse> balances = this.calculator.calculateBalances(members, expenses);

        // Total 120.000 entre 3 = 40.000 cada uno
        assertEquals(50_000, balances.get(0).balance());
        assertEquals(-10_000, balances.get(1).balance());
        assertEquals(-40_000, balances.get(2).balance());
        assertEquals(0, balances.stream().mapToLong(BalanceResponse::balance).sum());
    }

    // Con estos saldos salen 2 pagos y todos quedan en 0
    @Test
    void transfersSettleEveryone() {
        List<BalanceResponse> balances = List.of(
                new BalanceResponse(1L, "Ana", 90_000, 40_000, 50_000),
                new BalanceResponse(2L, "Beto", 30_000, 40_000, -10_000),
                new BalanceResponse(3L, "Caro", 0, 40_000, -40_000));

        List<TransferResponse> transfers = this.calculator.calculateTransfers(balances);

        assertEquals(2, transfers.size());
        assertEquals("Caro", transfers.get(0).fromName());
        assertEquals("Ana", transfers.get(0).toName());
        assertEquals(40_000, transfers.get(0).amount());
        assertEquals("Beto", transfers.get(1).fromName());
        assertEquals(10_000, transfers.get(1).amount());
    }

    // 100.000 + 10% de propina entre 3 personas
    @Test
    void quickSplitAddsTipAndDivides() {
        QuickSplitResponse result = this.calculator.quickSplit(100_000, 10, 3);

        assertEquals(10_000, result.tip());
        assertEquals(110_000, result.total());
        assertEquals(36_666, result.perPerson());
        assertEquals(2, result.remainder());
    }

    private Member member(Long id, String name) {
        // En la app el id lo pone la base; en la prueba se asigna a mano
        Member member = new Member(1L, name, null);
        member.setId(id);
        return member;
    }
}
