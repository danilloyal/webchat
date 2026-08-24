package com.danilloyal.webchat.controller;

import org.springframework.web.bind.annotation.RestController;

import com.danilloyal.webchat.dto.ChannelRequest;
import com.danilloyal.webchat.dto.ChannelResponse;
import com.danilloyal.webchat.service.ChannelService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;



@RestController
@RequiredArgsConstructor
public class ChannelController {

    private final ChannelService channelService;

    @PostMapping("/server/{serverId}/channel")
    public ResponseEntity<ChannelResponse> create(@Valid @RequestBody ChannelRequest request, @PathVariable Long serverId) {
        
        ChannelResponse response = channelService.create(request, serverId);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/server/{serverId}/channel")
    public ResponseEntity<List<ChannelResponse>> getChannelByServerId(@PathVariable Long serverId) {
        
        List<ChannelResponse> response = channelService.findByServerId(serverId);

        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/server/{serverId}/channel/{channelId}")
    public ResponseEntity<Void> delete(@PathVariable Long serverId, Long channelId){

        channelService.delete(channelId, serverId);

        return ResponseEntity.ok(null);
    }
}
