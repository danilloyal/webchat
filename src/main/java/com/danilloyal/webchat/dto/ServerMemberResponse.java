package com.danilloyal.webchat.dto;

import java.time.LocalDateTime;

public record ServerMemberResponse(Long userId, String name, String email, LocalDateTime joinedAt) {

}
