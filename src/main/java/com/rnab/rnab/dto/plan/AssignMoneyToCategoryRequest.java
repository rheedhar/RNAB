package com.rnab.rnab.dto.plan;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class AssignMoneyToCategoryRequest {

    @NotNull(message = "Amount to assign is required")
    @DecimalMin(value = "0.00", message = "Amount must be 0 or greater")
    private BigDecimal amountToAssign;

    public BigDecimal getAmountToAssign() {
        return amountToAssign;
    }

    public void setAmountToAssign(BigDecimal amountToAssign) {
        this.amountToAssign = amountToAssign;
    }
}
