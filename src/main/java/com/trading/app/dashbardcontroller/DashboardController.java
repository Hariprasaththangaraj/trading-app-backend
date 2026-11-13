package com.trading.app.dashbardcontroller;

import com.trading.app.dashbardcontroller.model.MarketDataDTO;
import com.trading.app.dashbardcontroller.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {
    @Autowired
    DashboardService dashboardService;

    @GetMapping("/crudeoil")
    public MarketDataDTO getCrudeOilLtp() {
        return dashboardService.getCrudeOilLtp();
    }
}
