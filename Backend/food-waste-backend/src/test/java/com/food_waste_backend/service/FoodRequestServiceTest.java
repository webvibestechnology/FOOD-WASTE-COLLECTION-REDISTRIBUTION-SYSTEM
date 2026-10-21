package com.food_waste_backend.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
import com.food_waste_backend.Entity.FoodRequest;
import com.food_waste_backend.Entity.Ngo;
import com.food_waste_backend.Entity.User;
import com.food_waste_backend.dto.FoodRequestDto;
import com.food_waste_backend.dto.FoodRequestResponse;
import com.food_waste_backend.enums.DonationStatus;
import com.food_waste_backend.enums.FoodCategory;
import com.food_waste_backend.enums.RequestStatus;
import com.food_waste_backend.enums.UserRole;
import com.food_waste_backend.repository.DonorRepository;
import com.food_waste_backend.repository.FoodDonationRepository;
import com.food_waste_backend.repository.FoodRequestRepository;
import com.food_waste_backend.repository.NgoRepository;
import com.food_waste_backend.repository.NotificationRepository;
import com.food_waste_backend.repository.PickupRequestRepository;
import com.food_waste_backend.repository.UserRepository;
import com.food_waste_backend.repository.VolunteerRepository;
import com.food_waste_backend.service.impl.FoodRequestServiceImpl;

@ExtendWith(MockitoExtension.class)
class FoodRequestServiceTest {

    @Mock
    private FoodRequestRepository foodRequestRepository;

    @Mock
    private NgoRepository ngoRepository;

    @Mock
    private FoodDonationRepository foodDonationRepository;

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private DonorRepository donorRepository;

    @Mock
    private PickupRequestRepository pickupRequestRepository;

    @Mock
    private VolunteerRepository volunteerRepository;

    private FoodRequestServiceImpl foodRequestService;

    private User ngoUser;
    private Ngo verifiedNgo;
    private Ngo unverifiedNgo;
    private FoodDonation availableDonation;

    @BeforeEach
    void setUp() {
        foodRequestService = new FoodRequestServiceImpl(
                foodRequestRepository,
                ngoRepository,
                foodDonationRepository,
                notificationRepository,
                userRepository,
                donorRepository,
                pickupRequestRepository,
                volunteerRepository
        );

        ngoUser = new User();
        ngoUser.setId(50L);
        ngoUser.setEmail("ngo@community.org");
        ngoUser.setName("Community Food Bank");
        ngoUser.setRole(UserRole.NGO);

        verifiedNgo = new Ngo();
        verifiedNgo.setId(60L);
        verifiedNgo.setUser(ngoUser);
        verifiedNgo.setNgoName("Community Food Bank");
        verifiedNgo.setVerified(true);

        unverifiedNgo = new Ngo();
        unverifiedNgo.setId(61L);
        unverifiedNgo.setUser(ngoUser);
        unverifiedNgo.setNgoName("Unverified Charity");
        unverifiedNgo.setVerified(false);

        User donorUser = new User();
        donorUser.setId(10L);
        donorUser.setEmail("donor@example.com");

        Donor donor = new Donor();
        donor.setId(20L);
        donor.setUser(donorUser);

        availableDonation = new FoodDonation();
        availableDonation.setId(100L);
        availableDonation.setTitle("Fresh Vegetables");
        availableDonation.setQuantity(BigDecimal.valueOf(50));
        availableDonation.setAllocatedQuantity(BigDecimal.ZERO);
        availableDonation.setQuantityUnit("kg");
        availableDonation.setCategory(FoodCategory.RAW_VEGETABLES);
        availableDonation.setStatus(DonationStatus.AVAILABLE);
        availableDonation.setExpiryTime(LocalDateTime.now().plusDays(2));
        availableDonation.setSafeUseDeadline(LocalDateTime.now().plusDays(2));
        availableDonation.setDonor(donor);
    }

