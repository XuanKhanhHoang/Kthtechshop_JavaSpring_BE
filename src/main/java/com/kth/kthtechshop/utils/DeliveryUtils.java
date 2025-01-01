package com.kth.kthtechshop.utils;

public class DeliveryUtils {
    public static Integer getDeliveryFee(int districtId) {
        if (districtId < 0 || districtId > 2500) return null;
        if (districtId < 250) return 25000;
        if (districtId < 750) return 30000;
        if (districtId < 1250) return 38000;
        return 45000;
    }
}
