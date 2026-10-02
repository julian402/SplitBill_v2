package ue.edu.co.splitbill.dto;

// memberCount y total los calcula el servidor
public record GroupResponse(Long id, String name, String description, Long ownerId, long memberCount, long total) {
}
