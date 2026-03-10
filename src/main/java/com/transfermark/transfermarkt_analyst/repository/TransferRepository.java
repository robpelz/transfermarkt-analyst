package com.transfermark.transfermarkt_analyst.repository;

import com.transfermark.transfermarkt_analyst.model.Transfer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TransferRepository extends JpaRepository<Transfer, Long> {

    List<Transfer> findByToClub(String club);

    List<Transfer> findByFromClub(String club);

    @Query("SELECT COUNT(t) FROM Transfer t WHERE t.toClub = :club")
    long countByToClub(@Param("club") String club);

    @Query("SELECT t.toClub, COUNT(t) as transferCount, " +
            "SUM(CASE WHEN t.wasSuccessful = true THEN 1 ELSE 0 END) as successCount " +
            "FROM Transfer t GROUP BY t.toClub")
    List<Object[]> getClubStatistics();

    List<Transfer> findBySeason(Integer season);

    List<Transfer> findByTransferFeeGreaterThan(Double fee);
}