package com.danilloyal.webchat.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.danilloyal.webchat.dto.ServerMemberResponse;
import com.danilloyal.webchat.dto.ServerRequest;
import com.danilloyal.webchat.dto.ServerResponse;
import com.danilloyal.webchat.service.ServerService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;

import java.util.List;

import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;





@RestController
@RequestMapping("/server")
@RequiredArgsConstructor
public class ServerController {
    
    private final ServerService serverService;
    
    @PostMapping
    public ResponseEntity<ServerResponse> create(@Valid @RequestBody ServerRequest request) {
        
        ServerResponse response = serverService.create(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ServerResponse> update(@PathVariable Long id, @Valid @RequestBody ServerRequest request) {
        
        ServerResponse response;
        try {
            response = serverService.update(id, request);
        } catch (BadRequestException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/{id}/join")
    public ResponseEntity<Void> join(@PathVariable Long id) {

        try {
            serverService.join(id);
        } catch (BadRequestException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        
        return ResponseEntity.ok(null);
    }
    
    @DeleteMapping("/{id}/leave")
    public ResponseEntity<Void> leave(@PathVariable Long id) {
        
        try {
            serverService.leave(id);
        } catch (BadRequestException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        return ResponseEntity.ok(null);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ServerResponse> getServerByID(@PathVariable Long id) {

        ServerResponse response = serverService.getServerByID(id);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
    
    @GetMapping("/{id}/members")
    public ResponseEntity<List<ServerMemberResponse>> getMembersByServerId(@PathVariable Long id) {
        List<ServerMemberResponse> response = serverService.findMembersByServerId(id);

        return ResponseEntity.ok(response);
    }
    
}
