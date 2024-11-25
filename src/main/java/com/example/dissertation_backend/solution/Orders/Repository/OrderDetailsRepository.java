package com.example.dissertation_backend.solution.Orders.Repository;

import com.example.dissertation_backend.solution.Customers.Model.ApplicationUser;
import com.example.dissertation_backend.solution.Orders.Model.OrderDetails;
import com.example.dissertation_backend.solution.Orders.Model.Orders;
import com.example.dissertation_backend.solution.Products.Model.Products;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderDetailsRepository
  extends JpaRepository<OrderDetails, Integer> {
  List<OrderDetails> findByProduct_Artisan_ArtisanId(Integer artisanId);

  List<OrderDetails> findByOrder_UserIdAndProductAndOrder_Status(
    ApplicationUser user,
    Products product,
    Orders.Status status
  );

  @Query(
    "SELECT od.product, SUM(od.quantity) as totalQuantity FROM OrderDetails od WHERE od.order.status = 'DELIVERED' GROUP BY od.product"
  )
  List<Object[]> findProductSales();

  @Query(
    "SELECT od.product, SUM(od.quantity) AS totalQuantity, COUNT(DISTINCT od.order) AS totalOrders " +
    "FROM OrderDetails od " +
    "WHERE od.order.status = 'DELIVERED' " +
    "GROUP BY od.product " +
    "ORDER BY totalQuantity DESC"
  )
  List<Object[]> findProductSalesAndOrderCount();

  @Query(
    "SELECT od.product, SUM(od.quantity) AS totalQuantity, COUNT(DISTINCT od.order) AS totalOrders " +
    "FROM OrderDetails od " +
    "WHERE od.order.status = 'DELIVERED' AND od.product.artisan.artisan.username = :username " +
    "GROUP BY od.product " +
    "ORDER BY totalQuantity DESC"
  )
  List<Object[]> findProductSalesAndOrderCountByUsername(String username);

  @Query(
    "SELECT SUM(od.priceAtOrder * od.quantity), YEAR(od.order.orderDateTime), MONTH(od.order.orderDateTime), DAY(od.order.orderDateTime) " +
    "FROM OrderDetails od " +
    "WHERE od.order.orderDateTime BETWEEN :startDate AND :endDate " +
    "AND od.product.artisan.artisan.username = :username " +
    "GROUP BY YEAR(od.order.orderDateTime), MONTH(od.order.orderDateTime), DAY(od.order.orderDateTime) " +
    "ORDER BY YEAR(od.order.orderDateTime), MONTH(od.order.orderDateTime), DAY(od.order.orderDateTime) ASC"
  )
  List<Object[]> findSalesByDateRangeGroupedByDayForArtisan(
    LocalDateTime startDate,
    LocalDateTime endDate,
    String username
  );

  @Query(
    "SELECT od.product.artisan, SUM(od.priceAtOrder * od.quantity) AS totalSales, COUNT(DISTINCT od.order) AS totalOrders " +
    "FROM OrderDetails od WHERE od.order.status = 'DELIVERED' GROUP BY od.product.artisan ORDER BY totalSales DESC"
  )
  List<Object[]> findSalesByArtisan();
}
