package ue.edu.co.splitbill.dto;

import java.util.List;

// Liquidación completa de un grupo, calculada en el servidor
public record SettlementResponse(long total, int memberCount, long perPerson,
                                 List<BalanceResponse> balances, List<TransferResponse> transfers) {
}
