package ue.edu.co.splitbill.dto;

// Integrante tal como se le devuelve a la app
public record MemberResponse(Long id, Long groupId, String name, String phone) {
}
