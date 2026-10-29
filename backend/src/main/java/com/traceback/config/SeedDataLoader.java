package com.traceback.config;

import com.traceback.categories.Category;
import com.traceback.categories.CategoryRepository;
import com.traceback.claims.Claim;
import com.traceback.claims.ClaimRepository;
import com.traceback.claims.ClaimStatus;
import com.traceback.claims.PrivateVerificationEvidence;
import com.traceback.matching.MatchingEngineService;
import com.traceback.notifications.NotificationService;
import com.traceback.notifications.NotificationType;
import com.traceback.reports.ItemReport;
import com.traceback.reports.ItemReportRepository;
import com.traceback.reports.ReportStatus;
import com.traceback.reports.ReportType;
import com.traceback.reports.Visibility;
import com.traceback.users.AccountStatus;
import com.traceback.users.Role;
import com.traceback.users.User;
import com.traceback.users.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Component
public class SeedDataLoader implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(SeedDataLoader.class);

    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final ItemReportRepository reportRepository;
    private final ClaimRepository claimRepository;
    private final MatchingEngineService matchingEngineService;
    private final NotificationService notificationService;
    private final PasswordEncoder passwordEncoder;

    @Value("${traceback.seed.enabled:true}")
    private boolean seedEnabled;

    public SeedDataLoader(CategoryRepository categoryRepository,
                          UserRepository userRepository,
                          ItemReportRepository reportRepository,
                          ClaimRepository claimRepository,
                          MatchingEngineService matchingEngineService,
                          NotificationService notificationService,
                          PasswordEncoder passwordEncoder) {
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
        this.reportRepository = reportRepository;
        this.claimRepository = claimRepository;
        this.matchingEngineService = matchingEngineService;
        this.notificationService = notificationService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (!seedEnabled) {
            log.info("Database seeding is disabled by configuration");
            return;
        }

        seedCategories();
        seedUsersAndDemoReports();
    }

    private void seedCategories() {
        if (categoryRepository.count() > 0) return;

        log.info("Seeding initial item categories...");
        List<Category> categories = List.of(
                new Category("Electronics", "electronics", "Laptops, phones, tablets, earphones, chargers", "Laptop"),
                new Category("Bags & Backpacks", "bags-backpacks", "Backpacks, handbags, duffel bags, totes", "Briefcase"),
                new Category("Wallets & Purses", "wallets-purses", "Wallets, coin purses, cardholders", "CreditCard"),
                new Category("Keys", "keys", "House keys, car keys, bike keys, key fobs", "Key"),
                new Category("Official IDs & Documents", "ids-documents", "Student cards, driver licenses, passports, certificates", "FileText"),
                new Category("Clothing & Accessories", "clothing-accessories", "Jackets, sweaters, hats, scarves, umbrellas", "Shirt"),
                new Category("Jewelry & Watches", "jewelry-watches", "Rings, bracelets, necklaces, watches", "Watch"),
                new Category("Books & Stationery", "books-stationery", "Textbooks, notebooks, pencil cases, calculators", "BookOpen"),
                new Category("Other", "other", "Miscellaneous items and personal belongings", "HelpCircle")
        );

        categoryRepository.saveAll(categories);
        log.info("Seeded {} categories successfully", categories.size());
    }

    private void seedUsersAndDemoReports() {
        if (userRepository.count() > 0) return;

        log.info("Seeding demo accounts and initial reports...");

        // 1. Admin User
        User admin = new User("System Administrator", "admin@traceback.demo", "admin@traceback.demo",
                passwordEncoder.encode("AdminSecret123!"), Set.of(Role.ROLE_ADMIN, Role.ROLE_MODERATOR, Role.ROLE_USER));
        admin.setCreatedAt(Instant.now());
        admin.setUpdatedAt(Instant.now());
        User savedAdmin = userRepository.save(admin);

        // 2. Moderator User
        User mod = new User("Sam Moderator", "sam@traceback.demo", "sam@traceback.demo",
                passwordEncoder.encode("Password123!"), Set.of(Role.ROLE_MODERATOR, Role.ROLE_USER));
        mod.setCreatedAt(Instant.now());
        mod.setUpdatedAt(Instant.now());
        User savedMod = userRepository.save(mod);

        // 3. Regular Demo Users
        User alex = new User("Alex Rivera", "alex@traceback.demo", "alex@traceback.demo",
                passwordEncoder.encode("Password123!"), Set.of(Role.ROLE_USER));
        alex.setPhone("+1-555-0199");
        alex.setCreatedAt(Instant.now());
        alex.setUpdatedAt(Instant.now());
        User savedAlex = userRepository.save(alex);

        User priya = new User("Priya Sharma", "priya@traceback.demo", "priya@traceback.demo",
                passwordEncoder.encode("Password123!"), Set.of(Role.ROLE_USER));
        priya.setPhone("+1-555-0244");
        priya.setCreatedAt(Instant.now());
        priya.setUpdatedAt(Instant.now());
        User savedPriya = userRepository.save(priya);

        // Fetch categories
        Category electronics = categoryRepository.findBySlug("electronics").orElse(null);
        Category bags = categoryRepository.findBySlug("bags-backpacks").orElse(null);
        Category keys = categoryRepository.findBySlug("keys").orElse(null);
        Category ids = categoryRepository.findBySlug("ids-documents").orElse(null);

        // Sample Report 1: Lost MacBook by Alex
        ItemReport lostMacBook = new ItemReport();
        lostMacBook.setOwnerId(savedAlex.getId());
        lostMacBook.setOwnerName(savedAlex.getName());
        lostMacBook.setReportType(ReportType.LOST);
        lostMacBook.setTitle("Apple MacBook Pro 14-inch Space Gray");
        lostMacBook.setCategoryId(electronics != null ? electronics.getId() : "1");
        lostMacBook.setCategoryName("Electronics");
        lostMacBook.setDescription("Left on the 2nd floor silent study area in Central Library. Has an astronaut sticker on the top cover.");
        lostMacBook.setColor("Space Gray");
        lostMacBook.setBrand("Apple");
        lostMacBook.setDistinguishingFeatures("Astronaut sticker on lid, minor scratch near USB-C port");
        lostMacBook.setDateLostOrFound(LocalDate.now().minusDays(2));
        lostMacBook.setLocation(new GeoJsonPoint(77.5946, 12.9716)); // Bangalore coordinates as standard example
        lostMacBook.setPublicLocationLabel("Central Library, 2nd Floor Study Wing");
        lostMacBook.setImages(List.of("https://images.unsplash.com/photo-1517336714731-489689fd1ca8?auto=format&fit=crop&w=600&q=80"));
        lostMacBook.setStatus(ReportStatus.ACTIVE);
        lostMacBook.setVisibility(Visibility.PUBLIC);
        lostMacBook.setCreatedAt(Instant.now().minusSeconds(86400 * 2));
        lostMacBook.setUpdatedAt(Instant.now());
        ItemReport savedLostMac = reportRepository.save(lostMacBook);

        // Sample Report 2: Found MacBook by Priya (Matches Report 1!)
        ItemReport foundMacBook = new ItemReport();
        foundMacBook.setOwnerId(savedPriya.getId());
        foundMacBook.setOwnerName(savedPriya.getName());
        foundMacBook.setReportType(ReportType.FOUND);
        foundMacBook.setTitle("Found Apple Laptop Space Grey in Library");
        foundMacBook.setCategoryId(electronics != null ? electronics.getId() : "1");
        foundMacBook.setCategoryName("Electronics");
        foundMacBook.setDescription("Discovered on study table 24 in Central Library around 7 PM. Handed to reception desk.");
        foundMacBook.setColor("Space Gray");
        foundMacBook.setBrand("Apple");
        foundMacBook.setDistinguishingFeatures("Has decorative space stickers on the back");
        foundMacBook.setDateLostOrFound(LocalDate.now().minusDays(1));
        foundMacBook.setLocation(new GeoJsonPoint(77.5948, 12.9719)); // ~40 meters away
        foundMacBook.setPublicLocationLabel("Central Library Reception Desk");
        foundMacBook.setImages(List.of("https://images.unsplash.com/photo-1611186871348-b1ce696e52c9?auto=format&fit=crop&w=600&q=80"));
        foundMacBook.setStatus(ReportStatus.ACTIVE);
        foundMacBook.setVisibility(Visibility.PUBLIC);
        foundMacBook.setCreatedAt(Instant.now().minusSeconds(86400));
        foundMacBook.setUpdatedAt(Instant.now());
        ItemReport savedFoundMac = reportRepository.save(foundMacBook);

        // Sample Report 3: Lost Black Backpack
        ItemReport lostBackpack = new ItemReport();
        lostBackpack.setOwnerId(savedAlex.getId());
        lostBackpack.setOwnerName(savedAlex.getName());
        lostBackpack.setReportType(ReportType.LOST);
        lostBackpack.setTitle("North Face Borealis Backpack Black");
        lostBackpack.setCategoryId(bags != null ? bags.getId() : "2");
        lostBackpack.setCategoryName("Bags & Backpacks");
        lostBackpack.setDescription("Black North Face backpack containing notebooks and a blue water bottle. Left near Cafeteria.");
        lostBackpack.setColor("Black");
        lostBackpack.setBrand("The North Face");
        lostBackpack.setDistinguishingFeatures("Reflective yellow clip on front strap");
        lostBackpack.setDateLostOrFound(LocalDate.now().minusDays(4));
        lostBackpack.setLocation(new GeoJsonPoint(77.5950, 12.9725));
        lostBackpack.setPublicLocationLabel("Campus Cafeteria Outdoor Patio");
        lostBackpack.setImages(List.of("https://images.unsplash.com/photo-1553062407-98eeb64c6a62?auto=format&fit=crop&w=600&q=80"));
        lostBackpack.setStatus(ReportStatus.ACTIVE);
        lostBackpack.setVisibility(Visibility.PUBLIC);
        lostBackpack.setCreatedAt(Instant.now().minusSeconds(86400 * 4));
        lostBackpack.setUpdatedAt(Instant.now());
        reportRepository.save(lostBackpack);

        // Sample Report 4: Found Student ID Card
        ItemReport foundId = new ItemReport();
        foundId.setOwnerId(savedMod.getId());
        foundId.setOwnerName(savedMod.getName());
        foundId.setReportType(ReportType.FOUND);
        foundId.setTitle("Found University Student ID Card");
        foundId.setCategoryId(ids != null ? ids.getId() : "5");
        foundId.setCategoryName("Official IDs & Documents");
        foundId.setDescription("Student smart card found near Computer Science block entrance. Kept safely at departmental office.");
        foundId.setColor("White");
        foundId.setBrand("University");
        foundId.setDateLostOrFound(LocalDate.now().minusDays(1));
        foundId.setLocation(new GeoJsonPoint(77.5935, 12.9705));
        foundId.setPublicLocationLabel("CS Engineering Block Reception");
        foundId.setStatus(ReportStatus.ACTIVE);
        foundId.setVisibility(Visibility.PUBLIC);
        foundId.setCreatedAt(Instant.now().minusSeconds(43200));
        foundId.setUpdatedAt(Instant.now());
        reportRepository.save(foundId);

        // Run matching engine for MacBook reports
        matchingEngineService.calculateMatchesForReport(savedLostMac);

        // Create sample claim for Alex on found laptop
        Claim demoClaim = new Claim(
                savedFoundMac.getId(),
                savedFoundMac.getTitle(),
                savedAlex.getId(),
                savedAlex.getName(),
                "This is my MacBook Pro that I forgot at study table 24. I have provided the serial number and sticker photo in the private verification.",
                new PrivateVerificationEvidence(
                        "Astronaut sticker with NASA logo, minor scratch on left port",
                        "C02G1234MD6R",
                        null,
                        "Password lock screen has wallpaper of James Webb deep field telescope"
                )
        );
        demoClaim.setStatus(ClaimStatus.PENDING);
        demoClaim.setCreatedAt(Instant.now().minusSeconds(12000));
        claimRepository.save(demoClaim);

        // Create sample notifications
        notificationService.createNotification(
                savedAlex.getId(),
                NotificationType.MATCH_FOUND,
                "Strong Potential Match Discovered!",
                "A found report ('Found Apple Laptop Space Grey in Library') matches your lost MacBook report with an 85% similarity score.",
                savedFoundMac.getId(),
                "MATCH"
        );

        notificationService.createNotification(
                savedPriya.getId(),
                NotificationType.CLAIM_SUBMITTED,
                "Ownership Claim Submitted",
                "Alex Rivera has submitted an ownership claim with serial number evidence for 'Found Apple Laptop Space Grey in Library'.",
                demoClaim.getId(),
                "CLAIM"
        );

        log.info("Demo database seeded successfully with accounts, reports, matches, and claims.");
    }
}
