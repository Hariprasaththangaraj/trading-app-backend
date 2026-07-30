package com.trading.app.brokerConfig.aliceBlueConfig;

import com.trading.app.brokerConfig.aliceBlueConfig.service.AliceBlueService;
import com.trading.app.brokerConfig.aliceBlueConfig.util.Constant;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

@RestController
public class AliceBlueController {

    private final AliceBlueService aliceBlueService;


    public AliceBlueController(AliceBlueService aliceBlueService) {
        this.aliceBlueService = aliceBlueService;
    }

    @CrossOrigin
    @GetMapping("/auth/login")
    public String login() {
        return aliceBlueService.getLoginUrl();
    }


    @GetMapping("/callback")
    public RedirectView callback(@RequestParam("authCode") String authCode) {
        System.out.println("AUTH CODE = " + authCode);
        String checkSumValue = aliceBlueService.generateCheckSum(authCode);
        if (checkSumValue != null) {
            return new RedirectView(
                    Constant.REDIRECT_FRONTENT);
        } else return new RedirectView(
                "Failed Checksum Generation");
    }

}
