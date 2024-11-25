package com.example.dissertation_backend.solution.ProductTest;

import com.example.dissertation_backend.solution.Products.Model.ProductAttributes;
import com.example.dissertation_backend.solution.Products.Model.Products;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ProductsTest {

  @Test
  void testCalculatePriceWithNoAttributes() {
    Products product = new Products();
    product.setProductPrice(100.00); // Set base price
    product.setAttributes(new HashSet<>()); // No attributes

    Assertions.assertEquals(100.00, product.calculatePriceWithCustomizations());
  }

  @Test
  void testCalculatePriceWithAttributes() {
    Products product = new Products();
    product.setProductPrice(100.00);

    Set<ProductAttributes> attributes = new HashSet<>();
    ProductAttributes attr1 = new ProductAttributes();
    attr1.setAffectsPricing(true);
    attr1.setProductAttributesValue("20");
    attributes.add(attr1);

    ProductAttributes attr2 = new ProductAttributes();
    attr2.setAffectsPricing(true);
    attr2.setProductAttributesValue("30");
    attributes.add(attr2);

    product.setAttributes(attributes);

    Assertions.assertEquals(150.00, product.calculatePriceWithCustomizations());
  }

  @Test
  void testCalculatePriceWithNonPricingAttributes() {
    Products product = new Products();
    product.setProductPrice(100.00);

    Set<ProductAttributes> attributes = new HashSet<>();
    ProductAttributes attr1 = new ProductAttributes();
    attr1.setAffectsPricing(false);
    attr1.setProductAttributesValue("20");
    attributes.add(attr1);

    product.setAttributes(attributes);

    Assertions.assertEquals(100.00, product.calculatePriceWithCustomizations());
  }

  @Test
  void testCalculatePriceWithMixedAttributes() {
    Products product = new Products();
    product.setProductPrice(100.00);

    Set<ProductAttributes> attributes = new HashSet<>();
    ProductAttributes attr1 = new ProductAttributes();
    attr1.setAffectsPricing(true);
    attr1.setProductAttributesValue("10.50");
    attributes.add(attr1);

    ProductAttributes attr2 = new ProductAttributes();
    attr2.setAffectsPricing(false);
    attr2.setProductAttributesValue("5.25");
    attributes.add(attr2);

    product.setAttributes(attributes);

    Assertions.assertEquals(110.50, product.calculatePriceWithCustomizations());
  }

  @Test
  void testCalculatePriceWithInvalidValues() {
    Products product = new Products();
    product.setProductPrice(100.00);

    Set<ProductAttributes> attributes = new HashSet<>();
    ProductAttributes attr = new ProductAttributes();
    attr.setAffectsPricing(true);
    attr.setProductAttributesValue("abc"); // Invalid numeric value
    attributes.add(attr);

    product.setAttributes(attributes);

    Assertions.assertEquals(100.00, product.calculatePriceWithCustomizations());
  }
}
