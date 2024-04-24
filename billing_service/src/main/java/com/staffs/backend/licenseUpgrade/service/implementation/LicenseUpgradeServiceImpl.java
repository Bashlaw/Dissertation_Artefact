package com.staffs.backend.licenseUpgrade.service.implementation;

import com.staffs.backend.billingSetup.dto.BillingSetupDTO;
import com.staffs.backend.billingSetup.service.BillingSetupService;
import com.staffs.backend.entity.billingSetup.model.BillingSetup;
import com.staffs.backend.entity.licenseType.LicenseType;
import com.staffs.backend.entity.licenseUpgrade.LicenseUpgrade;
import com.staffs.backend.entity.packages.Packages;
import com.staffs.backend.entity.regionRate.RegionRate;
import com.staffs.backend.exceptions.GeneralException;
import com.staffs.backend.general.dto.MessageConstant;
import com.staffs.backend.general.dto.UpgradeChargeDTO;
import com.staffs.backend.general.enums.ResponseCodeAndMessage;
import com.staffs.backend.licenseUpgrade.dto.LicenseUpgradeDTO;
import com.staffs.backend.licenseUpgrade.dto.LicenseUpgradeRequestDTO;
import com.staffs.backend.licenseUpgrade.dto.UpgradeChargeRequestDTO;
import com.staffs.backend.licenseUpgrade.service.LicenseUpgradeService;
import com.staffs.backend.packageRate.dto.PackageRateDTO;
import com.staffs.backend.packages.dto.PackageDTO;
import com.staffs.backend.packages.service.PackageService;
import com.staffs.backend.paymentSource.service.PaymentSourceService;
import com.staffs.backend.regionRate.service.RegionRateService;
import com.staffs.backend.repository.licenseUpgrade.LicenseUpgradeRepository;
import com.staffs.backend.utils.GeneralUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class LicenseUpgradeServiceImpl implements LicenseUpgradeService {

    private final PackageService packageService;
    private final RegionRateService regionRateService;
    private final BillingSetupService billingSetupService;
    private final PaymentSourceService paymentSourceService;
    private final LicenseUpgradeRepository licenseUpgradeRepository;

    @Override
    public LicenseUpgradeDTO logLicenseUpgrade(LicenseUpgradeRequestDTO dto) {

        //get check params
        log.info("getting required params");

        //get package from info
        Packages packageFrom = packageService.getPackageByName(dto.getPackageUpgradedFrom());
        //get package name from package from
        String packageNameFrom = packageFrom.getPackageName();

        //get package to info
        Packages packageTo = packageService.getPackageByName(dto.getPackageUpgradedTo());
        //get package name from package to
        String packageNameTo = packageTo.getPackageName();
        //get duration from package to
        Long durationTo = packageTo.getDuration();

        //get package DTO from
        PackageDTO packageDTOFrom = packageService.getPackageDTOByName(packageNameFrom);

        //get package DTO from
        PackageDTO packageDTOTo = packageService.getPackageDTOByName(packageNameTo);

        //get bill rate
        PackageRateDTO packageRateDTOFrom = packageDTOFrom.getCurrentBillRates();

        //get bill rate
        PackageRateDTO packageRateDTOTo = packageDTOTo.getCurrentBillRates();

        //get rate of package from
        double rateFrom = packageRateDTOFrom.getRate();

        //get rate of package to
        double rateTo = packageRateDTOTo.getRate();

        //check if the package is valid
        log.info("checking if package from is valid for the billed account");
        if (Objects.nonNull(billingSetupService.getAccountBillDTOInfo(dto.getAccountId() , packageNameFrom))) {

            //get billing setup info
            log.info("getting the billing setup info!");
            BillingSetup billingSetup = billingSetupService.getCurrentAccountBillInfo(dto.getAccountId() , packageNameFrom);

            //check if the package is upgraded
            log.info("checking if the package is upgraded");
            if (rateFrom < rateTo) {

                //calculate upgrade charges
                log.info("calculating upgrade charges");

                //get number of days left base on previous package
                log.info("get number of days left base on previous package used");

                //get previous package end date
                LocalDateTime packageFromEndDate = billingSetup.getValidTill();

                long diffDays = ChronoUnit.DAYS.between(LocalDateTime.now() , packageFromEndDate);

                //get the total charge left
                double chargeLeft = diffDays * rateFrom;

                //get license charge amount
                double chargeAmount = (durationTo * rateTo) - chargeLeft;
                String currency = GeneralUtil.getCurrency("default");

                //check source region rate for mpesa
                if (dto.getPaymentRequest().getPaymentSource().equals("MPESA") && !regionRateService.isRegionRate("Kenya" , packageRateDTOTo.getVersionNo()) && chargeAmount > 0) {
                    throw new GeneralException(ResponseCodeAndMessage.BAD_REQUEST.responseCode , MessageConstant.PACKAGE_REGION_RATE_FOR_COUNTRY_KENYA_IS_NEEDED_TO_BE_ABLE_TO_PAY_THROUGH_THIS_SOURCE);
                }

                //if there is region rate
                if (!dto.getPaymentRequest().getPaymentSource().equals("MPESA") && regionRateService.isRegionRate(dto.getPaymentRequest().getCountry() , packageRateDTOTo.getVersionNo())) {
                    if (Objects.nonNull(regionRateService.getSingleRegionRate(dto.getPaymentRequest().getCountry() , packageRateDTOTo.getVersionNo())) && chargeAmount > 0 && !dto.getPaymentRequest().getPaymentSource().equals("MPESA")) {
                        RegionRate regionTo = (regionRateService.getSingleRegionRate(dto.getPaymentRequest().getCountry() , packageRateDTOTo.getVersionNo()));
                        double regionRateTo = regionTo.getRate();
                        RegionRate regionFrom = (regionRateService.getSingleRegionRate(dto.getPaymentRequest().getCountry() , packageRateDTOFrom.getVersionNo()));
                        double regionRateFrom = regionFrom.getRate();

                        if (regionRateTo > 0) {

                            chargeLeft = diffDays * regionRateFrom;
                            chargeAmount = (durationTo * regionRateTo) - chargeLeft;

                            //set region currency
                            currency = GeneralUtil.getCurrency(dto.getPaymentRequest().getCountry());
                        }
                    }
                } else if (dto.getPaymentRequest().getPaymentSource().equals("MPESA")) {
                    double regionRateTo = regionRateService.getSingleRegionRate("Kenya" , packageRateDTOTo.getVersionNo()).getRate();
                    double regionRateFrom = regionRateService.getSingleRegionRate("Kenya" , packageRateDTOFrom.getVersionNo()).getRate();
                    if (regionRateTo > 0) {
                        chargeLeft = diffDays * regionRateFrom;
                        chargeAmount = (durationTo * regionRateTo) - chargeLeft;

                        //set region currency
                        currency = GeneralUtil.getCurrency("Kenya");
                    }
                }

                //insert new bill setup
                log.info("Inserting new billing setup for license upgrade");
                log.info(dto.getPaymentRequest().getPaymentSource());

                if (!licenseUpgradeRepository.existsByInitialBillId(billingSetup.getBillId())) {

                    BillingSetupDTO billingSetupDTO = billingSetupService.upgradeBill(dto , chargeAmount , currency);

                    //get billing setup info
                    log.info("getting billing setup for new bill info!");
                    BillingSetup billNew = billingSetupService.getNewBillInfo(billingSetupDTO.getTransactionRef());

                    if (Objects.nonNull(billNew)) {
                        log.info("logging license upgrade info! => {}" , dto.getAccountId());

                        LicenseUpgrade licenseUpgrade = new LicenseUpgrade();
                        licenseUpgrade.setAccountId(dto.getAccountId());
                        licenseUpgrade.setUpgradedTo(packageTo);
                        licenseUpgrade.setUpgradedFrom(packageFrom);
                        licenseUpgrade.setBillingSetup(billNew);
                        licenseUpgrade.setCreatedAt(LocalDateTime.now());
                        licenseUpgrade.setLicenseUpgradeBillId(UUID.randomUUID());
                        licenseUpgrade.setInitialBillId(billingSetup.getBillId());

                        licenseUpgrade = licenseUpgradeRepository.save(licenseUpgrade);

                        return getLicenseUpgradeDTO(licenseUpgrade , billingSetupDTO);

                    } else {
                        throw new GeneralException(ResponseCodeAndMessage.BAD_REQUEST.responseCode , MessageConstant.LICENSE_UPGRADE_ERROR);
                    }

                } else {
                    //get license upgrade
                    LicenseUpgrade licenseUpgrade = licenseUpgradeRepository.findByInitialBillId(billingSetup.getBillId()).orElseThrow(() -> new GeneralException(ResponseCodeAndMessage.RECORD_NOT_FOUND.responseCode , MessageConstant.RECORD_NOT_FOUND));

                    //get bill setup dto
                    BillingSetupDTO billingSetupDTO = billingSetupService.getNewBillByBillId(licenseUpgrade.getBillingSetup().getBillId());

                    return getLicenseUpgradeDTO(licenseUpgrade , billingSetupDTO);

                }

            } else {
                throw new GeneralException(ResponseCodeAndMessage.BAD_REQUEST.responseCode , MessageConstant.INVALID_LICENSE_UPGRADE_PACKAGE);
            }

        } else {
            throw new GeneralException(ResponseCodeAndMessage.BAD_REQUEST.responseCode , MessageConstant.INVALID_LICENSE_UPGRADE_FROM_INFO);
        }
    }

    @Override
    public LicenseUpgradeDTO getValidLicenseUpgradeById(Long licenseUpgradeId) {
        log.info("getting single license upgrade DTO info");

        return getLicenseUpgradeDTO(getLicenseUpgradeById(licenseUpgradeId));
    }

    @Override
    public List<LicenseUpgradeDTO> getLicenseUpgradeByAccountId(String accountId) {
        log.info("getting license upgrade DTOs info");

        List<LicenseUpgrade> licenseUpgrades = getLicenseUpgradesByAccountId(accountId);

        return licenseUpgrades.stream().map(this::getLicenseUpgradeDTO).collect(Collectors.toList());

    }

    @Override
    public UpgradeChargeDTO getChargeAmount(UpgradeChargeRequestDTO dto) {
        log.info("getting upgrade charge amount => {}" , dto);

        //get check params
        log.info("getting params");

        //get package from info
        Packages packageFrom = packageService.getPackageByName(dto.getPackageUpgradedFrom());
        //get package name from package from
        String packageNameFrom = packageFrom.getPackageName();
        //get a license type of package from
        LicenseType licenseTypeFrom = packageFrom.getPackageType().getLicenseType();

        //get package to info
        Packages packageTo = packageService.getPackageByName(dto.getPackageUpgradedTo());
        //get package name from package to
        String packageNameTo = packageTo.getPackageName();
        //get duration from package to
        Long durationTo = packageTo.getDuration();
        //get a license type of package to
        LicenseType licenseTypeTo = packageTo.getPackageType().getLicenseType();


        //get package DTO from
        PackageDTO packageDTOFrom = packageService.getPackageDTOByName(packageNameFrom);

        //get package DTO from
        PackageDTO packageDTOTo = packageService.getPackageDTOByName(packageNameTo);

        //get bill rate
        PackageRateDTO packageRateDTOFrom = packageDTOFrom.getCurrentBillRates();

        //get bill rate
        PackageRateDTO packageRateDTOTo = packageDTOTo.getCurrentBillRates();

        //get rate of package from
        double rateFrom = packageRateDTOFrom.getRate();

        //get rate of package to
        double rateTo = packageRateDTOTo.getRate();

        //check if they are of the same licenses type
        if (!Objects.equals(licenseTypeFrom.getLicenseTypeId() , licenseTypeTo.getLicenseTypeId())) {
            throw new GeneralException(ResponseCodeAndMessage.BAD_REQUEST.responseCode , MessageConstant.UPGRADE_PACKAGE_TO_HAS_TO_BE_OF_SAME_LICENSE_WITH_UPGRADE_PACKAGE_FROM);
        }

        //check if the package is valid
        log.info("checking if package from is valid for the account");
        if (Objects.nonNull(billingSetupService.getAccountBillDTOInfo(dto.getAccountId() , packageNameFrom))) {

            //get billing setup info
            log.info("getting billing setup info!");
            BillingSetup billingSetup = billingSetupService.getCurrentAccountBillInfo(dto.getAccountId() , packageNameFrom);

            //check if the package is upgraded
            log.info("checking if package is upgraded");
            if (rateFrom < rateTo) {

                //calculate upgrade charges
                log.info("calculating upgrade total charges");

                //get number of days left base on previous package
                log.info("get number of days left base on previous package");

                //get previous package end date
                LocalDateTime packageFromEndDate = billingSetup.getValidTill();

                long diffDays = ChronoUnit.DAYS.between(LocalDateTime.now() , packageFromEndDate);

                //get the total charge left
                double chargeLeft = diffDays * rateFrom;

                //get license charge amount
                double chargeAmount = (durationTo * rateTo) - chargeLeft;
                String currency = GeneralUtil.getCurrency("default");

                //if there is region rate
                if (Objects.nonNull(dto.getCountry()) && regionRateService.isRegionRate(dto.getCountry() , packageRateDTOTo.getVersionNo())) {
                    if (Objects.nonNull(regionRateService.getSingleRegionRate(dto.getCountry() , packageRateDTOTo.getVersionNo())) && chargeAmount > 0) {
                        RegionRate regionTo = (regionRateService.getSingleRegionRate(dto.getCountry() , packageRateDTOTo.getVersionNo()));
                        double regionRateTo = regionTo.getRate();
                        RegionRate regionFrom = (regionRateService.getSingleRegionRate(dto.getCountry() , packageRateDTOFrom.getVersionNo()));
                        double regionRateFrom = regionFrom.getRate();

                        if (regionRateTo > 0) {

                            chargeLeft = diffDays * regionRateFrom;
                            chargeAmount = (durationTo * regionRateTo) - chargeLeft;

                            //set region currency
                            currency = GeneralUtil.getCurrency(dto.getCountry());

                        }
                    }
                }

                // set params to upgrade charge
                UpgradeChargeDTO upgradeChargeDTO = new UpgradeChargeDTO();
                upgradeChargeDTO.setTotalChargeAmount(chargeAmount);
                upgradeChargeDTO.setPackageNameFrom(packageNameFrom);
                upgradeChargeDTO.setPackageNameTo(packageNameTo);
                upgradeChargeDTO.setClientName(packageFrom.getPackageType().getLicenseType().getClient().getClientName());
                upgradeChargeDTO.setPaymentSourceDTO(paymentSourceService.getAllSources());
                upgradeChargeDTO.setCurrency(currency);
                upgradeChargeDTO.setPeriod(diffDays + " days");

                return upgradeChargeDTO;

            } else {
                throw new GeneralException(ResponseCodeAndMessage.BAD_REQUEST.responseCode , MessageConstant.LICENSE_UPGRADE_ERROR);
            }

        } else {
            throw new GeneralException(ResponseCodeAndMessage.BAD_REQUEST.responseCode , MessageConstant.LICENSE_UPGRADE_ERROR);
        }
    }

    private LicenseUpgrade getLicenseUpgradeById(Long licenseUpgradeId) {
       return licenseUpgradeRepository.findByLicenseUpgradeId(licenseUpgradeId).orElseThrow(() -> new GeneralException(ResponseCodeAndMessage.RECORD_NOT_FOUND.responseCode , MessageConstant.RECORD_NOT_FOUND));
    }

    private List<LicenseUpgrade> getLicenseUpgradesByAccountId(String accountId) {
        return licenseUpgradeRepository.findByAccountId(accountId);
    }

    private LicenseUpgradeDTO getLicenseUpgradeDTO(LicenseUpgrade licenseUpgrade) {
        log.info("converting license Upgrade to license Upgrade DTO");

        LicenseUpgradeDTO licenseUpgradeDTO = new LicenseUpgradeDTO();
        BeanUtils.copyProperties(licenseUpgrade , licenseUpgradeDTO);

        //get to DTOs
        licenseUpgradeDTO.setUpgradedFrom(packageService.getPackageDTOByName(licenseUpgrade.getUpgradedFrom().getPackageName()));
        licenseUpgradeDTO.setUpgradedTo(packageService.getPackageDTOByName(licenseUpgrade.getUpgradedTo().getPackageName()));
        licenseUpgradeDTO.setBillingSetup(billingSetupService.getNewBillByBillId(licenseUpgrade.getBillingSetup().getBillId()));

        return licenseUpgradeDTO;
    }

    private LicenseUpgradeDTO getLicenseUpgradeDTO(LicenseUpgrade licenseUpgrade , BillingSetupDTO billingSetupDTO) {
        log.info("converting licenseUpgrade to licenseUpgrade DTO");

        LicenseUpgradeDTO licenseUpgradeDTO = new LicenseUpgradeDTO();
        BeanUtils.copyProperties(licenseUpgrade , licenseUpgradeDTO);

        //get to DTOs
        licenseUpgradeDTO.setUpgradedFrom(packageService.getPackageDTOByName(licenseUpgrade.getUpgradedFrom().getPackageName()));
        licenseUpgradeDTO.setUpgradedTo(packageService.getPackageDTOByName(licenseUpgrade.getUpgradedTo().getPackageName()));
        licenseUpgradeDTO.setBillingSetup(billingSetupDTO);

        return licenseUpgradeDTO;
    }

}
