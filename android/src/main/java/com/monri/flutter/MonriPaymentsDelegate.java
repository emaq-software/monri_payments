package com.monri.flutter;

import android.content.Intent;

import com.monri.android.Monri;
import com.monri.android.ResultCallback;
import com.monri.android.model.PaymentResult;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.PluginRegistry;

public class MonriPaymentsDelegate implements PluginRegistry.ActivityResultListener, ResultCallback<PaymentResult> {
    private Monri monri;
    private MethodChannel.Result confirmPaymentResult;

    MonriPaymentsDelegate(Monri monri) {
        this.monri = monri;
    }

    @Override
    public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
        if (this.monri != null) {
            return this.monri.onPaymentResult(requestCode, data, this);
        }

        return false;
    }

    @Override
    public void onSuccess(PaymentResult result) {
        if (confirmPaymentResult != null) {
            Map<String, Object> response = new HashMap<>();
            Map<String, Object> data = new HashMap<>();
            final String status = result.getStatus();
            data.put("status", result.getStatus());
            data.put("amount", result.getAmount());
            data.put("order_number", result.getOrderNumber());
            data.put("transaction_type", result.getTransactionType());
            data.put("pan_token", result.getPanToken());
            List<String> errors = result.getErrors();
            if (errors != null) {
                data.put("errors", errors);
            }
            data.put("created_at", result.getCreatedAt());
            data.put("amount", result.getAmount());
            data.put("currency", result.getCurrency());
            response.put("status", status);
            response.put("data", data);
            confirmPaymentResult.success(response);
        }
    }

    @Override
    public void onError(Throwable throwable) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "error");
        this.confirmPaymentResult.success(response);
    }

    public void setMonri(Monri monri) {
        this.monri = monri;
    }

    void setConfirmPaymentResult(MethodChannel.Result confirmPaymentResult) {
        this.confirmPaymentResult = confirmPaymentResult;
    }
}
