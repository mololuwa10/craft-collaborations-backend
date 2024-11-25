package com.example.dissertation_backend.solution.Customers.Model;

import com.example.dissertation_backend.solution.Addresses.Model.Address;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import org.springframework.security.core.userdetails.UserDetails;

@Entity
@Table(name = "users")
public class ApplicationUser implements UserDetails {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "user_id")
  private Integer userId;

  @Column(name = "firstname")
  public String firstname;

  @Column(name = "lastname")
  public String lastname;

  @Column(name = "user_email", unique = true)
  public String userEmail;

  @Column(name = "username", unique = true)
  private String username;

  @Column(name = "user_password")
  private String password;

  @Column(name = "bank_account_no")
  private String bankAccountNo;

  @Column(name = "bank_sort_code")
  private String bankSortCode;

  @Column(name = "contact_telephone")
  private String contactTelephone;

  @Column(name = "contact_address")
  private String contactAddress;

  @ManyToMany(fetch = FetchType.EAGER)
  @JoinTable(
    name = "user_role_junction",
    joinColumns = { @JoinColumn(name = "user_id") },
    inverseJoinColumns = { @JoinColumn(name = "role_id") }
  )
  private Set<Roles> authorities;

  @Column(name = "date_joined")
  private LocalDateTime dateJoined;

  @OneToOne(
    mappedBy = "artisan",
    cascade = CascadeType.ALL,
    fetch = FetchType.LAZY,
    optional = true
  )
  @JsonBackReference
  private ArtisanProfile artisanProfile;

  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
  @JsonManagedReference
  private Set<Address> addresses = new HashSet<>();

  @Column(name = "enabled")
  private boolean enabled;

  public ApplicationUser() {
    super();
    authorities = new HashSet<Roles>();
  }

  public Set<Address> getAddresses() {
    return addresses;
  }

  public ApplicationUser(ArtisanProfile artisanProfile) {
    super();
    this.artisanProfile = artisanProfile;
  }

  public ApplicationUser(
    Integer userId,
    String firstname,
    String lastname,
    String user_email,
    String username,
    String password,
    String bankAccountNo,
    String bankSortCode,
    String contactTelephone,
    String contactAddress,
    Set<Roles> authorities,
    LocalDateTime dateJoined,
    boolean enabled
  ) {
    this.userId = userId;
    this.firstname = firstname;
    this.lastname = lastname;
    this.userEmail = user_email;
    this.username = username;
    this.password = password;
    this.bankAccountNo = bankAccountNo;
    this.bankSortCode = bankSortCode;
    this.contactTelephone = contactTelephone;
    this.contactAddress = contactAddress;
    this.authorities = authorities;
    this.dateJoined = dateJoined;
    this.enabled = enabled;
  }

  // Getters and Setters
  public Integer getUserId() {
    return userId;
  }

  public void setUserId(Integer userId) {
    this.userId = userId;
  }

  public String getFirstname() {
    return firstname;
  }

  public void setFirstname(String firstname) {
    this.firstname = firstname;
  }

  public String getLastname() {
    return lastname;
  }

  public void setLastname(String lastname) {
    this.lastname = lastname;
  }

  public String getUser_email() {
    return userEmail;
  }

  public void setUser_email(String user_email) {
    this.userEmail = user_email;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getBankAccountNo() {
    return bankAccountNo;
  }

  public void setBankAccountNo(String bankAccountNo) {
    this.bankAccountNo = bankAccountNo;
  }

  public String getBankSortCode() {
    return bankSortCode;
  }

  public void setBankSortCode(String bankSortCode) {
    this.bankSortCode = bankSortCode;
  }

  public String getContactTelephone() {
    return contactTelephone;
  }

  public void setContactTelephone(String contactTelephone) {
    this.contactTelephone = contactTelephone;
  }

  public String getContactAddress() {
    return contactAddress;
  }

  public void setContactAddress(String contactAddress) {
    this.contactAddress = contactAddress;
  }

  public Set<Roles> getAuthorities() {
    return authorities;
  }

  public void setAuthorities(Set<Roles> authorities) {
    this.authorities = authorities;
  }

  public LocalDateTime getDateJoined() {
    return dateJoined;
  }

  public void setDateJoined(LocalDateTime dateJoined) {
    this.dateJoined = dateJoined;
  }

  // Standard getters and setters
  public ArtisanProfile getArtisanProfile() {
    return artisanProfile;
  }

  public void setArtisanProfile(ArtisanProfile artisanProfile) {
    this.artisanProfile = artisanProfile;
  }

  public boolean getEnabled() {
    return enabled;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  public void setAddresses(Set<Address> addresses) {
    this.addresses = addresses;
  }

  public void addAddress(Address address) {
    addresses.add(address);
    address.setUser(this);
  }

  public void removeAddress(Address address) {
    addresses.remove(address);
    address.setUser(null);
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }
}
