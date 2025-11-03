package com.billgenpro.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.billgenpro.model.Invoice;
import com.billgenpro.model.User;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    
    @Query("SELECT i FROM Invoice i ORDER BY i.date DESC")
    List<Invoice> findAllOrderByDateDesc();
    
    @Query("SELECT i FROM Invoice i WHERE i.user = :user ORDER BY i.date DESC")
    List<Invoice> findByUserOrderByDateDesc(@Param("user") User user);
    
    @Query("SELECT DISTINCT i FROM Invoice i LEFT JOIN FETCH i.items WHERE i.id = :id")
    Optional<Invoice> findByIdWithItems(Long id);
    
    @Query("SELECT DISTINCT i FROM Invoice i LEFT JOIN FETCH i.items WHERE i.id = :id AND i.user = :user")
    Optional<Invoice> findByIdAndUserWithItems(@Param("id") Long id, @Param("user") User user);
    
    @Query("SELECT i FROM Invoice i WHERE i.user = :user AND i.number LIKE %:number%")
    List<Invoice> findByUserAndNumberContainingIgnoreCase(@Param("user") User user, @Param("number") String number);
    
    List<Invoice> findByNumberContainingIgnoreCase(String number);
    
    boolean existsByNumber(String number);
    
    @Query("SELECT COUNT(i) > 0 FROM Invoice i WHERE i.number = :number AND i.user = :user")
    boolean existsByNumberAndUser(@Param("number") String number, @Param("user") User user);
}