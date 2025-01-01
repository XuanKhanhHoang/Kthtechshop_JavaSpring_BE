package com.kth.kthtechshop.services;

import com.kth.kthtechshop.dto.VNPayReturnResponseDTO;
import com.kth.kthtechshop.models.Order;
import com.kth.kthtechshop.utils.SecurityUtil;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class PaymentService {
    private final String vnpVersion = "2.1.0";
    private final String locale = "vn";
    private final String currCode = "VND";
    @Value("${vnp_TmnCode}")
    private String tmnCode;
    @Value("${vnp_HashSecret}")
    private String secretKey;
    @Value("${vnp_Url}")
    private String vnpUrl;
    @Value("${vnp_ReturnUrl}")
    private String returnUrl;

    @Transactional
    public String createPayment(String ipAddr, Long orderId, Long price) {
        String vnpCommand = "pay";
        LocalDateTime now = LocalDateTime.now();
        String createDate = now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        Map<String, String> vnpParams = new HashMap<>();
        vnpParams.put("vnp_Version", vnpVersion);
        vnpParams.put("vnp_Command", vnpCommand);
        vnpParams.put("vnp_TmnCode", tmnCode);
        vnpParams.put("vnp_Locale", locale);
        vnpParams.put("vnp_CurrCode", currCode);
        vnpParams.put("vnp_TxnRef", String.valueOf(orderId));
        vnpParams.put("vnp_OrderInfo", "Thanh toan cho ma GD:" + orderId);
        vnpParams.put("vnp_OrderType", "other");
        vnpParams.put("vnp_Amount", String.valueOf(price * 100));
        vnpParams.put("vnp_ReturnUrl", returnUrl);
        vnpParams.put("vnp_IpAddr", ipAddr);
        vnpParams.put("vnp_CreateDate", createDate);
        vnpParams = sortObject(vnpParams);
        String signData = String.join("&", vnpParams.entrySet().stream().map(e -> e.getKey() + "=" + e.getValue()).toArray(String[]::new));
        String hmac = SecurityUtil.hmacSHA512(secretKey, signData);
        vnpParams.put("vnp_SecureHash", hmac);
        String query = vnpParams.entrySet().stream().map(e -> e.getKey() + "=" + e.getValue()).reduce((e1, e2) -> e1 + "&" + e2).orElse("");
        return vnpUrl + "?" + query;
    }

    @Transactional
    public VNPayReturnResponseDTO handleVnPayReturn(Map<String, String> vnpParams) {
        long orderId = -1;
        String status = vnpParams.get("vnp_ResponseCode");
        String secureHash = vnpParams.get("vnp_SecureHash");
        vnpParams.remove("vnp_SecureHash");
        vnpParams.remove("vnp_SecureHashType");
        vnpParams = sortObject(vnpParams);
        String signData = String.join("&", vnpParams.entrySet().stream().map(e -> e.getKey() + "=" + e.getValue()).toArray(String[]::new));
        String hmac = SecurityUtil.hmacSHA512(secretKey, signData);
        if (hmac.equals(secureHash)) {
            orderId = Long.parseLong(vnpParams.get("vnp_TxnRef"));
            if (status != null && status.equals("00"))
                return new VNPayReturnResponseDTO(orderId, 0);

        }
        return new VNPayReturnResponseDTO(-1, 99);

    }

    private Map<String, String> sortObject(Map<String, String> obj) {
        Map<String, String> sorted = new TreeMap<>(obj);
        Map<String, String> encoded = new TreeMap<>();
        for (Map.Entry<String, String> entry : sorted.entrySet()) {
            encoded.put(entry.getKey(), URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8).replace("%20", "+"));
        }
        return encoded;
    }


}
