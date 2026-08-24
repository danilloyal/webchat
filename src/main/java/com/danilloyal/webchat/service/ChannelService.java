package com.danilloyal.webchat.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.danilloyal.webchat.dto.ChannelRequest;
import com.danilloyal.webchat.dto.ChannelResponse;
import com.danilloyal.webchat.expection.ForbiddenException;
import com.danilloyal.webchat.expection.NotFoundException;
import com.danilloyal.webchat.model.Channel;
import com.danilloyal.webchat.model.Server;
import com.danilloyal.webchat.model.User;
import com.danilloyal.webchat.repository.ChannelRepository;
import com.danilloyal.webchat.repository.ServerMemberRepository;
import com.danilloyal.webchat.repository.ServerRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChannelService {

    private final UserService userService;

    private final ServerRepository serverRepository;

    private final ServerMemberRepository serverMemberRepository;

    private final ChannelRepository channelRepository;

    @Transactional
    public ChannelResponse create(ChannelRequest request, Long serverId){
        
        User user = userService.getCurrentUser();

        Server server = serverRepository.findById(serverId).orElseThrow(() -> new NotFoundException("Server not found"));
        
        boolean isMember = serverMemberRepository.existsByUserIdAndServerId(user.getId(), server.getId());
        
        if(!isMember){
            throw new ForbiddenException("User is not a member of this server");
        }

        Channel channel = new Channel();
        channel.setServer(server);
        channel.setName(request.name());
        channel.setType(request.type());
        channel.setCreatedAt(LocalDateTime.now());

        Channel savedChannel = channelRepository.save(channel);

        return toResponse(savedChannel);
    }

    @Transactional(readOnly = true)
    public List<ChannelResponse> findByServerId(Long serverId){
        User user = userService.getCurrentUser();

        Server server = serverRepository.findById(serverId).orElseThrow(() -> new NotFoundException("Server not found"));
        
        boolean isMember = serverMemberRepository.existsByUserIdAndServerId(user.getId(), server.getId());
        
        if(!isMember){
            throw new ForbiddenException("User is not a member of this server");
        }

        return channelRepository.findByServerId(serverId).stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public Channel getAuthorizedChannel(Long channelId) {

        User user = userService.getCurrentUser();

        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new NotFoundException("Channel not found"));

        boolean isMember = serverMemberRepository.existsByUserIdAndServerId(user.getId(), channel.getServer().getId());

        if (!isMember) {
            throw new ForbiddenException(
                    "User is not a member of this server"
            );
        }

        return channel;
    }

    private ChannelResponse toResponse(Channel channel){
        return new ChannelResponse(
            channel.getId(),
            channel.getServer().getId(),
            channel.getName(),
            channel.getType(),
            channel.getCreatedAt());
    }

    @Transactional
    public void delete(Long channelId, Long serverId) {
        User user = userService.getCurrentUser();

        Channel channel = channelRepository
                .findByIdAndServerId(channelId, serverId)
                .orElseThrow(() ->
                        new NotFoundException("Channel not found")
                );

        boolean isMember = serverMemberRepository
                .existsByUserIdAndServerId(user.getId(), serverId);

        if (!isMember) {
            throw new ForbiddenException(
                    "User is not a member of this server"
            );
        }

        channelRepository.delete(channel);
    }
}
