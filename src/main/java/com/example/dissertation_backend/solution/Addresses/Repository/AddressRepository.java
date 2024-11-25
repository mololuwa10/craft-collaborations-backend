package com.example.dissertation_backend.solution.Addresses.Repository;

import com.example.dissertation_backend.solution.Addresses.Model.Address;
import com.example.dissertation_backend.solution.Customers.Model.ApplicationUser;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AddressRepository extends JpaRepository<Address, Integer> {
  List<Address> findByUser(ApplicationUser user);
  List<Address> findByUserUserId(Integer userId);
  Optional<Address> findByAddressId(Integer addressId);
  List<Address> findByUserUserIdAndIsDefault(Integer userId, boolean isDefault);
}
