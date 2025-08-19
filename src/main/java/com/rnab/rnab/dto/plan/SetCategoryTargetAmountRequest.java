package com.rnab.rnab.dto.plan;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class SetCategoryTargetAmountRequest {
    @NotNull(message = "Target amount is required")
    @DecimalMin(value = "0.00", message = "Target amount must be 0 or greater")
    private BigDecimal targetAmount;

    public BigDecimal getTargetAmount() {
        return targetAmount;
    }

    public void setTargetAmount(BigDecimal targetAmount) {
        this.targetAmount = targetAmount;
    }
}
