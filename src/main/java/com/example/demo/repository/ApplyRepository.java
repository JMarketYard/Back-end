package com.example.demo.repository;

import com.example.demo.entity.Apply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface ApplyRepository extends JpaRepository<Apply, Long> {
}
