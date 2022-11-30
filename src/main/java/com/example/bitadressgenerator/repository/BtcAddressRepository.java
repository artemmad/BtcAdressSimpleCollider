package com.example.bitadressgenerator.repository;

import com.example.bitadressgenerator.model.BtcAddressEntinty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BtcAddressRepository extends JpaRepository<BtcAddressEntinty, String> {
}
