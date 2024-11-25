package com.example.dissertation_backend.solution.Addresses.Service;

import com.example.dissertation_backend.solution.Addresses.Model.Address;
import com.example.dissertation_backend.solution.Addresses.Repository.AddressRepository;
import com.example.dissertation_backend.solution.Customers.Model.ApplicationUser;
import com.example.dissertation_backend.solution.Customers.Repository.UserRepository;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AddressService {

  @Autowired
  private AddressRepository addressRepository;

  @Autowired
  private UserRepository userRepository;

  public List<Address> findAllAddresses() {
    return addressRepository.findAll();
  }

  public Optional<Address> findAddressById(Integer id) {
    return addressRepository.findById(id);
  }

  public Address saveAddress(Address address) {
    return addressRepository.save(address);
  }

  public void deleteAddress(Integer id) {
    addressRepository.deleteById(id);
  }

  public Optional<ApplicationUser> getCurrentUser() {
    Authentication authentication = SecurityContextHolder
      .getContext()
      .getAuthentication();
    if (
      authentication != null &&
      authentication.isAuthenticated() &&
      !authentication.getPrincipal().equals("anonymousUser")
    ) {
      String username = authentication.getName();
      return userRepository.findByUsername(username);
    }
    return Optional.empty();
  }

  public List<Address> findAddressesForCurrentUser() {
    Authentication authentication = SecurityContextHolder
      .getContext()
      .getAuthentication();
    String username = authentication.getName();
    Optional<ApplicationUser> user = userRepository.findByUsername(username);
    if (user.isPresent()) {
      return addressRepository.findByUser(user.get());
    }
    return Collections.emptyList();
  }

  public Address setDefaultAddress(String username, Integer addressId) {
    ApplicationUser user = userRepository.findByUsername(username).orElse(null);
    if (user == null) {
      return null;
    }

    List<Address> allAddresses = addressRepository.findByUserUserId(
      user.getUserId()
    );
    Address defaultAddress = null;

    for (Address address : allAddresses) {
      if (address.getAddressId().equals(addressId)) {
        address.setDefault(true);
        defaultAddress = address;
      } else {
        address.setDefault(false);
      }
      addressRepository.save(address);
    }

    return defaultAddress;
  }
}
