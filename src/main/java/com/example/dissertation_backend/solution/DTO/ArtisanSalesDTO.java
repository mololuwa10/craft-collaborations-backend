package com.example.dissertation_backend.solution.DTO;

import com.example.dissertation_backend.solution.Customers.Model.ArtisanProfile;

public class ArtisanSalesDTO {

  private ArtisanProfile artisan;
  private double totalSales;
  private int totalOrders;

  public ArtisanSalesDTO(
    ArtisanProfile artisan,
    double totalSales,
    int totalOrders
  ) {
    this.artisan = artisan;
    this.totalSales = totalSales;
    this.totalOrders = totalOrders;
  }

  // Getters and Setters
  public ArtisanProfile getArtisan() {
    return artisan;
  }

  public void setArtisan(ArtisanProfile artisan) {
    this.artisan = artisan;
  }

  public double getTotalSales() {
    return totalSales;
  }

  public void setTotalSales(double totalSales) {
    this.totalSales = totalSales;
  }

  public int getTotalOrders() {
    return totalOrders;
  }

  public void setTotalOrders(int totalOrders) {
    this.totalOrders = totalOrders;
  }
}
