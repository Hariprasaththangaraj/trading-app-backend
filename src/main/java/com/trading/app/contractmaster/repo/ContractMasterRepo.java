package com.trading.app.contractmaster.repo;

import com.trading.app.contractmaster.model.InstrumentMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ContractMasterRepo extends JpaRepository<InstrumentMaster, Long> {
    boolean existsByExchangeAndToken(
            String exchange,
            String token
    );
}
