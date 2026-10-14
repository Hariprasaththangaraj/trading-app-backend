package com.trading.app.brokerConfig.repo;

import com.trading.app.brokerConfig.model.BrokerConfigDTO;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BrokerRepo
        extends JpaRepository<BrokerConfigDTO, Long> {

    @Modifying(
            clearAutomatically = true,
            flushAutomatically = true
    )
    @Query("""
        UPDATE BrokerConfigDTO b
        SET b.brokerName = :brokerName,
            b.appSecret = :appSecret,
            b.appId = :appId
        WHERE b.clientId = :clientId
          AND b.appKey = :appKey
        """)
    int updateByClientIdAndAppKey(
            @Param("brokerName") String brokerName,
            @Param("appSecret") String appSecret,
            @Param("appId") String appId,
            @Param("clientId") String clientId,
            @Param("appKey") String appKey
    );

    BrokerConfigDTO getByClientId(String userId);
}