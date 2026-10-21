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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import com.food_waste_backend.Entity.Donor;
import com.food_waste_backend.Entity.FoodDonation;
import com.food_waste_backend.Entity.FoodRequest;
import com.food_waste_backend.Entity.Ngo;
import com.food_waste_backend.Entity.PickupRequest;
import com.food_waste_backend.Entity.User;
import com.food_waste_backend.Entity.Volunteer;
import com.food_waste_backend.dto.PickupResponse;
import com.food_waste_backend.enums.DonationStatus;
import com.food_waste_backend.enums.PickupStatus;
import com.food_waste_backend.enums.RequestStatus;
import com.food_waste_backend.enums.UserRole;
import com.food_waste_backend.repository.FoodDonationRepository;
import com.food_waste_backend.repository.FoodRequestRepository;
import com.food_waste_backend.repository.NotificationRepository;
import com.food_waste_backend.repository.PickupRequestRepository;
import com.food_waste_backend.repository.UserRepository;
import com.food_waste_backend.repository.VolunteerRepository;
import com.food_waste_backend.service.impl.PickupServiceImpl;

@ExtendWith(MockitoExtension.class)
class PickupServiceTest {

    @Mock
    private PickupRequestRepository pickupRequestRepository;

    @Mock
    private VolunteerRepository volunteerRepository;

    @Mock
    private FoodDonationRepository foodDonationRepository;

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private FoodRequestRepository foodRequestRepository;

    private PickupServiceImpl pickupService;

    private User volunteerUser;
    private Volunteer volunteer;
    private FoodDonation donation;
    private FoodRequest foodRequest;
    private PickupRequest pickup;

    @BeforeEach
    void setUp() {
        pickupService = new PickupServiceImpl(
                pickupRequestRepository,
                volunteerRepository,
                foodDonationRepository,
                notificationRepository,
                userRepository,
                foodRequestRepository
        );

        volunteerUser = new User();
        volunteerUser.setId(70L);
        volunteerUser.setEmail("volunteer@delivery.org");
        volunteerUser.setName("Alex Driver");
        volunteerUser.setRole(UserRole.VOLUNTEER);

        volunteer = new Volunteer();
        volunteer.setId(80L);
        volunteer.setUser(volunteerUser);
        volunteer.setVehicleType("Van");

        User donorUser = new User();
        donorUser.setId(10L);
        donorUser.setEmail("donor@example.com");

        Donor donor = new Donor();
        donor.setId(20L);
        donor.setUser(donorUser);

        donation = new FoodDonation();
        donation.setId(100L);
        donation.setTitle("Prepared Meals");
        donation.setDonor(donor);
        donation.setStatus(DonationStatus.ACCEPTED);
        donation.setPickupAddress("456 Chef Street");

        User ngoUser = new User();
        ngoUser.setId(50L);
        ngoUser.setEmail("ngo@community.org");

        Ngo ngo = new Ngo();
        ngo.setId(60L);
        ngo.setUser(ngoUser);
        ngo.setNgoName("Hope Shelter");

        foodRequest = new FoodRequest();
        foodRequest.setId(300L);
        foodRequest.setDonation(donation);
        foodRequest.setNgo(ngo);
        foodRequest.setStatus(RequestStatus.APPROVED);

        pickup = new PickupRequest();
        pickup.setId(500L);
        pickup.setDonation(donation);
        pickup.setFoodRequest(foodRequest);
        pickup.setVolunteer(volunteer);
        pickup.setStatus(PickupStatus.SCHEDULED);
        pickup.setScheduledTime(LocalDateTime.now().plusHours(1));
    }

    @Test
    @DisplayName("Volunteer successfully claims an unassigned pickup")
    void testClaimPickup_Success() {
        pickup.setVolunteer(null);

        when(pickupRequestRepository.findById(500L)).thenReturn(Optional.of(pickup));
        when(volunteerRepository.findByUserEmail("volunteer@delivery.org")).thenReturn(Optional.of(volunteer));
        when(pickupRequestRepository.save(any(PickupRequest.class))).thenReturn(pickup);

        PickupResponse response = pickupService.claimPickup(500L, "volunteer@delivery.org");

        assertNotNull(response);
        assertEquals(80L, pickup.getVolunteer().getId());
        verify(pickupRequestRepository).save(pickup);
    }

