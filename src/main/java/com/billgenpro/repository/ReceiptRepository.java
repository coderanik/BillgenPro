package com.billgenpro.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.billgenpro.model.Receipt;
import com.billgenpro.model.User;

@Repository
public interface ReceiptRepository extends JpaRepository<Receipt, Long> {
    
    @Query("SELECT r FROM Receipt r ORDER BY r.date DESC")
    List<Receipt> findAllOrderByDateDesc();
    
    @Query("SELECT r FROM Receipt r WHERE r.user = :user ORDER BY r.date DESC")
    List<Receipt> findByUserOrderByDateDesc(@Param("user") User user);
    
    @Query("SELECT r FROM Receipt r WHERE r.user = :user AND r.number LIKE %:number%")
    List<Receipt> findByUserAndNumberContainingIgnoreCase(@Param("user") User user, @Param("number") String number);
    
    @Query("SELECT r FROM Receipt r WHERE r.id = :id AND r.user = :user")
    Optional<Receipt> findByIdAndUser(@Param("id") Long id, @Param("user") User user);
    
    @Query("SELECT DISTINCT r FROM Receipt r LEFT JOIN FETCH r.items WHERE r.id = :id AND r.user = :user")
    Optional<Receipt> findByIdAndUserWithItems(@Param("id") Long id, @Param("user") User user);
    
    List<Receipt> findByNumberContainingIgnoreCase(String number);
    
    boolean existsByNumber(String number);
    
    @Query("SELECT COUNT(r) > 0 FROM Receipt r WHERE r.number = :number AND r.user = :user")
    boolean existsByNumberAndUser(@Param("number") String number, @Param("user") User user);
}