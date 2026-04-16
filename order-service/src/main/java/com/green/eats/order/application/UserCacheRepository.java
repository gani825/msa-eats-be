package com.green.eats.order.application;

import com.green.eats.order.entity.UserCache;
import org.springframework.data.jpa.repository.JpaRepository;

// UserCache 엔티티 전용 JPA Repository
public interface UserCacheRepository extends JpaRepository<UserCache, Long> {
}