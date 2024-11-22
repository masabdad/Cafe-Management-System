package com.cafe.management.DAO;

import com.cafe.management.POJO.Analysis.Analysis1;
import com.cafe.management.POJO.Analysis.Analysis2;
import com.cafe.management.POJO.Bill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.stream.Collectors;

public interface BillDao extends JpaRepository<Bill, Integer> {

    List<Bill> getAllBills();

    List<Bill> getBillByUserName(@Param("username") String username);

    @Query("SELECT new com.cafe.management.POJO.Analysis.Analysis1(b.createdBy, count(b.createdBy)) FROM Bill b GROUP BY b.createdBy")
    List<Analysis1> getAllOrderCountByUser();

    @Query("SELECT new com.cafe.management.POJO.Analysis.Analysis1(b.createdBy, sum(b.total)) FROM Bill b GROUP BY b.createdBy")
    List<Analysis1> getSpentMoneyTotalAll();

    @Query(value = "SELECT product_name, SUM(value) AS total_sales FROM (SELECT JSON_UNQUOTE(JSON_EXTRACT(productdetail, CONCAT('$[', idx, '].name'))) AS product_name, JSON_EXTRACT(productdetail, CONCAT('$[', idx, '].total')) AS value FROM bill CROSS JOIN (SELECT 0 AS idx UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3) AS idx WHERE idx < JSON_LENGTH(bill.productdetail)) AS subquery GROUP BY product_name", nativeQuery = true)
    List<Object[]> getProductSaleAll();



    default List<Analysis2> getProductSaleAllAsAnalysis2() {
        return getProductSaleAll().stream()
                .map(row -> new Analysis2((String) row[0], (Double) row[1]))
                .collect(Collectors.toList());
    }

    @Query(value = "SELECT product_name, SUM(value) as total_sales FROM (SELECT JSON_UNQUOTE(JSON_EXTRACT(productdetail, CONCAT('$[', idx, '].category'))) as product_name, JSON_EXTRACT(productdetail, CONCAT('$[', idx, '].total')) as value FROM bill JOIN (SELECT 0 as idx UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3) as idx WHERE idx < JSON_LENGTH(bill.productdetail)) as subquery GROUP BY product_name", nativeQuery = true)
    List<Analysis2> getCategorySaleAllAsAnalysis2();


}
