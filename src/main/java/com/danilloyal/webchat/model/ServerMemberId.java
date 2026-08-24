package com.danilloyal.webchat.model;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ServerMemberId implements Serializable{
    
    private Long serverId;

    private Long userId;

    @Override
    public boolean equals(Object o){
        if (this == o) return true;
        if (!(o instanceof ServerMemberId)) return false;

        ServerMemberId that = (ServerMemberId) o;

        return Objects.equals(userId, that.userId)
                && Objects.equals(serverId, that.serverId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, serverId);
    }
}
