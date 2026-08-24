package com.danilloyal.webchat.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.danilloyal.webchat.model.Channel;

public interface ChannelRepository extends JpaRepository<Channel, Long>{
    List<Channel> findByServerId(Long serverId);

    Optional<Channel> findByIdAndServerId(Long channelId, Long serverId);
}
