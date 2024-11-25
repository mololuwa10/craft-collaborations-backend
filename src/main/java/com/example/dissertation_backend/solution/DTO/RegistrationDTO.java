package com.example.dissertation_backend.solution.DTO;

import java.time.LocalDateTime;

public class RegistrationDTO {

  private String firstname;
  private String lastname;
  private String user_email;
  private String username;
  private String password;
  private String contactAddress;
  private String contactTelephone;
  private String bankSortCode;
  private String bankAccountNo;
  private LocalDateTime dateJoined;
  private boolean enabled;

  private RegistrationDTO() {
    super();
  }

  private RegistrationDTO(
    String firstname,
    String lastname,
    String user_email,
    String username,
    String password,
    String contactAddress,
    String contactTelephone,
    String bankSortCode,
    String bankAccountNo,
    LocalDateTime dateJoined,
    boolean enabled
  ) {
    super();
    this.firstname = firstname;
    this.lastname = lastname;
    this.user_email = user_email;
    this.username = username;
    this.password = password;
    this.contactAddress = contactAddress;
    this.contactTelephone = contactTelephone;
    this.bankSortCode = bankSortCode;
    this.bankAccountNo = bankAccountNo;
    this.dateJoined = dateJoined;
    this.enabled = enabled;
  }

  // GETTERS AND SETTERS
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

  public String getContactAddress() {
    return contactAddress;
  }

  public void setContactAddress(String contactAddress) {
    this.contactAddress = contactAddress;
  }

  public String getContactTelephone() {
    return contactTelephone;
  }

  public void setContactTelephone(String contactTelephone) {
    this.contactTelephone = contactTelephone;
  }

  public String getBankSortCode() {
    return bankSortCode;
  }

  public void setBankSortCode(String bankSortCode) {
    this.bankSortCode = bankSortCode;
  }

  public String getBankAccountNo() {
    return bankAccountNo;
  }

  public void setBankAccountNo(String bankAccountNo) {
    this.bankAccountNo = bankAccountNo;
  }

  public LocalDateTime getDateJoined() {
    return dateJoined;
  }

  public void setDateJoined(LocalDateTime dateJoined) {
    this.dateJoined = dateJoined;
  }

  public boolean getEnabled() {
    return enabled;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  // TO STRING
  @Override
  public String toString() {
    return (
      "RegistrationDTO [firstname=" +
      firstname +
      ", lastname=" +
      lastname +
      ", user_email=" +
      user_email +
      ", username=" +
      username +
      ", password=" +
      password +
      ", contactAddress=" +
      contactAddress +
      ", contactTelephone=" +
      contactTelephone +
      ", bankSortCode=" +
      bankSortCode +
      ", bankAccountNo=" +
      bankAccountNo +
      ", dateJoined=" +
      dateJoined +
      "]"
    );
  }
}
