package ue.edu.co.splitbill.dto;

// Saldo de un integrante: lo que pagó menos lo que le tocaba. Positivo = le deben; negativo = debe
public record BalanceResponse(Long memberId, String name, long paid, long share, long balance) {
}
