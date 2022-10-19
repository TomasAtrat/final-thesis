package com.gmail.tomasatrat.backend.repositories;

import com.gmail.tomasatrat.backend.data.entity.VProductivityByUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;

public interface VProductivityByUserRepository extends JpaRepository<VProductivityByUser, Long> {

    @Query(nativeQuery = true, value = "SELECT * FROM v_productivity_by_user WHERE user_id = :userId")
    List<VProductivityByUser> findByUser(Long userId);
}
