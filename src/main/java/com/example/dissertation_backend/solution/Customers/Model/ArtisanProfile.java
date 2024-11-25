package com.example.dissertation_backend.solution.Customers.Model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import java.util.*;

@Entity
@Table(name = "artisan_profiles")
public class ArtisanProfile {

  @Id
  @Column(name = "artisan_id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer artisanId;

  @OneToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "user_id", referencedColumnName = "user_id")
  @JsonManagedReference
  private ApplicationUser artisan;

  @Column(name = "bio")
  private String bio;

  // Possibly other artisan-specific fields
  @Column(name = "profile_picture")
  private String profilePicture;

  @Column(name = "location")
  private String location;

  @Column(name = "store_name")
  private String storeName;

  @Column(name = "store_banner")
  private String storeBanner;

  @Column(name = "announcements", length = 2000)
  private String announcements;

  @Column(name = "business_hours")
  private String businessHours;

  @Column(name = "gallery")
  @ElementCollection
  private List<String> gallery;

  @Column(name = "stories", length = 2000)
  private String stories;

  @Column(name = "specializations")
  private String specializations;

  @Column(name = "materials_used")
  private String materialsUsed;

  @Column(name = "services_offered")
  private String servicesOffered;

  @Column(name = "experience_years")
  private Integer experienceYears;

  @Column(name = "shipping_policies", length = 2000)
  private String shippingPolicies;

  @Column(name = "return_policy", length = 2000)
  private String returnPolicy;

  @Column(name = "payment_options")
  private String paymentOptions;

  @Column(name = "terms_conditions", length = 2000)
  private String termsConditions;

  @Column(name = "privacy_policy", length = 2000)
  private String privacyPolicy;

  @Column(name = "communication_preferences")
  private String communicationPreferences;

  @Column(name = "preferred_language")
  private String preferredLanguage;

  @Column(name = "creation_date")
  @Temporal(TemporalType.TIMESTAMP)
  private Date creationDate;

  // Constructor
  public ArtisanProfile() {
    super();
  }

  public ArtisanProfile(
    ApplicationUser artisan,
    String bio,
    String profilePicture,
    String location,
    String storeName
  ) {
    this.artisan = artisan;
    this.bio = bio;
    this.profilePicture = profilePicture;
    this.location = location;
    this.storeName = storeName;
  }

  public ArtisanProfile(
    ApplicationUser artisan,
    String bio,
    String profilePicture,
    String location,
    String storeName,
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
    String storeBanner,
    Date creationDate
  ) {
    this.artisan = artisan;
    this.bio = bio;
    this.profilePicture = profilePicture;
    this.location = location;
    this.storeName = storeName;
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
    this.storeBanner = storeBanner;
    this.creationDate = creationDate;
  }

  // Getters and Setters
  public Integer getArtisanId() {
    return artisanId;
  }

  public void setArtisanId(Integer artisanId) {
    this.artisanId = artisanId;
  }

  public ApplicationUser getArtisan() {
    return artisan;
  }

  public void setArtisan(ApplicationUser artisan) {
    this.artisan = artisan;
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

  public String getStoreName() {
    return storeName;
  }

  public void setStoreName(String storeName) {
    this.storeName = storeName;
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

  public String getStoreBanner() {
    return storeBanner;
  }

  public void setStoreBanner(String storeBanner) {
    this.storeBanner = storeBanner;
  }

  public Date getCreationDate() {
    return creationDate;
  }

  public void setCreationDate(Date creationDate) {
    this.creationDate = creationDate;
  }
}
