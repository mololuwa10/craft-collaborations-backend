package com.example.dissertation_backend.solution.DTO;

import com.example.dissertation_backend.solution.Customers.Model.ApplicationUser;
import com.example.dissertation_backend.solution.Customers.Model.Roles;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

public class ApplicationUserDTO {

  private Integer userId;
  private String firstname;
  private String lastname;
  private String username;
  private String user_email;
  private String bankAccountNo;
  private String bankSortCode;
  private String contactTelephone;
  private String contactAddress;
  private String password;
  private Set<Roles> authorities;
  private LocalDateTime dateJoined;

  private ApplicationUser user;
  private ArtisanProfileDTO artisanProfileDTO;

  public ApplicationUserDTO(
    ApplicationUser user,
    ArtisanProfileDTO artisanProfileDTO
  ) {
    this.user = user;
    this.artisanProfileDTO = artisanProfileDTO;
  }

  public ApplicationUserDTO() {
    super();
    authorities = new HashSet<Roles>();
  }

  public ApplicationUserDTO(
    String firstname,
    String lastname,
    String user_email,
    String username,
    String bankAccountNo,
    String bankSortCode,
    String contactTelephone,
    String contactAddress,
    String password,
    Set<Roles> authorities,
    LocalDateTime dateJoined
  ) {
    this.firstname = firstname;
    this.lastname = lastname;
    this.user_email = user_email;
    this.bankAccountNo = bankAccountNo;
    this.bankSortCode = bankSortCode;
    this.contactTelephone = contactTelephone;
    this.contactAddress = contactAddress;
    this.username = username;
    this.password = password;
    this.authorities = authorities;
    this.dateJoined = dateJoined;
  }

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
    return user_email;
  }

  public void setUser_email(String user_email) {
    this.user_email = user_email;
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

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
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

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public ApplicationUser getUser() {
    return user;
  }

  public void setUser(ApplicationUser user) {
    this.user = user;
  }

  public ArtisanProfileDTO getArtisanProfileDTO() {
    return artisanProfileDTO;
  }

  public void setArtisanProfileDTO(ArtisanProfileDTO artisanProfileDTO) {
    this.artisanProfileDTO = artisanProfileDTO;
  }
}
