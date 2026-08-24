package com.danilloyal.webchat.dto;

import com.danilloyal.webchat.model.ChannelType;

public record ChannelRequest(String name, ChannelType type) {

}
