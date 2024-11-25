package com.example.dissertation_backend.solution.AddressTest;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.example.dissertation_backend.solution.Addresses.Controller.AddressController;
import com.example.dissertation_backend.solution.Addresses.Model.Address;
import com.example.dissertation_backend.solution.Addresses.Service.AddressService;
import com.example.dissertation_backend.solution.Customers.Model.ApplicationUser;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

public class AddressControllerTests {

  @Mock
  private AddressService addressService;

  @InjectMocks
  private AddressController addressController;

  private MockMvc mockMvc;

  @BeforeEach
  public void setup() {
    MockitoAnnotations.openMocks(this);
    mockMvc = MockMvcBuilders.standaloneSetup(addressController).build();
  }

  @Test
  @WithMockUser(username = "user1", roles = { "USER" })
  public void getCurrentUserAddresses_Success() throws Exception {
    when(addressService.findAddressesForCurrentUser())
      .thenReturn(List.of(new Address()));

    mockMvc
      .perform(get("/api/addresses/my-addresses"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$").isArray());
  }

  @Test
  @WithMockUser(username = "user1", roles = { "USER" })
  public void getCurrentUserAddresses_NotFound() throws Exception {
    when(addressService.findAddressesForCurrentUser()).thenReturn(List.of());

    mockMvc
      .perform(get("/api/addresses/my-addresses"))
      .andExpect(status().isNotFound());
  }

  @Test
  @WithMockUser(username = "user1", roles = { "USER" })
  public void createAddress_Success() throws Exception {
    Address address = new Address();
    address.setCountry("USA");
    address.setCity("City");
    ApplicationUser user = new ApplicationUser();
    user.setUsername("user1");

    when(addressService.getCurrentUser()).thenReturn(Optional.of(user));
    when(addressService.saveAddress(any(Address.class))).thenReturn(address);

    mockMvc
      .perform(
        post("/api/addresses")
          .contentType(MediaType.APPLICATION_JSON)
          .content("{\"country\":\"USA\",\"city\":\"City\"}")
      )
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.country").value("USA"));
  }

  @Test
  @WithMockUser(username = "user1", roles = { "USER" })
  public void updateAddress_Unauthorized() throws Exception {
    when(addressService.getCurrentUser()).thenReturn(Optional.empty());

    mockMvc
      .perform(
        put("/api/addresses/1")
          .contentType(MediaType.APPLICATION_JSON)
          .content("{\"country\":\"Changed\"}")
      )
      .andExpect(status().isForbidden());
  }
}
