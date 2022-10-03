package com.gmail.tomasatrat.backend.repositories;

import com.gmail.tomasatrat.backend.data.entity.User;
import com.gmail.tomasatrat.backend.data.entity.VTasksByUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface VTasksByUserRepository extends JpaRepository<VTasksByUser, Long> {
    @Query(nativeQuery = true, value = "SELECT * FROM v_tasks_by_user WHERE role != 'admin' LIMIT 1")
    VTasksByUser findAutoUser();
}
