package com.danilloyal.webchat.dto;

import jakarta.validation.constraints.NotBlank;

public record AuthenticateRequest(@NotBlank String username, @NotBlank String password) {

}
