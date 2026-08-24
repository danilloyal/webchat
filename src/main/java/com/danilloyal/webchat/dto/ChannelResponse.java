package com.danilloyal.webchat.dto;

import java.time.LocalDateTime;

import com.danilloyal.webchat.model.ChannelType;

public record ChannelResponse(Long id, Long serverId, String name, ChannelType type, LocalDateTime createdAt) {

}
