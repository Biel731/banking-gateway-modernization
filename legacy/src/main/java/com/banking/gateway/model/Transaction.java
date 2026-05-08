package com.banking.gateway.model;

import org.apache.log4j.Logger;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/* Transaction - Central gateway entity -> Represents a financial transaction */

/**
 * - Verbose POJO: manual getters and setters for all fields
 * - @Temporal without timezone — causes subtle bugs in environments with different time zones
 * - No correct equals/hashCode — Hibernate may behave unpredictably
 * - Static logger coupled to the class — impossible to mock in tests.
 */

@Entity
@Table(name = "transactions")
public class Transaction {

    private static final Logger logger = Logger.getLogger(Transaction.class);

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "transaction_seq")
    @SequenceGenerator(name = "transaction_seq", sequenceName = "transaction_seq", allocationSize = 1)

    private Long id;

    @Column(name = "external_id", nullable = false, unique = true)
    private String externalId;

    @Column(name = "partner_code", nullable = false)
    private String partnerCode;

    @Column(name = "amount", precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(name = "status", nullable = false)
    private String status;  // "PENDING", "APPROVED", "REJECTED", "ERROR"
                            // problema central: os status ficam "soltos", ou seja,
                            // sem validação, sem enum para guardar os valores e etc.

    @Column(name = "created_at", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @Column(name = "updated_at", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;

    @Column(name = "raw_response", nullable = false, columnDefinition = "TEXT")
    private String rawResponse; // O dado vem bruto do parceiro guardado como String,
                                // sem tipagem, sem parcing estruturado

    // Construtor padrão obrigatório do Hibernate
    public Transaction() {
        this.externalId = externalId;
        this.partnerCode = partnerCode;
        this.amount = amount;
        this.status = "PENDING";
        this.createdAt = new Date();
    }

    // Getters e setters manuais - boilerplate que o Java 17 alimina o com record
    public Long getId() { return id; }
    public void setId(Long id) {this.id = id;}

    public String getExternalId() { return externalId; }
    public void setExternalId(String externalId) {this.externalId = externalId; }

    public String getPartnerCode() { return partnerCode; }
    public void setPartnerCode(String partnerCode) { this.partnerCode = partnerCode; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public Date getUpdateAt() { return updatedAt; }
    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }

    public String getRawResponse() { return rawResponse; }
    public void setRawResponse(String rawResponse) {this.rawResponse = rawResponse;}
}
