package com.example.bitadressgenerator.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.OffsetDateTime;

@Entity
@Table(name = "btc_f_address")
@Data
public class BtcAddressEntinty {

    @Id
    @Column(name = "id")
    private String address;

    @Column(name = "private_key")
    private String privateKey;

    @Column(name = "created_date", nullable = false, updatable = false)
    @CreatedDate
    private OffsetDateTime createdDate;

    @Column(name = "modified_date")
    @LastModifiedDate
    private OffsetDateTime modifiedDate;
}
