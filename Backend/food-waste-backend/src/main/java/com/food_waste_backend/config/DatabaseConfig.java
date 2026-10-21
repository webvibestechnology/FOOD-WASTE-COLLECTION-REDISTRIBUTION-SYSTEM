package com.food_waste_backend.config;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.food_waste_backend.Entity.Donor;
import com.food_waste_backend.Entity.FoodDonation;
import com.food_waste_backend.Entity.FoodRequest;
import com.food_waste_backend.Entity.Ngo;
import com.food_waste_backend.Entity.PickupRequest;
import com.food_waste_backend.Entity.User;
import com.food_waste_backend.Entity.Volunteer;
import com.food_waste_backend.enums.DonationStatus;
import com.food_waste_backend.enums.FoodCategory;
import com.food_waste_backend.enums.PickupStatus;
import com.food_waste_backend.enums.RequestStatus;
import com.food_waste_backend.enums.UserRole;
import com.food_waste_backend.repository.DonorRepository;
import com.food_waste_backend.repository.FoodDonationRepository;
import com.food_waste_backend.repository.FoodRequestRepository;
import com.food_waste_backend.repository.NgoRepository;
import com.food_waste_backend.repository.PickupRequestRepository;
import com.food_waste_backend.repository.UserRepository;
import com.food_waste_backend.repository.VolunteerRepository;

@Component
public class DatabaseConfig implements CommandLineRunner {

    private final UserRepository userRepository;
    private final DonorRepository donorRepository;
    private final FoodDonationRepository donationRepository;
    private final NgoRepository ngoRepository;
    private final FoodRequestRepository foodRequestRepository;
    private final VolunteerRepository volunteerRepository;
    private final PickupRequestRepository pickupRequestRepository;
    private final PasswordEncoder passwordEncoder;

