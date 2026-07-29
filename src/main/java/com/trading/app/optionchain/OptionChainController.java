package com.trading.app.optionchain;

import com.trading.app.brokerConfig.aliceBlueConfig.service.AliceBlueService;
import com.trading.app.optionchain.model.OptionChain;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/optionChain")
public class OptionChainController {

    @Autowired
    AliceBlueService aliceBlueService;

    @PostMapping("/getUnderlying")
    public ResponseEntity<OptionChain> getUnderLaying(@RequestBody  OptionChain optionChain) {

        return null;
    }



}