package com.danilloyal.webchat.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.danilloyal.webchat.model.ServerMember;
import com.danilloyal.webchat.model.ServerMemberId;

public interface ServerMemberRepository extends JpaRepository<ServerMember, ServerMemberId>{
    boolean existsByUserIdAndServerId(Long userId, Long serverId);

    void deleteByUserIdAndServerId(Long userId, Long serverId);

    Optional<ServerMember> findFirstByServerIdAndUserIdNotOrderByJoinedAtAsc(Long userId, Long serverId);

    List<ServerMember> findByServerId(Long serverId);
}
