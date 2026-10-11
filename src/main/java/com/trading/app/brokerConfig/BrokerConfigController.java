package com.trading.app.brokerConfig;

import com.trading.app.brokerConfig.aliceBlueConfig.model.BorkerConfigRequest;
import com.trading.app.brokerConfig.aliceBlueConfig.service.AliceBlueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/brokerConfig")
public class BrokerConfigController {

    @Autowired
    AliceBlueService aliceBlueService ;

    @PostMapping("/login")
    public String getLoginDetails(@RequestBody BorkerConfigRequest borkerConfigRequest) throws Exception {
        return aliceBlueService.getLoginUrl(borkerConfigRequest);
    }
}
