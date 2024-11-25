package com.example.dissertation_backend.solution.Addresses.Controller;

import com.example.dissertation_backend.solution.Addresses.Model.Address;
import com.example.dissertation_backend.solution.Addresses.Service.AddressService;
import com.example.dissertation_backend.solution.Customers.Model.ApplicationUser;
import java.security.Principal;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/addresses")
@CrossOrigin(
  origins = { "*" },
  methods = {
    RequestMethod.OPTIONS,
    RequestMethod.PATCH,
    RequestMethod.GET,
    RequestMethod.PUT,
    RequestMethod.DELETE,
    RequestMethod.POST,
  }
)
public class AddressController {

  @Autowired
  private AddressService addressService;

  @GetMapping
  public List<Address> getAllAddresses() {
    return addressService.findAllAddresses();
  }

  @GetMapping("/my-addresses")
  public ResponseEntity<?> getCurrentUserAddresses() {
    List<Address> addresses = addressService.findAddressesForCurrentUser();
    return ResponseEntity.ok(addresses);
  }

  @GetMapping("/{id}")
  public ResponseEntity<?> getAddressById(@PathVariable Integer id) {
    Optional<Address> address = addressService.findAddressById(id);
    if (address != null) {
      return ResponseEntity.ok(address);
    } else {
      return ResponseEntity.notFound().build();
    }
  }

  @PostMapping
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<?> createAddress(@RequestBody Address address) {
    Optional<ApplicationUser> user = addressService.getCurrentUser();
    if (user.isPresent()) {
      address.setUser(user.get());
      Address savedAddress = addressService.saveAddress(address);
      return ResponseEntity.ok(savedAddress);
    }
    return ResponseEntity.status(403).body("User not authenticated");
  }

  @PutMapping("/{id}")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<?> updateAddress(
    @PathVariable Integer id,
    @RequestBody Address addressDetails
  ) {
    Optional<ApplicationUser> user = addressService.getCurrentUser();
    if (user.isPresent()) {
      Optional<Address> existingAddress = addressService.findAddressById(id);
      if (
        existingAddress.isPresent() &&
        existingAddress.get().getUser().equals(user.get())
      ) {
        existingAddress.get().setCountry(addressDetails.getCountry());
        existingAddress.get().setPostcode(addressDetails.getPostcode());
        existingAddress.get().setAddressLine1(addressDetails.getAddressLine1());
        existingAddress.get().setAddressLine2(addressDetails.getAddressLine2());
        existingAddress.get().setCity(addressDetails.getCity());
        existingAddress.get().setCounty(addressDetails.getCounty());
        final Address updatedAddress = addressService.saveAddress(
          existingAddress.get()
        );
        return ResponseEntity.ok(updatedAddress);
      }
      return ResponseEntity
        .status(403)
        .body("Unauthorized to update this address");
    }
    return ResponseEntity.status(403).body("User not authenticated");
  }

  @PutMapping("/set-default/{addressId}")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<Address> setDefaultAddress(
    Principal principal,
    @PathVariable Integer addressId
  ) {
    if (principal == null) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
    }

    String username = principal.getName();
    Address address = addressService.setDefaultAddress(username, addressId);
    if (address != null) {
      return ResponseEntity.ok(address);
    } else {
      return ResponseEntity.notFound().build();
    }
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<?> deleteAddress(@PathVariable Integer id) {
    Optional<ApplicationUser> user = addressService.getCurrentUser();
    if (user.isPresent()) {
      Optional<Address> address = addressService.findAddressById(id);
      if (address.isPresent() && address.get().getUser().equals(user.get())) {
        addressService.deleteAddress(id);
        return ResponseEntity.ok().build();
      }
      return ResponseEntity
        .status(403)
        .body("Unauthorized to delete this address");
    }
    return ResponseEntity.status(403).body("User not authenticated");
  }
}
