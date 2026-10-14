package com.trading.app.brokerConfig;

import com.trading.app.brokerConfig.aliceBlueConfig.model.BorkerConfigRequest;
import com.trading.app.brokerConfig.aliceBlueConfig.service.AliceBlueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/brokerConfig")
public class BrokerConfigController {

    @Autowired
    AliceBlueService aliceBlueService ;


    @CrossOrigin
    @PostMapping("/connection")
    public String getLoginDetails(@RequestBody BorkerConfigRequest borkerConfigRequest) throws Exception {
        return aliceBlueService.brokerConnection(borkerConfigRequest);
    }
}
