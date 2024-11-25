package com.example.dissertation_backend.solution.Orders.Repository;

import com.example.dissertation_backend.solution.Customers.Model.ApplicationUser;
import com.example.dissertation_backend.solution.Orders.Model.Orders;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrdersRepository extends JpaRepository<Orders, Long> {
  List<Orders> findByUserId(ApplicationUser user);
  Optional<Orders> findByIdAndUserId(Long id, ApplicationUser user);
}
