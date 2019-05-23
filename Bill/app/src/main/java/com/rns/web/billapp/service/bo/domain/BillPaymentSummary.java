package com.rns.web.billapp.service.bo.domain;

import java.math.BigDecimal;

/**
 * Created by Admin on 23/01/2019.
 */

public class BillPaymentSummary {

    private Long totalCustomers;
    private BigDecimal totalPaid;
    private BigDecimal totalGenerated;
    private Long totalInvoices;
    private Long paidInvoices;
    private BigDecimal pendingSettlements;
    private BigDecimal completedSettlements;
    private Long pendingInvoices;
    private BigDecimal pendingAmount;
    private Long onlineInvoices;
    private BigDecimal onlinePaid;
    private Long offlineInvoices;
    private BigDecimal offlinePaid;
    private BigDecimal totalProfit;
    private String timeSaved;
    private String moneySaved;
    private String totalCollection;
    private String monthlyCollection;
    private String pendingTotal;

    public BigDecimal getTotalPaid() {
        return totalPaid;
    }

    public void setTotalPaid(BigDecimal totalPaid) {
        this.totalPaid = totalPaid;
    }

    public BigDecimal getTotalGenerated() {
        return totalGenerated;
    }

    public void setTotalGenerated(BigDecimal totalGenerated) {
        this.totalGenerated = totalGenerated;
    }

    public Long getTotalInvoices() {
        return totalInvoices;
    }

    public void setTotalInvoices(Long totalInvoices) {
        this.totalInvoices = totalInvoices;
    }

    public Long getPaidInvoices() {
        return paidInvoices;
    }

    public void setPaidInvoices(Long paidInvoices) {
        this.paidInvoices = paidInvoices;
    }

    public BigDecimal getPendingSettlements() {
        return pendingSettlements;
    }

    public void setPendingSettlements(BigDecimal pendingSettlements) {
        this.pendingSettlements = pendingSettlements;
    }

    public BigDecimal getCompletedSettlements() {
        return completedSettlements;
    }

    public void setCompletedSettlements(BigDecimal completedSettlements) {
        this.completedSettlements = completedSettlements;
    }

    public Long getPendingInvoices() {
        return pendingInvoices;
    }

    public void setPendingInvoices(Long pendingInvoices) {
        this.pendingInvoices = pendingInvoices;
    }

    public BigDecimal getPendingAmount() {
        return pendingAmount;
    }

    public void setPendingAmount(BigDecimal pendingAmount) {
        this.pendingAmount = pendingAmount;
    }

    public Long getOnlineInvoices() {
        return onlineInvoices;
    }

    public void setOnlineInvoices(Long onlineInvoices) {
        this.onlineInvoices = onlineInvoices;
    }

    public BigDecimal getOnlinePaid() {
        return onlinePaid;
    }

    public void setOnlinePaid(BigDecimal onlinePaid) {
        this.onlinePaid = onlinePaid;
    }

    public Long getOfflineInvoices() {
        return offlineInvoices;
    }

    public void setOfflineInvoices(Long offlineInvoices) {
        this.offlineInvoices = offlineInvoices;
    }

    public BigDecimal getOfflinePaid() {
        return offlinePaid;
    }

    public void setOfflinePaid(BigDecimal offlinePaid) {
        this.offlinePaid = offlinePaid;
    }

    public BigDecimal getTotalProfit() {
        return totalProfit;
    }

    public void setTotalProfit(BigDecimal totalProfit) {
        this.totalProfit = totalProfit;
    }

    public String getMonthlyCollection() {
        return monthlyCollection;
    }

    public void setMonthlyCollection(String monthlyCollection) {
        this.monthlyCollection = monthlyCollection;
    }

    public String getTotalCollection() {
        return totalCollection;
    }

    public String getMoneySaved() {
        return moneySaved;
    }

    public void setMoneySaved(String moneySaved) {
        this.moneySaved = moneySaved;
    }

    public String getTimeSaved() {
        return timeSaved;
    }

    public void setTimeSaved(String timeSaved) {
        this.timeSaved = timeSaved;
    }

    public void setTotalCollection(String totalCollection) {
        this.totalCollection = totalCollection;
    }

    public String getPendingTotal() {
        return pendingTotal;
    }

    public void setPendingTotal(String pendingTotal) {
        this.pendingTotal = pendingTotal;
    }

    public Long getTotalCustomers() {
        return totalCustomers;
    }

    public void setTotalCustomers(Long totalCustomers) {
        this.totalCustomers = totalCustomers;
    }
}
