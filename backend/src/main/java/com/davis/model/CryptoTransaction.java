package com.davis.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
public class CryptoTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "case_id", nullable = false)
    private Long caseId;

    @Column(name = "wallet_from", nullable = false, length = 128)
    private String walletFrom;

    @Column(name = "wallet_to", nullable = false, length = 128)
    private String walletTo;

    @Column(nullable = false)
    private Double amount;

    @Column(length = 32)
    private String asset = "BTC";

    private LocalDateTime timestamp = LocalDateTime.now();

    @Column(name = "transaction_hash", nullable = false, length = 128)
    private String transactionHash;

    public CryptoTransaction() {}

    public CryptoTransaction(Long caseId, String walletFrom, String walletTo, Double amount, String asset, LocalDateTime timestamp, String transactionHash) {
        this.caseId = caseId;
        this.walletFrom = walletFrom;
        this.walletTo = walletTo;
        this.amount = amount;
        this.asset = asset;
        this.timestamp = timestamp != null ? timestamp : LocalDateTime.now();
        this.transactionHash = transactionHash;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getCaseId() { return caseId; }
    public void setCaseId(Long caseId) { this.caseId = caseId; }

    public String getWalletFrom() { return walletFrom; }
    public void setWalletFrom(String walletFrom) { this.walletFrom = walletFrom; }

    public String getWalletTo() { return walletTo; }
    public void setWalletTo(String walletTo) { this.walletTo = walletTo; }

    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }

    public String getAsset() { return asset; }
    public void setAsset(String asset) { this.asset = asset; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public String getTransactionHash() { return transactionHash; }
    public void setTransactionHash(String transactionHash) { this.transactionHash = transactionHash; }
}
