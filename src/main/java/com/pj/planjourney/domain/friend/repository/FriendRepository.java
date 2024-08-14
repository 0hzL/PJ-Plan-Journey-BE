package com.pj.planjourney.domain.friend.repository;

import com.pj.planjourney.domain.friend.entity.Friend;
import com.pj.planjourney.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FriendRepository extends JpaRepository<Friend, Long> {
    Page<Friend> findByUser(User user, Pageable pageable);
    Friend findByUserAndFriend(User user, User friend);
}
