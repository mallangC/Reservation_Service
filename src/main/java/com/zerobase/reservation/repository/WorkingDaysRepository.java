package com.zerobase.reservation.repository;

import com.zerobase.reservation.model.WorkingDays;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkingDaysRepository extends JpaRepository<WorkingDays, Long> {
}
