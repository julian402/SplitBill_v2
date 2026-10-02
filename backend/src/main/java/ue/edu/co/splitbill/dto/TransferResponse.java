package ue.edu.co.splitbill.dto;

// Un pago para quedar a paz y salvo: fromName le paga amount a toName
public record TransferResponse(Long fromMemberId, String fromName, Long toMemberId, String toName, long amount) {
}