    @Test
    @DisplayName("Transition status to IN_PROGRESS")
    void testUpdatePickupStatus_InProgress() {
        when(pickupRequestRepository.findById(500L)).thenReturn(Optional.of(pickup));
        when(userRepository.findByEmail("volunteer@delivery.org")).thenReturn(Optional.of(volunteerUser));
        when(pickupRequestRepository.save(any(PickupRequest.class))).thenReturn(pickup);

        PickupResponse response = pickupService.updatePickupStatus(500L, "IN_PROGRESS", "volunteer@delivery.org", "En route to donor");

        assertNotNull(response);
        assertEquals(PickupStatus.IN_PROGRESS.name(), pickup.getStatus().name());
        assertEquals("En route to donor", pickup.getHandoverNotes());
        verify(pickupRequestRepository).save(pickup);
    }

    @Test
    @DisplayName("Transition status to COLLECTED updates collectedTime and marks donation as COLLECTED")
    void testUpdatePickupStatus_Collected_SetsTimestamp() {
        when(pickupRequestRepository.findById(500L)).thenReturn(Optional.of(pickup));
        when(userRepository.findByEmail("volunteer@delivery.org")).thenReturn(Optional.of(volunteerUser));
        when(pickupRequestRepository.save(any(PickupRequest.class))).thenReturn(pickup);

        PickupResponse response = pickupService.updatePickupStatus(500L, "COLLECTED", "volunteer@delivery.org", "Handover completed cleanly");

        assertNotNull(response);
        assertEquals(PickupStatus.COLLECTED.name(), pickup.getStatus().name());
        assertNotNull(pickup.getCollectedTime());
        assertEquals(DonationStatus.COLLECTED, donation.getStatus());
        verify(foodDonationRepository).save(donation);
        verify(pickupRequestRepository).save(pickup);
    }

    @Test
    @DisplayName("Transition status to COMPLETED marks foodRequest as COMPLETED and donation as COMPLETED")
    void testUpdatePickupStatus_Completed_MarksDelivered() {
        when(pickupRequestRepository.findById(500L)).thenReturn(Optional.of(pickup));
        when(userRepository.findByEmail("volunteer@delivery.org")).thenReturn(Optional.of(volunteerUser));
        when(pickupRequestRepository.save(any(PickupRequest.class))).thenReturn(pickup);

        PickupResponse response = pickupService.updatePickupStatus(500L, "COMPLETED", "volunteer@delivery.org", "Delivered safely to shelter manager");

        assertNotNull(response);
        assertEquals(PickupStatus.COMPLETED.name(), pickup.getStatus().name());
        assertNotNull(pickup.getCompletedTime());
        assertEquals(RequestStatus.COMPLETED, foodRequest.getStatus());
        assertEquals(DonationStatus.COMPLETED, donation.getStatus());
        verify(foodRequestRepository).save(foodRequest);
        verify(foodDonationRepository).save(donation);
        verify(pickupRequestRepository).save(pickup);
    }

    @Test
    @DisplayName("Unauthorized user cannot modify someone else's pickup")
    void testUpdatePickupStatus_UnauthorizedUser_ThrowsAccessDenied() {
        User otherVolunteerUser = new User();
        otherVolunteerUser.setId(99L);
        otherVolunteerUser.setEmail("other@volunteer.org");
        otherVolunteerUser.setRole(UserRole.VOLUNTEER);

        when(pickupRequestRepository.findById(500L)).thenReturn(Optional.of(pickup));
        when(userRepository.findByEmail("other@volunteer.org")).thenReturn(Optional.of(otherVolunteerUser));

        assertThrows(AccessDeniedException.class, () ->
                pickupService.updatePickupStatus(500L, "COMPLETED", "other@volunteer.org", null));
    }
}
