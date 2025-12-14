package com.bom.dsa.repository;

import com.bom.dsa.entity.Lead;
import com.bom.dsa.enums.LeadStatus;
import com.bom.dsa.enums.ProductType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for Lead entity operations.
 * Provides methods for CRUD and search operations on leads.
 */
@Repository
public interface LeadRepository extends JpaRepository<Lead, UUID> {

        /**
         * Find lead by application reference number.
         * 
         * @param applicationReferenceNumber the reference number
         * @return optional lead
         */
        Optional<Lead> findByApplicationReferenceNumber(String applicationReferenceNumber);

        /**
         * Find leads by creator (DSA user) with soft delete filter.
         * 
         * @param createdBy the creator username
         * @param pageable  pagination info
         * @return page of leads
         */
        Page<Lead> findByCreatedByAndIsDeletedFalse(String createdBy, Pageable pageable);

        /**
         * Find leads by status with soft delete filter.
         * 
         * @param status   the lead status
         * @param pageable pagination info
         * @return page of leads
         */
        Page<Lead> findByStatusAndIsDeletedFalse(LeadStatus status, Pageable pageable);

        /**
         * Find leads by product type with soft delete filter.
         * 
         * @param productType the product type
         * @param pageable    pagination info
         * @return page of leads
         */
        Page<Lead> findByProductTypeAndIsDeletedFalse(ProductType productType, Pageable pageable);

        /**
         * Find leads by creator and status with soft delete filter.
         * 
         * @param createdBy the creator username
         * @param status    the lead status
         * @param pageable  pagination info
         * @return page of leads
         */
        Page<Lead> findByCreatedByAndStatusAndIsDeletedFalse(String createdBy, LeadStatus status, Pageable pageable);

        /**
         * Search leads with filters using JPQL with JOIN FETCH for eager loading.
         * 
         * @param createdBy   the creator username
         * @param status      optional status filter
         * @param productType optional product type filter
         * @param searchTerm  optional search term
         * @param pageable    pagination info
         * @return page of leads
         */
        @Query("SELECT DISTINCT l FROM Lead l " +
                        "LEFT JOIN FETCH l.basicDetails bd " +
                        "WHERE l.createdBy = :createdBy " +
                        "AND l.isDeleted = false " +
                        "AND (:status IS NULL OR l.status = :status) " +
                        "AND (:productType IS NULL OR l.productType = :productType) " +
                        "AND (:searchTerm IS NULL OR l.applicationReferenceNumber LIKE %:searchTerm% " +
                        "OR bd.firstName LIKE %:searchTerm% " +
                        "OR bd.lastName LIKE %:searchTerm% " +
                        "OR bd.mobileNumber LIKE %:searchTerm%)")
        Page<Lead> searchLeads(
                        @Param("createdBy") String createdBy,
                        @Param("status") LeadStatus status,
                        @Param("productType") ProductType productType,
                        @Param("searchTerm") String searchTerm,
                        Pageable pageable);

        /**
         * Count leads by creator with soft delete filter.
         * 
         * @param createdBy the creator username
         * @return count of leads
         */
        Long countByCreatedByAndIsDeletedFalse(String createdBy);

        /**
         * Count leads by creator and status with soft delete filter.
         * 
         * @param createdBy the creator username
         * @param status    the lead status
         * @return count of leads
         */
        Long countByCreatedByAndStatusAndIsDeletedFalse(String createdBy, LeadStatus status);

        /**
         * Count leads by status with soft delete filter.
         * 
         * @param status the lead status
         * @return count of leads
         */
        Long countByStatusAndIsDeletedFalse(LeadStatus status);

        /**
         * Get recent leads for dashboard with eager loading.
         * 
         * @param createdBy the creator username
         * @param pageable  pagination info
         * @return list of recent leads
         */
        @Query("SELECT l FROM Lead l " +
                        "LEFT JOIN FETCH l.basicDetails " +
                        "WHERE l.createdBy = :createdBy AND l.isDeleted = false " +
                        "ORDER BY l.createdAt DESC")
        List<Lead> findRecentLeads(@Param("createdBy") String createdBy, Pageable pageable);

        /**
         * Get lead with all details eagerly loaded.
         * 
         * @param id the lead ID
         * @return optional lead with details
         */
        @Query("SELECT l FROM Lead l " +
                        "LEFT JOIN FETCH l.basicDetails " +
                        "LEFT JOIN FETCH l.occupationDetails " +
                        "LEFT JOIN FETCH l.financialDetails " +
                        "LEFT JOIN FETCH l.vehicleLoanDetails " +
                        "LEFT JOIN FETCH l.educationLoanDetails " +
                        "LEFT JOIN FETCH l.homeLoanDetails " +
                        "LEFT JOIN FETCH l.loanAgainstPropertyDetails " +
                        "WHERE l.id = :id AND l.isDeleted = false")
        Optional<Lead> findByIdWithDetails(@Param("id") UUID id);

        /**
         * Find leads created between dates with soft delete filter.
         * 
         * @param startDate start date
         * @param endDate   end date
         * @return list of leads
         */
        List<Lead> findByCreatedAtBetweenAndIsDeletedFalse(Instant startDate, Instant endDate);

        /**
         * Count by product type for analytics.
         * 
         * @param createdBy the creator username
         * @return list of product type counts
         */
        @Query("SELECT l.productType, COUNT(l) FROM Lead l WHERE l.createdBy = :createdBy AND l.isDeleted = false GROUP BY l.productType")
        List<Object[]> countByCreatedByGroupByProductType(@Param("createdBy") String createdBy);

        /**
         * Count by status for analytics.
         * 
         * @param createdBy the creator username
         * @return list of status counts
         */
        @Query("SELECT l.status, COUNT(l) FROM Lead l WHERE l.createdBy = :createdBy AND l.isDeleted = false GROUP BY l.status")
        List<Object[]> countByCreatedByGroupByStatus(@Param("createdBy") String createdBy);
}
