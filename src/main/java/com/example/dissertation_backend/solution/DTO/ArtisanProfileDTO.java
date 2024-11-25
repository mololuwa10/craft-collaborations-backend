package com.example.dissertation_backend.solution.DTO;

import java.util.List;

public class ArtisanProfileDTO {

  private Integer artisanId;
  private String bio;
  private String profilePicture;
  private String location;
  private String storeName;
  private String storeBanner;
  private String announcements;
  private String businessHours;
  private List<String> gallery;
  private String stories;
  private String specializations;
  private String materialsUsed;
  private String servicesOffered;
  private Integer experienceYears;
  private String shippingPolicies;
  private String returnPolicy;
  private String paymentOptions;
  private String termsConditions;
  private String privacyPolicy;
  private String communicationPreferences;
  private String preferredLanguage;

  // Fields from ApplicationUser
  private String firstname;
  private String lastname;
  private String user_email;
  private String bankAccountNo;
  private String bankSortCode;
  private String contactTelephone;
  private String contactAddress;

  public ArtisanProfileDTO() {
    super();
  }

  public ArtisanProfileDTO(
    Integer artisanId,
    String bio,
    String profilePicture,
    String location,
    String storeName,
    String storeBanner,
    String announcements,
    String businessHours,
    List<String> gallery,
    String stories,
    String specializations,
    String materialsUsed,
    String servicesOffered,
    Integer experienceYears,
    String shippingPolicies,
    String returnPolicy,
    String paymentOptions,
    String termsConditions,
    String privacyPolicy,
    String communicationPreferences,
    String preferredLanguage,
    String firstname,
    String lastname,
    String user_email,
    String bankAccountNo,
    String bankSortCode,
    String contactTelephone,
    String contactAddress
  ) {
    this.artisanId = artisanId;
    this.bio = bio;
    this.profilePicture = profilePicture;
    this.location = location;
    this.storeName = storeName;
    this.storeBanner = storeBanner;
    this.announcements = announcements;
    this.businessHours = businessHours;
    this.gallery = gallery;
    this.stories = stories;
    this.specializations = specializations;
    this.materialsUsed = materialsUsed;
    this.servicesOffered = servicesOffered;
    this.experienceYears = experienceYears;
    this.shippingPolicies = shippingPolicies;
    this.returnPolicy = returnPolicy;
    this.paymentOptions = paymentOptions;
    this.termsConditions = termsConditions;
    this.privacyPolicy = privacyPolicy;
    this.communicationPreferences = communicationPreferences;
    this.preferredLanguage = preferredLanguage;
    this.firstname = firstname;
    this.lastname = lastname;
    this.user_email = user_email;
    this.bankAccountNo = bankAccountNo;
    this.bankSortCode = bankSortCode;
    this.contactTelephone = contactTelephone;
    this.contactAddress = contactAddress;
  }

  public ArtisanProfileDTO(
    Integer artisanId,
    String bio,
    String profilePicture,
    String location,
    String storeName,
    String firstname,
    String lastname,
    String user_email,
    String bankAccountNo,
    String bankSortCode,
    String contactTelephone,
    String contactAddress
  ) {
    this.artisanId = artisanId;
    this.bio = bio;
    this.profilePicture = profilePicture;
    this.location = location;
    this.storeName = storeName;
    this.firstname = firstname;
    this.lastname = lastname;
    this.user_email = user_email;
    this.bankAccountNo = bankAccountNo;
    this.bankSortCode = bankSortCode;
    this.contactTelephone = contactTelephone;
    this.contactAddress = contactAddress;
  }

  public Integer getArtisanId() {
    return artisanId;
  }

  public void setArtisanId(Integer artisanId) {
    this.artisanId = artisanId;
  }

  public String getBio() {
    return bio;
  }

  public void setBio(String bio) {
    this.bio = bio;
  }

  public String getProfilePicture() {
    return profilePicture;
  }

  public void setProfilePicture(String profilePicture) {
    this.profilePicture = profilePicture;
  }

  public String getLocation() {
    return location;
  }

  public void setLocation(String location) {
    this.location = location;
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

  public String getStoreName() {
    return storeName;
  }

  public void setStoreName(String storeName) {
    this.storeName = storeName;
  }

  public String getStoreBanner() {
    return storeBanner;
  }

  public void setStoreBanner(String storeBanner) {
    this.storeBanner = storeBanner;
  }

  public String getAnnouncements() {
    return announcements;
  }

  public void setAnnouncements(String announcements) {
    this.announcements = announcements;
  }

  public String getBusinessHours() {
    return businessHours;
  }

  public void setBusinessHours(String businessHours) {
    this.businessHours = businessHours;
  }

  public List<String> getGallery() {
    return gallery;
  }

  public void setGallery(List<String> gallery) {
    this.gallery = gallery;
  }

  public String getStories() {
    return stories;
  }

  public void setStories(String stories) {
    this.stories = stories;
  }

  public String getSpecializations() {
    return specializations;
  }

  public void setSpecializations(String specializations) {
    this.specializations = specializations;
  }

  public String getMaterialsUsed() {
    return materialsUsed;
  }

  public void setMaterialsUsed(String materialsUsed) {
    this.materialsUsed = materialsUsed;
  }

  public String getServicesOffered() {
    return servicesOffered;
  }

  public void setServicesOffered(String servicesOffered) {
    this.servicesOffered = servicesOffered;
  }

  public Integer getExperienceYears() {
    return experienceYears;
  }

  public void setExperienceYears(Integer experienceYears) {
    this.experienceYears = experienceYears;
  }

  public String getShippingPolicies() {
    return shippingPolicies;
  }

  public void setShippingPolicies(String shippingPolicies) {
    this.shippingPolicies = shippingPolicies;
  }

  public String getReturnPolicy() {
    return returnPolicy;
  }

  public void setReturnPolicy(String returnPolicy) {
    this.returnPolicy = returnPolicy;
  }

  public String getPaymentOptions() {
    return paymentOptions;
  }

  public void setPaymentOptions(String paymentOptions) {
    this.paymentOptions = paymentOptions;
  }

  public String getTermsConditions() {
    return termsConditions;
  }

  public void setTermsConditions(String termsConditions) {
    this.termsConditions = termsConditions;
  }

  public String getPrivacyPolicy() {
    return privacyPolicy;
  }

  public void setPrivacyPolicy(String privacyPolicy) {
    this.privacyPolicy = privacyPolicy;
  }

  public String getCommunicationPreferences() {
    return communicationPreferences;
  }

  public void setCommunicationPreferences(String communicationPreferences) {
    this.communicationPreferences = communicationPreferences;
  }

  public String getPreferredLanguage() {
    return preferredLanguage;
  }

  public void setPreferredLanguage(String preferredLanguage) {
    this.preferredLanguage = preferredLanguage;
  }
}
