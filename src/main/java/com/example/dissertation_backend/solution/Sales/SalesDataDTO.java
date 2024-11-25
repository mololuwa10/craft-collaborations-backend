package com.example.dissertation_backend.solution.Sales;

import java.time.LocalDateTime;

public class SalesDataDTO {

  private LocalDateTime date;
  private double totalSales;

  public SalesDataDTO() {
    super();
  }

  public SalesDataDTO(LocalDateTime date, double totalSales) {
    this.date = date;
    this.totalSales = totalSales;
  }

  public LocalDateTime getDate() {
    return date;
  }

  public void setDate(LocalDateTime date) {
    this.date = date;
  }

  public double getTotalSales() {
    return totalSales;
  }

  public void setTotalSales(double totalSales) {
    this.totalSales = totalSales;
  }
}
