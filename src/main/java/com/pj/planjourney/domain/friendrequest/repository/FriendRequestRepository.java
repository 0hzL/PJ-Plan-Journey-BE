package com.pj.planjourney.domain.friendrequest.repository;

import com.pj.planjourney.domain.friendrequest.entity.FriendRequest;
import com.pj.planjourney.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FriendRequestRepository extends JpaRepository<FriendRequest, Long> {
    Page<FriendRequest> findByReceiver(User receiver, Pageable pageable);
    List<FriendRequest> findBySender(User sender);
    FriendRequest findBySenderAndReceiver(User sender, User receiver);
}