    public DatabaseConfig(
            UserRepository userRepository,
            DonorRepository donorRepository,
            FoodDonationRepository donationRepository,
            NgoRepository ngoRepository,
            FoodRequestRepository foodRequestRepository,
            VolunteerRepository volunteerRepository,
            PickupRequestRepository pickupRequestRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.donorRepository = donorRepository;
        this.donationRepository = donationRepository;
        this.ngoRepository = ngoRepository;
        this.foodRequestRepository = foodRequestRepository;
        this.volunteerRepository = volunteerRepository;
        this.pickupRequestRepository = pickupRequestRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        System.out.println("Syncing and initializing database seed data idempotently...");

        String defaultEncodedPassword = passwordEncoder.encode("Password@123");

        // 1. Ensure Standard Admin User
        User adminUser = userRepository.findByEmail("admin@foodwaste.org").orElseGet(() -> {
            User user = new User();
            user.setName("Platform Admin");
            user.setEmail("admin@foodwaste.org");
            user.setPassword(defaultEncodedPassword);
            user.setPhone("1111111111");
            user.setRole(UserRole.ADMIN);
            user.setEnabled(true);
            user.setCreatedAt(LocalDateTime.now());
            user.setUpdatedAt(LocalDateTime.now());
            return userRepository.save(user);
        });
        adminUser.setPassword(defaultEncodedPassword);
        adminUser.setEnabled(true);
        userRepository.save(adminUser);

        // 2. Ensure Standard Donor User & Profile
        User donorUser = userRepository.findByEmail("john.doe@example.com").orElseGet(() -> {
            User user = new User();
            user.setName("John Doe");
            user.setEmail("john.doe@example.com");
            user.setPassword(defaultEncodedPassword);
            user.setPhone("1234567890");
            user.setRole(UserRole.DONOR);
            user.setEnabled(true);
            user.setCreatedAt(LocalDateTime.now());
            user.setUpdatedAt(LocalDateTime.now());
            return userRepository.save(user);
        });
        donorUser.setPassword(defaultEncodedPassword);
        donorUser.setEnabled(true);
        userRepository.save(donorUser);

        Donor donor = donorRepository.findByUserId(donorUser.getId()).orElseGet(() -> {
            Donor newDonor = new Donor();
            newDonor.setUser(donorUser);
            newDonor.setOrganizationName("Green Harvest Food Bank");
            newDonor.setDescription("Community food bank helping reduce surplus food waste");
            newDonor.setCreatedAt(LocalDateTime.now());
            return donorRepository.save(newDonor);
        });

        // 3. Ensure Standard NGO User & Profile
        User ngoUser = userRepository.findByEmail("jane.smith@ngo.org").orElseGet(() -> {
            User user = new User();
            user.setName("Jane Smith");
            user.setEmail("jane.smith@ngo.org");
            user.setPassword(defaultEncodedPassword);
            user.setPhone("9876543210");
            user.setRole(UserRole.NGO);
            user.setEnabled(true);
            user.setCreatedAt(LocalDateTime.now());
            user.setUpdatedAt(LocalDateTime.now());
            return userRepository.save(user);
        });
        ngoUser.setPassword(defaultEncodedPassword);
        ngoUser.setEnabled(true);
        userRepository.save(ngoUser);

        Ngo ngo = ngoRepository.findByUserId(ngoUser.getId()).orElseGet(() -> {
            Ngo newNgo = new Ngo();
            newNgo.setUser(ngoUser);
            newNgo.setNgoName("Help Hands Foundation");
            newNgo.setRegistrationNumber("NGO-2024-001");
            newNgo.setDescription("Non-profit organization distributing meals to shelters and elderly homes");
            newNgo.setContactPerson("Jane Smith");
            newNgo.setVerified(true);
            newNgo.setRegisteredAt(LocalDateTime.now());
            return ngoRepository.save(newNgo);
        });
        ngo.setVerified(true);
        ngoRepository.save(ngo);

        // 4. Ensure Standard Volunteer User & Profile
        User volunteerUser = userRepository.findByEmail("mike.johnson@volunteer.org").orElseGet(() -> {
            User user = new User();
            user.setName("Mike Johnson");
            user.setEmail("mike.johnson@volunteer.org");
            user.setPassword(defaultEncodedPassword);
            user.setPhone("5555555555");
            user.setRole(UserRole.VOLUNTEER);
            user.setEnabled(true);
            user.setCreatedAt(LocalDateTime.now());
            user.setUpdatedAt(LocalDateTime.now());
            return userRepository.save(user);
        });
        volunteerUser.setPassword(defaultEncodedPassword);
        volunteerUser.setEnabled(true);
        userRepository.save(volunteerUser);

        Volunteer volunteer = volunteerRepository.findByUserId(volunteerUser.getId()).orElseGet(() -> {
            Volunteer newVolunteer = new Volunteer();
            newVolunteer.setUser(volunteerUser);
            newVolunteer.setVehicleType("Van (Temperature-Controlled)");
            newVolunteer.setOperatingArea("Metro Central & North District");
            newVolunteer.setAvailable(true);
            return volunteerRepository.save(newVolunteer);
        });

        // 5. Heal any existing orphaned users in the database by creating corresponding role profile
        List<User> allUsers = userRepository.findAll();
        for (User u : allUsers) {
            if (u.getRole() == UserRole.DONOR && donorRepository.findByUserId(u.getId()).isEmpty()) {
                Donor d = new Donor();
                d.setUser(u);
                d.setOrganizationName(u.getName() + " Kitchen");
                d.setDescription("Food donor organization");
                d.setCreatedAt(LocalDateTime.now());
                donorRepository.save(d);
            } else if (u.getRole() == UserRole.NGO && ngoRepository.findByUserId(u.getId()).isEmpty()) {
                Ngo n = new Ngo();
                n.setUser(u);
                n.setNgoName(u.getName() + " Relief Org");
                n.setRegistrationNumber("NGO-" + u.getId() + "-REG");
                n.setDescription("Food distribution organization");
                n.setContactPerson(u.getName());
                n.setVerified(true);
                n.setRegisteredAt(LocalDateTime.now());
                ngoRepository.save(n);
            } else if (u.getRole() == UserRole.VOLUNTEER && volunteerRepository.findByUserId(u.getId()).isEmpty()) {
                Volunteer v = new Volunteer();
                v.setUser(u);
                v.setVehicleType("Standard Car");
                v.setOperatingArea("City Center");
                v.setAvailable(true);
                volunteerRepository.save(v);
            }
        }

        // 6. Seed sample food donations if fewer than 3 exist
        if (donationRepository.count() < 3) {
            createSampleDonation(donor, "Fresh Artisan Bread & Bagels", "BAKERY", BigDecimal.valueOf(45), "kg", LocalDateTime.now().plusDays(3), "Freshly baked artisan sourdough bread and multigrain bagels from downtown bakery.");
            createSampleDonation(donor, "Fresh Farm Vegetables", "RAW_VEGETABLES", BigDecimal.valueOf(60), "kg", LocalDateTime.now().plusDays(4), "Crisp organic carrots, cabbage, cucumbers, and potatoes.");
            createSampleDonation(donor, "Cooked Steamed Rice & Lentils", "COOKED_FOOD", BigDecimal.valueOf(30), "kg", LocalDateTime.now().plusHours(18), "Freshly prepared vegetarian steamed rice and dal from corporate catering hall.");
            createSampleDonation(donor, "Assorted Seasonal Fruit Baskets", "FRUITS", BigDecimal.valueOf(25), "kg", LocalDateTime.now().plusDays(5), "Ripe apples, oranges, and bananas suitable for distribution.");
            createSampleDonation(donor, "Packaged Organic Dairy Milk", "DAIRY", BigDecimal.valueOf(20), "liters", LocalDateTime.now().plusDays(2), "Pasteurized whole milk cartons stored under proper refrigeration.");
        }

        // 7. Seed sample requests if none exist
        if (foodRequestRepository.count() == 0) {
            List<FoodDonation> donations = donationRepository.findAll();
            if (!donations.isEmpty()) {
                FoodRequest request1 = new FoodRequest();
                request1.setNgo(ngo);
                request1.setDonation(donations.get(0));
                request1.setNotes("Allocated for community evening meal distribution program");
                request1.setStatus(RequestStatus.APPROVED);
                request1.setRequestedAt(LocalDateTime.now().minusDays(1));
                request1.setUpdatedAt(LocalDateTime.now().minusHours(12));
                foodRequestRepository.save(request1);
            }
        }

        // 8. Seed sample pickup requests if none exist
        if (pickupRequestRepository.count() == 0) {
            List<FoodDonation> donations = donationRepository.findAll();
            if (!donations.isEmpty()) {
                PickupRequest pickup1 = new PickupRequest();
                pickup1.setVolunteer(volunteer);
                pickup1.setDonation(donations.get(0));
                pickup1.setScheduledTime(LocalDateTime.now().plusHours(2));
                pickup1.setStatus(PickupStatus.SCHEDULED);
                pickup1.setCreatedAt(LocalDateTime.now());
                pickupRequestRepository.save(pickup1);
            }
        }

        System.out.println("Idempotent database seeding and profile sync completed successfully.");
    }

    private void createSampleDonation(Donor donor, String title, String category, 
            BigDecimal quantity, String unit, LocalDateTime expiry, String description) {
        try {
            FoodDonation donation = new FoodDonation();
            donation.setDonor(donor);
            donation.setTitle(title);
            donation.setDescription(description);
            donation.setQuantity(quantity);
            donation.setQuantityUnit(unit);
            donation.setCategory(FoodCategory.valueOf(category));
            donation.setExpiryTime(expiry);
            donation.setStatus(DonationStatus.AVAILABLE);
            donation.setCreatedAt(LocalDateTime.now());
            donation.setUpdatedAt(LocalDateTime.now());
            donationRepository.save(donation);
        } catch (Exception e) {
            System.err.println("Error creating sample donation: " + title + " - " + e.getMessage());
        }
    }
}
