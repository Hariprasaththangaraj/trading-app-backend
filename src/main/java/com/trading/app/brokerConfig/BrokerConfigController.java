package com.trading.app.brokerConfig;

import com.trading.app.brokerConfig.model.BorkerConfigRequest;
import com.trading.app.brokerConfig.service.AliceBlueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/brokerConfig")
public class BrokerConfigController {
    @Autowired
    AliceBlueService aliceBlueService;

    @PostMapping("/connection")
    public String getLoginDetails(@RequestBody BorkerConfigRequest borkerConfigRequest) throws Exception {
        return aliceBlueService.brokerConnection(borkerConfigRequest);
    }

    @PostMapping("/save")
    public String saveDetails(@RequestBody BorkerConfigRequest borkerConfigRequest) {
        return aliceBlueService.saveDetails(borkerConfigRequest);
    }

    @GetMapping("/getBrokerDetails")
    public BorkerConfigRequest getBrokerDetails(@RequestParam("userId") Long userId,
            @RequestParam("brokerName") String brokerName) {
        return aliceBlueService.getBrokerDetails(userId, brokerName);
    }
}
