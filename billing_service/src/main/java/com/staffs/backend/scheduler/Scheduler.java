package com.staffs.backend.scheduler;

import com.staffs.backend.billingSetup.dto.BillingSetupDTO;
import com.staffs.backend.entity.billingSetup.iModel.BillingSetupBasicInfoI;
import com.staffs.backend.entity.billingSetup.model.BillingSetup;
import com.staffs.backend.billingSetup.service.BillingSetupService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
public class Scheduler {

    private final BillingSetupService billingSetupService;

    public Scheduler(BillingSetupService billingSetupService) {
        this.billingSetupService = billingSetupService;
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void checkOutDatedBills() {
        log.info("outdated bills spooling started!!!");

        List<BillingSetupBasicInfoI> outdatedBills = billingSetupService.getOutdatedBills();

        if (!outdatedBills.isEmpty()) {
            log.info("{} outdated bills found!!!", outdatedBills.size());

            List<BillingSetup> billingSetups = new ArrayList<>();

            for (BillingSetupBasicInfoI outdatedBill : outdatedBills) {
                BillingSetup billingSetup = billingSetupService.getOutdatedBill(outdatedBill.billId());

                //add to list
                billingSetups.add(billingSetup);
            }

            //invalidate outdated bills
            billingSetupService.updateOutdatedBills(billingSetups);

            //handle recurrent bills here
            List<BillingSetupDTO> billingSetupDTOs = billingSetupService.renewBills(billingSetups);

            log.info("{} renewed bills here", Arrays.toString(billingSetupDTOs.toArray()));

        }

    }

}
