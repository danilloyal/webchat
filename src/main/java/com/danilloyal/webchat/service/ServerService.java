package com.danilloyal.webchat.service;


import java.util.List;
import java.util.Optional;

import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.danilloyal.webchat.dto.ServerMemberResponse;
import com.danilloyal.webchat.dto.ServerRequest;
import com.danilloyal.webchat.dto.ServerResponse;
import com.danilloyal.webchat.expection.ForbiddenException;
import com.danilloyal.webchat.expection.NotFoundException;
import com.danilloyal.webchat.model.Server;
import com.danilloyal.webchat.model.ServerMember;
import com.danilloyal.webchat.model.User;
import com.danilloyal.webchat.repository.ServerMemberRepository;
import com.danilloyal.webchat.repository.ServerRepository;
import com.danilloyal.webchat.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ServerService {
    
    private final ServerRepository serverRepository;

    private final UserRepository userRepository;

    private final ServerMemberRepository serverMemberRepository;

    private final UserService userService;

    @Transactional
    public ServerResponse create(ServerRequest request) {
        
        String username = userService.getCurrentUser().getUsername();

        User owner = userRepository.findByUsername(username)
            .orElseThrow(() -> new NotFoundException("User not found"));

        Server server = new Server();

        server.setName(request.name());
        server.setOwner(owner);

        Server savedServer = serverRepository.save(server);

        ServerMember serverMember = new ServerMember(owner, savedServer);
        serverMemberRepository.save(serverMember);

        return new ServerResponse(
                savedServer.getId(),
                savedServer.getName(),
                savedServer.getOwner().getId(),
                savedServer.getCreatedAt()
        );
    
    }

    @Transactional
    public ServerResponse update(Long id, ServerRequest request) throws BadRequestException {
        
        String username = userService.getCurrentUser().getUsername();

        User owner = userRepository.findByUsername(username)
            .orElseThrow(() -> new NotFoundException("User not found"));

        Server server = serverRepository.findById(id).orElseThrow(() -> new NotFoundException("Server not found"));

        if (!server.getOwner().getId().equals(owner.getId())) {
        throw new ForbiddenException("You are not the owner of this server");
        }

        if (server.getName().equals(request.name())) {
            throw new BadRequestException("No changes were made");
        }
        server.setName(request.name());

        Server updatedServer = serverRepository.save(server);

        return new ServerResponse(
                updatedServer.getId(),
                updatedServer.getName(),
                updatedServer.getOwner().getId(),
                updatedServer.getCreatedAt()
        );
    }

    @Transactional
    public void join(Long id) throws BadRequestException {
        User user = userService.getCurrentUser();

        Server server = serverRepository.findById(id).orElseThrow(() -> new NotFoundException("Server not found"));

        boolean alreadyMember = serverMemberRepository.existsByUserIdAndServerId(user.getId(), server.getId());

        if(alreadyMember){
            throw new BadRequestException("User is already member of this server");
        }

        ServerMember newServerMember = new ServerMember(user, server);

        serverMemberRepository.save(newServerMember);
    }

    @Transactional
    public void leave(Long id) throws BadRequestException {

        User user = userService.getCurrentUser();

        Server server = serverRepository.findById(id)
                .orElseThrow(() ->
                        new NotFoundException("Server not found")
                );

        if (server.getOwner().getId().equals(user.getId())) {
            leaveAsOwner(server, user);
        } else {
            leaveAsMember(server, user);
        }
    }

    private void leaveAsOwner(Server server, User owner){

        Optional<ServerMember> oldestMember =
            serverMemberRepository
                    .findFirstByServerIdAndUserIdNotOrderByJoinedAtAsc(
                            server.getId(),
                            owner.getId()
                    );

        if (oldestMember.isEmpty()) {
            serverRepository.delete(server);
            return;
        }

        server.setOwner(oldestMember.get().getUser());

        serverMemberRepository.deleteByUserIdAndServerId(
                owner.getId(),
                server.getId()
        );
    }

    private void leaveAsMember(Server server, User user) throws BadRequestException{

        boolean isMember = serverMemberRepository
            .existsByUserIdAndServerId(
                    user.getId(),
                    server.getId()
            );

        if (!isMember) {
            throw new BadRequestException(
                    "User is not a member of this server"
            );
        }

        serverMemberRepository.deleteByUserIdAndServerId(
                user.getId(),
                server.getId()
        );
    }

    @Transactional(readOnly = true)
    public ServerResponse getServerByID(Long id) {
        Server server = serverRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("Server not found"));
        
        return new ServerResponse(
               server.getId(),
               server.getName(),
               server.getOwner().getId(),
               server.getCreatedAt()
        );
    }

    public List<ServerMemberResponse> findMembersByServerId(Long id) {
        User user = userService.getCurrentUser();

        Server server = serverRepository.findById(id).orElseThrow(() -> new NotFoundException("Server not found"));

        boolean isMember = serverMemberRepository
            .existsByUserIdAndServerId(
                    user.getId(),
                    server.getId()
            );
        
        if (!isMember) {
            throw new ForbiddenException(
                    "User is not a member of this server"
            );
        }

        return serverMemberRepository.findByServerId(id).stream().map(this::toServerMemberResponse).toList();
    }

    private ServerMemberResponse toServerMemberResponse(ServerMember serverMember){
        return new ServerMemberResponse(
            serverMember.getUser().getId(),
            serverMember.getUser().getUsername(),
            serverMember.getUser().getEmail(),
            serverMember.getJoinedAt()
        );
    }
}
