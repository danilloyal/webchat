package com.danilloyal.webchat.dto;

import java.time.LocalDateTime;

public record ServerResponse(Long id, String name, Long ownerID, LocalDateTime createdAt) {

}
