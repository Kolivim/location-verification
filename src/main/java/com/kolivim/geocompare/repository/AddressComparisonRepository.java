package com.kolivim.geocompare.repository;

import com.kolivim.geocompare.entity.AddressComparison;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AddressComparisonRepository extends JpaRepository<AddressComparison, Long> {}