package com.food_waste_backend.service;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import com.food_waste_backend.Entity.Donor;
import com.food_waste_backend.Entity.FoodDonation;
import com.food_waste_backend.Entity.User;
import com.food_waste_backend.dto.DonationRequest;
import com.food_waste_backend.dto.DonationResponse;
import com.food_waste_backend.enums.DonationStatus;
import com.food_waste_backend.enums.FoodCategory;
import com.food_waste_backend.enums.UserRole;
import com.food_waste_backend.repository.DonorRepository;
import com.food_waste_backend.repository.FoodDonationRepository;
import com.food_waste_backend.repository.UserRepository;
import com.food_waste_backend.service.impl.DonationServiceImpl;

@ExtendWith(MockitoExtension.class)
class DonationServiceTest {

    @Mock
    private FoodDonationRepository donationRepository;

    @Mock
    private DonorRepository donorRepository;

    @Mock
    private UserRepository userRepository;

    private DonationServiceImpl donationService;

    private User donorUser;
    private Donor donor;

    @BeforeEach
    void setUp() {
        donationService = new DonationServiceImpl(donationRepository, donorRepository, userRepository);

        donorUser = new User();
        donorUser.setId(10L);
        donorUser.setEmail("donor@example.com");
        donorUser.setName("Fresh Bakery");
        donorUser.setPhone("+1234567890");
        donorUser.setRole(UserRole.DONOR);

        donor = new Donor();
        donor.setId(20L);
        donor.setUser(donorUser);
        donor.setOrganizationName("Fresh Bakery");
    }

    @Test
    @DisplayName("Reject donation creation when expiryTime is in the past")
    void testCreateDonation_PastExpiry_ThrowsException() {
        DonationRequest request = new DonationRequest();
        request.setTitle("Past Bread");
        request.setQuantity(10);
        request.setQuantityUnit("kg");
        request.setCategory("BAKERY");
        request.setExpiryTime(LocalDateTime.now().minusHours(2));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                donationService.createDonation(request, 10L));
        assertEquals("Expiry time must be in the future", ex.getMessage());
        verify(donationRepository, never()).save(any());
    }

    @Test
    @DisplayName("Reject donation creation when safeUseDeadline is in the past")
    void testCreateDonation_PastSafeUseDeadline_ThrowsException() {
        DonationRequest request = new DonationRequest();
        request.setTitle("Unsafe Dairy");
        request.setQuantity(5);
        request.setQuantityUnit("liters");
        request.setCategory("DAIRY");
        request.setExpiryTime(LocalDateTime.now().plusDays(2));
        request.setSafeUseDeadline(LocalDateTime.now().minusMinutes(30));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                donationService.createDonation(request, 10L));
        assertEquals("Safe-use deadline must be in the future", ex.getMessage());
        verify(donationRepository, never()).save(any());
    }

    @Test
    @DisplayName("Successfully create donation with food safety attributes")
    void testCreateDonation_Success() {
        DonationRequest request = new DonationRequest();
        request.setTitle("Artisan Sourdough Loaves");
        request.setDescription("Freshly baked sourdough from morning batch");
        request.setQuantity(15);
        request.setQuantityUnit("kg");
        request.setCategory("BAKERY");
        request.setExpiryTime(LocalDateTime.now().plusDays(1));
        request.setSafeUseDeadline(LocalDateTime.now().plusDays(1));
        request.setAllergens("Gluten, Wheat");
        request.setStorageRequirements("Cool dry place");
        request.setDietaryInfo("Vegetarian, Vegan");
        request.setPickupAddress("123 Bakery Lane");

        when(userRepository.findById(10L)).thenReturn(Optional.of(donorUser));
        when(donorRepository.findByUserId(10L)).thenReturn(Optional.of(donor));
        when(donationRepository.save(any(FoodDonation.class))).thenAnswer(invocation -> {
            FoodDonation saved = invocation.getArgument(0);
            saved.setId(100L);
            return saved;
        });

        DonationResponse response = donationService.createDonation(request, 10L);

        assertNotNull(response);
        assertEquals(100L, response.getId());
        assertEquals("Artisan Sourdough Loaves", response.getTitle());
        assertEquals(Integer.valueOf(15), response.getQuantity());
        assertEquals(DonationStatus.AVAILABLE.name(), response.getStatus());
        assertEquals("Gluten, Wheat", response.getAllergens());
        assertEquals("Cool dry place", response.getStorageRequirements());
        verify(donationRepository).save(any(FoodDonation.class));
    }

    @Test
    @DisplayName("Delete donation verifies ownership and denies unauthorized user")
    void testDeleteDonation_UnauthorizedUser_ThrowsAccessDenied() {
        FoodDonation donation = new FoodDonation();
        donation.setId(100L);
        donation.setDonor(donor);
        donation.setStatus(DonationStatus.AVAILABLE);

        User stranger = new User();
        stranger.setId(99L);
        stranger.setEmail("stranger@example.com");
        stranger.setRole(UserRole.DONOR);

        when(donationRepository.findById(100L)).thenReturn(Optional.of(donation));
        when(userRepository.findByEmail("stranger@example.com")).thenReturn(Optional.of(stranger));

        assertThrows(AccessDeniedException.class, () ->
                donationService.deleteDonation(100L, "stranger@example.com"));
        verify(donationRepository, never()).deleteById(100L);
    }
}
