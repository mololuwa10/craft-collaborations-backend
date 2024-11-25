package com.example.dissertation_backend.solution.Stripe.Model;

// import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.util.List;

public class CheckoutPayment {

  // private String productName;

  @NotBlank(message = "Customer email is required")
  @Email(message = "Invalid email format")
  private String customerEmail;

  //  currency like usd, eur
  @NotBlank
  private String currency;

  @NotBlank
  private String successUrl;

  @NotBlank
  private String cancelUrl;

  @Positive
  private long amount;

  @Positive
  private long quantity;

  private List<CheckoutItem> items;

  // getters and setters
  // public String getProductName() {
  //   return productName;
  // }

  // public void setProductName(String productName) {
  //   this.productName = productName;
  // }

  public String getCurrency() {
    return currency;
  }

  public void setCurrency(String currency) {
    this.currency = currency;
  }

  public String getSuccessUrl() {
    return successUrl;
  }

  public void setSuccessUrl(String successUrl) {
    this.successUrl = successUrl;
  }

  public String getCancelUrl() {
    return cancelUrl;
  }

  public void setCancelUrl(String cancelUrl) {
    this.cancelUrl = cancelUrl;
  }

  public long getAmount() {
    return amount;
  }

  public void setAmount(long amount) {
    this.amount = amount;
  }

  public long getQuantity() {
    return quantity;
  }

  public void setQuantity(long quantity) {
    this.quantity = quantity;
  }

  public String getCustomerEmail() {
    return customerEmail;
  }

  public void setCustomerEmail(String customerEmail) {
    this.customerEmail = customerEmail;
  }

  public List<CheckoutItem> getItems() {
    return items;
  }

  public void setItems(List<CheckoutItem> items) {
    this.items = items;
  }
}