    @Test
    @DisplayName("Prevent request on expired food donation")
    void testCreateFoodRequest_ExpiredDonation_ThrowsException() {
        availableDonation.setExpiryTime(LocalDateTime.now().minusHours(1));
        when(foodDonationRepository.findById(100L)).thenReturn(Optional.of(availableDonation));

        FoodRequestDto dto = new FoodRequestDto();
        dto.setDonationId(100L);
        dto.setRequestedQuantity(BigDecimal.valueOf(10));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                foodRequestService.createFoodRequest("ngo@community.org", dto));
        assertTrue(ex.getMessage().contains("Cannot request expired"));
        verify(foodRequestRepository, never()).save(any());
    }

    @Test
    @DisplayName("Prevent over-allocation: reject request exceeding remaining quantity")
    void testCreateFoodRequest_OverAllocation_ThrowsException() {
        when(foodDonationRepository.findById(100L)).thenReturn(Optional.of(availableDonation));

        FoodRequestDto dto = new FoodRequestDto();
        dto.setDonationId(100L);
        dto.setRequestedQuantity(BigDecimal.valueOf(60)); // Available is 50

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                foodRequestService.createFoodRequest("ngo@community.org", dto));
        assertTrue(ex.getMessage().contains("exceeds available remaining quantity"));
        verify(foodRequestRepository, never()).save(any());
    }

    @Test
    @DisplayName("Enforce NGO verification: unverified NGO is denied request creation")
    void testCreateFoodRequest_UnverifiedNgo_ThrowsAccessDenied() {
        when(foodDonationRepository.findById(100L)).thenReturn(Optional.of(availableDonation));
        when(ngoRepository.findByUserEmail("ngo@community.org")).thenReturn(Optional.of(unverifiedNgo));

        FoodRequestDto dto = new FoodRequestDto();
        dto.setDonationId(100L);
        dto.setRequestedQuantity(BigDecimal.valueOf(20));

        AccessDeniedException ex = assertThrows(AccessDeniedException.class, () ->
                foodRequestService.createFoodRequest("ngo@community.org", dto));
        assertTrue(ex.getMessage().contains("Only verified NGOs can request food donations"));
        verify(foodRequestRepository, never()).save(any());
    }

    @Test
    @DisplayName("Successfully create food request and update donation to REQUESTED")
    void testCreateFoodRequest_Success() {
        when(foodDonationRepository.findById(100L)).thenReturn(Optional.of(availableDonation));
        when(ngoRepository.findByUserEmail("ngo@community.org")).thenReturn(Optional.of(verifiedNgo));
        when(foodRequestRepository.findByDonationId(100L)).thenReturn(Collections.emptyList());
        when(foodRequestRepository.save(any(FoodRequest.class))).thenAnswer(invocation -> {
            FoodRequest req = invocation.getArgument(0);
            req.setId(300L);
            return req;
        });

        FoodRequestDto dto = new FoodRequestDto();
        dto.setDonationId(100L);
        dto.setRequestedQuantity(BigDecimal.valueOf(25));
        dto.setBeneficiaryCount(50);
        dto.setDeliveryNotes("Deliver to main shelter gate");

        FoodRequestResponse response = foodRequestService.createFoodRequest("ngo@community.org", dto);

        assertNotNull(response);
        assertEquals(300L, response.getId());
        assertEquals(BigDecimal.valueOf(25), response.getRequestedQuantity());
        assertEquals(RequestStatus.PENDING.name(), response.getStatus());
        assertEquals(DonationStatus.REQUESTED, availableDonation.getStatus());
        verify(foodRequestRepository).save(any(FoodRequest.class));
        verify(foodDonationRepository).save(availableDonation);
    }

    @Test
    @DisplayName("Approving request increases allocatedQuantity, creates pickup, marks donation ACCEPTED if full")
    void testApproveFoodRequest_FullyAllocated_CreatesPickup() {
        FoodRequest request = new FoodRequest();
        request.setId(300L);
        request.setDonation(availableDonation);
        request.setNgo(verifiedNgo);
        request.setRequestedQuantity(BigDecimal.valueOf(50)); // Request entire 50 kg
        request.setStatus(RequestStatus.PENDING);

        when(foodRequestRepository.findById(300L)).thenReturn(Optional.of(request));
        when(foodRequestRepository.save(any(FoodRequest.class))).thenReturn(request);

        FoodRequestResponse response = foodRequestService.approveFoodRequest(300L, "donor@example.com");

        assertNotNull(response);
        assertEquals(RequestStatus.APPROVED.name(), request.getStatus().name());
        assertEquals(BigDecimal.valueOf(50), availableDonation.getAllocatedQuantity());
        assertEquals(DonationStatus.ACCEPTED, availableDonation.getStatus());
        verify(foodDonationRepository).save(availableDonation);
        verify(pickupRequestRepository).save(any());
    }

    @Test
    @DisplayName("Cancelling approved request restores allocated quantity back to donation")
    void testCancelFoodRequest_ApprovedRequest_RestoresQuantity() {
        availableDonation.setAllocatedQuantity(BigDecimal.valueOf(30));
        availableDonation.setStatus(DonationStatus.REQUESTED);

        FoodRequest request = new FoodRequest();
        request.setId(300L);
        request.setDonation(availableDonation);
        request.setNgo(verifiedNgo);
        request.setRequestedQuantity(BigDecimal.valueOf(20));
        request.setStatus(RequestStatus.APPROVED);

        when(foodRequestRepository.findById(300L)).thenReturn(Optional.of(request));
        when(foodRequestRepository.save(any(FoodRequest.class))).thenReturn(request);

        foodRequestService.cancelFoodRequest(300L, "ngo@community.org");

        assertEquals(RequestStatus.CANCELLED, request.getStatus());
        assertEquals(BigDecimal.valueOf(10), availableDonation.getAllocatedQuantity()); // 30 - 20 = 10
        assertEquals(DonationStatus.AVAILABLE, availableDonation.getStatus());
        verify(foodDonationRepository).save(availableDonation);
    }
}
