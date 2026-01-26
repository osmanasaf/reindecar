package com.reindecar.common.config;

import com.reindecar.common.valueobject.Money;
import com.reindecar.entity.branch.Branch;
import com.reindecar.entity.customer.Customer;
import com.reindecar.entity.customer.CustomerCompany;
import com.reindecar.entity.customer.CustomerPerson;
import com.reindecar.entity.customer.Driver;
import com.reindecar.entity.damage.DamageLocation;
import com.reindecar.entity.damage.DamageReport;
import com.reindecar.entity.damage.DamageSeverity;
import com.reindecar.entity.damage.DamageType;
import com.reindecar.entity.maintenance.MaintenanceRecord;
import com.reindecar.entity.maintenance.MaintenanceType;
import com.reindecar.entity.pricing.RentalType;
import com.reindecar.entity.rental.Rental;
import com.reindecar.entity.rental.RentalStatus;
import com.reindecar.entity.user.Role;
import com.reindecar.entity.user.User;
import com.reindecar.entity.vehicle.*;
import com.reindecar.repository.branch.BranchRepository;
import com.reindecar.repository.customer.CustomerRepository;
import com.reindecar.repository.customer.DriverRepository;
import com.reindecar.repository.damage.DamageReportRepository;
import com.reindecar.repository.maintenance.MaintenanceRecordRepository;
import com.reindecar.repository.rental.RentalRepository;
import com.reindecar.repository.user.UserRepository;
import com.reindecar.repository.vehicle.VehicleCategoryRepository;
import com.reindecar.repository.vehicle.VehicleRepository;
import com.reindecar.repository.vehicle.VehicleStatusHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@Configuration
@Profile("dev")
@RequiredArgsConstructor
@Slf4j
public class DataSeeder {

    @Bean
    CommandLineRunner initDatabase(
            BranchRepository branchRepository,
            UserRepository userRepository,
            VehicleCategoryRepository vehicleCategoryRepository,
            VehicleRepository vehicleRepository,
            DriverRepository driverRepository,
            CustomerRepository customerRepository,
            RentalRepository rentalRepository,
            DamageReportRepository damageReportRepository,
            MaintenanceRecordRepository maintenanceRecordRepository,
            VehicleStatusHistoryRepository vehicleStatusHistoryRepository,
            PasswordEncoder passwordEncoder) {
        
        return args -> {
            log.info("Starting data seeding for development environment...");

            // 1. Seed Branches
            if (branchRepository.count() == 0) {
                log.info("Seeding branches...");
                
                Branch istanbul = Branch.create(
                    "IST01",
                    "Istanbul Merkez",
                    "Istanbul",
                    "Kadıköy",
                    "Bağdat Caddesi No: 123",
                    "2165550101",
                    "istanbul@reindecar.com"
                );
                branchRepository.save(istanbul);

                Branch ankara = Branch.create(
                    "ANK01",
                    "Ankara Merkez",
                    "Ankara",
                    "Çankaya",
                    "Tunalı Hilmi Caddesi No: 45",
                    "3125550101",
                    "ankara@reindecar.com"
                );
                branchRepository.save(ankara);

                Branch izmir = Branch.create(
                    "IZM01",
                    "Izmir Merkez",
                    "Izmir",
                    "Konak",
                    "Alsancak Mahallesi No: 67",
                    "2325550101",
                    "izmir@reindecar.com"
                );
                branchRepository.save(izmir);

                log.info("Seeded {} branches", branchRepository.count());
            }

            // 2. Seed Users
            if (userRepository.count() == 0) {
                log.info("Seeding users...");

                User admin = User.create(
                    "admin",
                    "admin@reindecar.com",
                    passwordEncoder.encode("Admin123"),
                    "System",
                    "Administrator",
                    Role.ADMIN,
                    null
                );
                userRepository.save(admin);

                Branch istanbul = branchRepository.findByCode("IST01").orElse(null);
                Branch ankara = branchRepository.findByCode("ANK01").orElse(null);
                
                if (istanbul != null) {
                    User operator1 = User.create(
                        "operator_istanbul",
                        "operator.istanbul@reindecar.com",
                        passwordEncoder.encode("Operator123"),
                        "Ahmet",
                        "Yılmaz",
                        Role.OPERATOR,
                        istanbul.getId()
                    );
                    userRepository.save(operator1);
                }

                if (ankara != null) {
                    User operator2 = User.create(
                        "operator_ankara",
                        "operator.ankara@reindecar.com",
                        passwordEncoder.encode("Operator123"),
                        "Mehmet",
                        "Demir",
                        Role.OPERATOR,
                        ankara.getId()
                    );
                    userRepository.save(operator2);
                }

                log.info("Seeded {} users", userRepository.count());
            }

            // 3. Seed Vehicle Categories
            if (vehicleCategoryRepository.count() == 0) {
                log.info("Seeding vehicle categories...");

                VehicleCategory pool = VehicleCategory.create(
                    "POOL",
                    "Havuz Araçlar",
                    "Genel kullanım için havuz araçları",
                    Money.tl(1500),
                    1
                );
                vehicleCategoryRepository.save(pool);

                VehicleCategory executive = VehicleCategory.create(
                    "EXECUTIVE",
                    "Yönetici Araçlar",
                    "Yönetici kadrosu için tahsisli araçlar",
                    Money.tl(3000),
                    2
                );
                vehicleCategoryRepository.save(executive);

                VehicleCategory dedicated = VehicleCategory.create(
                    "DEDICATED",
                    "Özel Tahsis",
                    "Belirli müşterilere özel tahsis edilmiş araçlar",
                    Money.tl(2500),
                    3
                );
                vehicleCategoryRepository.save(dedicated);

                VehicleCategory protocol = VehicleCategory.create(
                    "PROTOCOL",
                    "Makam Araçları",
                    "Protokol ve makam hizmetleri için araçlar",
                    Money.tl(5000),
                    4
                );
                vehicleCategoryRepository.save(protocol);

                VehicleCategory welcome = VehicleCategory.create(
                    "WELCOME",
                    "Karşılama Araçlar",
                    "Havalimanı ve özel karşılama hizmetleri için araçlar",
                    Money.tl(2000),
                    5
                );
                vehicleCategoryRepository.save(welcome);

                log.info("Seeded {} vehicle categories", vehicleCategoryRepository.count());
            }

            // 4. Seed Vehicles
            if (vehicleRepository.count() == 0) {
                log.info("Seeding vehicles...");

                Branch istanbul = branchRepository.findByCode("IST01").orElse(null);
                Branch ankara = branchRepository.findByCode("ANK01").orElse(null);
                Branch izmir = branchRepository.findByCode("IZM01").orElse(null);

                VehicleCategory pool = vehicleCategoryRepository.findByCode("POOL").orElse(null);
                VehicleCategory executive = vehicleCategoryRepository.findByCode("EXECUTIVE").orElse(null);
                VehicleCategory dedicated = vehicleCategoryRepository.findByCode("DEDICATED").orElse(null);
                VehicleCategory protocol = vehicleCategoryRepository.findByCode("PROTOCOL").orElse(null);
                VehicleCategory welcome = vehicleCategoryRepository.findByCode("WELCOME").orElse(null);

                if (istanbul != null && pool != null) {
                    Money dailyPrice = Money.tl(1500);
                    Money weeklyPrice = Money.tl(9000);
                    Money monthlyPrice = Money.tl(30000);
                    
                    Vehicle egea = Vehicle.create(
                        "34ABC123",
                        "TR123456789012345",
                        "Fiat",
                        "Egea",
                        2023,
                        "White",
                        FuelType.DIESEL,
                        Transmission.MANUAL,
                        1300,
                        5,
                        pool.getId(),
                        istanbul.getId(),
                        15000,
                        LocalDate.now().plusYears(1),
                        LocalDate.now().plusYears(2),
                        LocalDate.of(2023, 1, 15),
                        dailyPrice,
                        weeklyPrice,
                        monthlyPrice,
                        "Havuz aracı - genel kullanım"
                    );
                    vehicleRepository.save(egea);
                }

                if (istanbul != null && executive != null) {
                    Money dailyPrice = Money.tl(3000);
                    Money weeklyPrice = Money.tl(18000);
                    Money monthlyPrice = Money.tl(60000);
                    
                    Vehicle passat = Vehicle.create(
                        "34DEF456",
                        "TR123456789012346",
                        "Volkswagen",
                        "Passat",
                        2023,
                        "Black",
                        FuelType.DIESEL,
                        Transmission.AUTOMATIC,
                        1600,
                        5,
                        executive.getId(),
                        istanbul.getId(),
                        25000,
                        LocalDate.now().plusYears(1),
                        LocalDate.now().plusYears(2),
                        LocalDate.of(2023, 3, 20),
                        dailyPrice,
                        weeklyPrice,
                        monthlyPrice,
                        "Yönetici aracı"
                    );
                    vehicleRepository.save(passat);
                }

                if (ankara != null && dedicated != null) {
                    Money dailyPrice = Money.tl(2500);
                    Money weeklyPrice = Money.tl(15000);
                    Money monthlyPrice = Money.tl(50000);
                    
                    Vehicle megane = Vehicle.create(
                        "06GHI789",
                        "TR123456789012347",
                        "Renault",
                        "Megane",
                        2024,
                        "Red",
                        FuelType.GASOLINE,
                        Transmission.AUTOMATIC,
                        1000,
                        5,
                        dedicated.getId(),
                        ankara.getId(),
                        5000,
                        LocalDate.now().plusYears(1),
                        LocalDate.now().plusYears(2),
                        LocalDate.of(2024, 1, 10),
                        dailyPrice,
                        weeklyPrice,
                        monthlyPrice,
                        "Özel tahsis - Müşteri XYZ"
                    );
                    vehicleRepository.save(megane);
                }

                if (izmir != null && welcome != null) {
                    Money dailyPrice = Money.tl(2000);
                    Money weeklyPrice = Money.tl(12000);
                    Money monthlyPrice = Money.tl(40000);
                    
                    Vehicle xc90 = Vehicle.create(
                        "35JKL012",
                        "TR123456789012348",
                        "Volvo",
                        "XC90",
                        2023,
                        "Silver",
                        FuelType.HYBRID,
                        Transmission.AUTOMATIC,
                        2000,
                        7,
                        welcome.getId(),
                        izmir.getId(),
                        30000,
                        LocalDate.now().plusYears(1),
                        LocalDate.now().plusYears(2),
                        LocalDate.of(2023, 6, 15),
                        dailyPrice,
                        weeklyPrice,
                        monthlyPrice,
                        "Havalimanı karşılama"
                    );
                    vehicleRepository.save(xc90);
                }

                if (istanbul != null && protocol != null) {
                    Money dailyPrice = Money.tl(5000);
                    Money weeklyPrice = Money.tl(30000);
                    Money monthlyPrice = Money.tl(100000);
                    
                    Vehicle bmw5 = Vehicle.create(
                        "34MNO345",
                        "TR123456789012349",
                        "BMW",
                        "520i",
                        2024,
                        "Blue",
                        FuelType.GASOLINE,
                        Transmission.AUTOMATIC,
                        1600,
                        5,
                        protocol.getId(),
                        istanbul.getId(),
                        8000,
                        LocalDate.now().plusYears(1),
                        LocalDate.now().plusYears(2),
                        LocalDate.of(2024, 2, 1),
                        dailyPrice,
                        weeklyPrice,
                        monthlyPrice,
                        "Makam aracı - Genel Müdür"
                    );
                    vehicleRepository.save(bmw5);
                }

                if (ankara != null && protocol != null) {
                    Money dailyPrice = Money.tl(7000);
                    Money weeklyPrice = Money.tl(42000);
                    Money monthlyPrice = Money.tl(140000);
                    
                    Vehicle mercedesS = Vehicle.create(
                        "06PQR678",
                        "TR123456789012350",
                        "Mercedes-Benz",
                        "S400",
                        2024,
                        "Black",
                        FuelType.DIESEL,
                        Transmission.AUTOMATIC,
                        3000,
                        5,
                        protocol.getId(),
                        ankara.getId(),
                        2000,
                        LocalDate.now().plusYears(1),
                        LocalDate.now().plusYears(2),
                        LocalDate.of(2024, 5, 10),
                        dailyPrice,
                        weeklyPrice,
                        monthlyPrice,
                        "Makam aracı - CEO"
                    );
                    vehicleRepository.save(mercedesS);
                }

                log.info("Seeded {} vehicles", vehicleRepository.count());
            }

            if (driverRepository.count() == 0) {
                Driver driver1 = Driver.create(
                    null,
                    "72526094184",
                    "Ahmet",
                    "Yilmaz",
                    "5321234567",
                    "A1B2C3D4E5",
                    "B",
                    LocalDate.now().plusYears(2),
                    true
                );
                driverRepository.save(driver1);

                Driver driver2 = Driver.create(
                    null,
                    "56944169082",
                    "Ayse",
                    "Demir",
                    "5321234568",
                    "B9C8D7E6F5",
                    "B",
                    LocalDate.now().plusYears(3),
                    false
                );
                driverRepository.save(driver2);
            }

            // 5. Seed Customers
            if (customerRepository.count() == 0) {
                log.info("Seeding customers...");

                CustomerPerson customer1 = CustomerPerson.create(
                    "98214440002",
                    "Mehmet",
                    "Kaya",
                    LocalDate.of(1985, 5, 15),
                    "5321112233",
                    "mehmet.kaya@email.com",
                    "Atatürk Mahallesi, İnönü Caddesi No: 45",
                    "Istanbul",
                    "D12345678",
                    "B",
                    LocalDate.now().plusYears(2)
                );
                customer1.updateCreditScore(750);
                customerRepository.save(customer1);

                CustomerPerson customer2 = CustomerPerson.create(
                    "50027150202",
                    "Ayşe",
                    "Yılmaz",
                    LocalDate.of(1990, 8, 22),
                    "5322223344",
                    "ayse.yilmaz@email.com",
                    "Kızılay Mahallesi, Tunalı Hilmi Caddesi No: 12",
                    "Ankara",
                    "D23456789",
                    "B",
                    LocalDate.now().plusYears(1)
                );
                customer2.updateCreditScore(680);
                customerRepository.save(customer2);

                CustomerCompany company1 = CustomerCompany.create(
                    "ABC Teknoloji A.Ş.",
                    "6172975411",
                    "Kadıköy",
                    "TR-12345",
                    "2165551234",
                    "info@abcteknoloji.com",
                    "Bağdat Caddesi No: 100",
                    "Istanbul",
                    "Bağdat Caddesi No: 100",
                    "Ali Veli",
                    "5323334455",
                    "Teknoloji",
                    50
                );
                company1.updateCreditScore(820);
                customerRepository.save(company1);

                CustomerCompany company2 = CustomerCompany.create(
                    "XYZ İnşaat Ltd. Şti.",
                    "0896167417",
                    "Çankaya",
                    "TR-23456",
                    "3125555678",
                    "info@xyzinşaat.com",
                    "Tunalı Hilmi Caddesi No: 200",
                    "Ankara",
                    "Tunalı Hilmi Caddesi No: 200",
                    "Fatma Demir",
                    "5324445566",
                    "İnşaat",
                    120
                );
                company2.updateCreditScore(790);
                customerRepository.save(company2);

                // Dashboard için ek müşteriler
                CustomerPerson customer3 = CustomerPerson.create(
                    "89843307552",
                    "Ali",
                    "Özkan",
                    LocalDate.of(1988, 3, 10),
                    "5325556677",
                    "ali.ozkan@email.com",
                    "Bostancı Mahallesi, Bağdat Caddesi No: 200",
                    "Istanbul",
                    "D34567890",
                    "B",
                    LocalDate.now().plusYears(3)
                );
                customer3.updateCreditScore(720);
                customerRepository.save(customer3);

                CustomerPerson customer4 = CustomerPerson.create(
                    "90281257194",
                    "Zeynep",
                    "Arslan",
                    LocalDate.of(1992, 11, 5),
                    "5326667788",
                    "zeynep.arslan@email.com",
                    "Çankaya Mahallesi, Atatürk Bulvarı No: 50",
                    "Ankara",
                    "D45678901",
                    "B",
                    LocalDate.now().plusMonths(6)
                );
                customer4.updateCreditScore(690);
                customerRepository.save(customer4);

                CustomerCompany company3 = CustomerCompany.create(
                    "DEF Lojistik A.Ş.",
                    "3500368512",
                    "Kadıköy",
                    "TR-34567",
                    "2165559012",
                    "info@deflojistik.com",
                    "Moda Caddesi No: 300",
                    "Istanbul",
                    "Moda Caddesi No: 300",
                    "Can Yıldız",
                    "5327778899",
                    "Lojistik",
                    80
                );
                company3.updateCreditScore(810);
                customerRepository.save(company3);

                log.info("Seeded {} customers", customerRepository.count());
            }

            // 6. Seed Rentals
            if (rentalRepository.count() == 0) {
                log.info("Seeding rentals...");

                Vehicle egea = vehicleRepository.findByPlateNumberAndDeletedFalse("34ABC123").orElse(null);
                Vehicle passat = vehicleRepository.findByPlateNumberAndDeletedFalse("34DEF456").orElse(null);
                Vehicle megane = vehicleRepository.findByPlateNumberAndDeletedFalse("06GHI789").orElse(null);
                Vehicle xc90 = vehicleRepository.findByPlateNumberAndDeletedFalse("35JKL012").orElse(null);
                Vehicle bmw5 = vehicleRepository.findByPlateNumberAndDeletedFalse("34MNO345").orElse(null);
                Vehicle mercedesS = vehicleRepository.findByPlateNumberAndDeletedFalse("06PQR678").orElse(null);

                Customer customer1 = customerRepository.findAll().stream()
                    .filter(c -> c.getDisplayName().contains("Mehmet"))
                    .findFirst().orElse(null);
                Customer customer2 = customerRepository.findAll().stream()
                    .filter(c -> c.getDisplayName().contains("Ayşe"))
                    .findFirst().orElse(null);
                Customer customer3 = customerRepository.findAll().stream()
                    .filter(c -> c.getDisplayName().contains("Ali"))
                    .findFirst().orElse(null);
                Customer customer4 = customerRepository.findAll().stream()
                    .filter(c -> c.getDisplayName().contains("Zeynep"))
                    .findFirst().orElse(null);
                Customer company1 = customerRepository.findAll().stream()
                    .filter(c -> c.getDisplayName().contains("ABC"))
                    .findFirst().orElse(null);
                Customer company2 = customerRepository.findAll().stream()
                    .filter(c -> c.getDisplayName().contains("XYZ"))
                    .findFirst().orElse(null);
                Customer company3 = customerRepository.findAll().stream()
                    .filter(c -> c.getDisplayName().contains("DEF"))
                    .findFirst().orElse(null);
                // company3 kullanılmıyor ama gelecekte kullanılabilir

                Branch istanbul = branchRepository.findByCode("IST01").orElse(null);
                Branch ankara = branchRepository.findByCode("ANK01").orElse(null);
                Branch izmir = branchRepository.findByCode("IZM01").orElse(null);

                if (egea != null && customer1 != null && istanbul != null) {
                    // Aktif kiralama
                    Rental activeRental = Rental.create(
                        "RENT-2026-001",
                        RentalType.DAILY,
                        egea.getId(),
                        customer1.getId(),
                        customer1.getCustomerType(),
                        null,
                        null,
                        istanbul.getId(),
                        istanbul.getId(),
                        LocalDate.now().minusDays(1),
                        LocalDate.now().plusDays(5),
                        null,
                        null,
                        null,
                        Money.tl(1500),
                        Money.tl(15000),
                        Money.tl(500),
                        "Egea aktif kiralama",
                        "operator_istanbul"
                    );
                    activeRental.reserve();
                    activeRental.activate(15000);
                    rentalRepository.save(activeRental);
                }

                if (passat != null && customer2 != null && istanbul != null) {
                    // Tamamlanmış kiralama
                    Rental completedRental = Rental.create(
                        "RENT-2025-100",
                        RentalType.WEEKLY,
                        passat.getId(),
                        customer2.getId(),
                        customer2.getCustomerType(),
                        null,
                        null,
                        istanbul.getId(),
                        istanbul.getId(),
                        LocalDate.now().minusDays(1),
                        LocalDate.now(),
                        null,
                        null,
                        null,
                        Money.tl(3000),
                        Money.tl(21000),
                        Money.tl(1000),
                        "Passat haftalık kiralama",
                        "operator_istanbul"
                    );
                    completedRental.reserve();
                    completedRental.activate(25000);
                    completedRental.startReturn();
                    completedRental.complete(LocalDate.now(), 26500, Money.tl(500));
                    rentalRepository.save(completedRental);
                }

                if (megane != null && company1 != null && ankara != null) {
                    // Aylık kiralama
                    Rental monthlyRental = Rental.create(
                        "RENT-2025-200",
                        RentalType.MONTHLY,
                        megane.getId(),
                        company1.getId(),
                        company1.getCustomerType(),
                        null,
                        null,
                        ankara.getId(),
                        ankara.getId(),
                        LocalDate.now().minusDays(1),
                        LocalDate.now(),
                        null,
                        null,
                        null,
                        Money.tl(2500),
                        Money.tl(75000),
                        Money.tl(2000),
                        "Megane aylık kiralama",
                        "operator_ankara"
                    );
                    monthlyRental.reserve();
                    monthlyRental.activate(5000);
                    monthlyRental.startReturn();
                    monthlyRental.complete(LocalDate.now(), 8500, Money.zero());
                    rentalRepository.save(monthlyRental);
                }

                // Dashboard için ek kiralamalar
                if (xc90 != null && customer3 != null && izmir != null) {
                    // Rezerve edilmiş kiralama
                    Rental reservedRental = Rental.create(
                        "RENT-2026-002",
                        RentalType.DAILY,
                        xc90.getId(),
                        customer3.getId(),
                        customer3.getCustomerType(),
                        null,
                        null,
                        izmir.getId(),
                        izmir.getId(),
                        LocalDate.now().plusDays(3),
                        LocalDate.now().plusDays(10),
                        null,
                        null,
                        null,
                        Money.tl(2000),
                        Money.tl(16000),
                        Money.tl(500),
                        "XC90 rezerve kiralama",
                        "operator_istanbul"
                    );
                    reservedRental.reserve();
                    rentalRepository.save(reservedRental);
                }

                if (bmw5 != null && company2 != null && istanbul != null) {
                    // Gecikmiş kiralama
                    Rental overdueRental = Rental.create(
                        "RENT-2025-300",
                        RentalType.WEEKLY,
                        bmw5.getId(),
                        company2.getId(),
                        company2.getCustomerType(),
                        null,
                        null,
                        istanbul.getId(),
                        istanbul.getId(),
                        LocalDate.now().minusDays(1),
                        LocalDate.now().minusDays(1),
                        null,
                        null,
                        null,
                        Money.tl(5000),
                        Money.tl(35000),
                        Money.tl(1500),
                        "BMW 520i gecikmiş kiralama",
                        "operator_istanbul"
                    );
                    overdueRental.reserve();
                    overdueRental.activate(8000);
                    overdueRental.markAsOverdue();
                    rentalRepository.save(overdueRental);
                }

                if (mercedesS != null && customer4 != null && ankara != null) {
                    // Tamamlanmış kiralama (daha eski)
                    Rental oldRental = Rental.create(
                        "RENT-2025-400",
                        RentalType.DAILY,
                        mercedesS.getId(),
                        customer4.getId(),
                        customer4.getCustomerType(),
                        null,
                        null,
                        ankara.getId(),
                        ankara.getId(),
                        LocalDate.now().minusDays(1),
                        LocalDate.now().minusDays(1),
                        null,
                        null,
                        null,
                        Money.tl(7000),
                        Money.tl(49000),
                        Money.tl(2000),
                        "Mercedes S400 eski kiralama",
                        "operator_ankara"
                    );
                    oldRental.reserve();
                    oldRental.activate(2000);
                    oldRental.startReturn();
                    oldRental.complete(LocalDate.now().minusDays(1), 3500, Money.tl(1000));
                    rentalRepository.save(oldRental);
                }

                if (passat != null && customer3 != null && istanbul != null) {
                    // İptal edilmiş kiralama
                    Rental cancelledRental = Rental.create(
                        "RENT-2026-003",
                        RentalType.DAILY,
                        passat.getId(),
                        customer3.getId(),
                        customer3.getCustomerType(),
                        null,
                        null,
                        istanbul.getId(),
                        istanbul.getId(),
                        LocalDate.now().plusDays(5),
                        LocalDate.now().plusDays(12),
                        null,
                        null,
                        null,
                        Money.tl(3000),
                        Money.tl(24000),
                        Money.tl(0),
                        "Passat iptal edilmiş kiralama",
                        "operator_istanbul"
                    );
                    cancelledRental.cancel();
                    rentalRepository.save(cancelledRental);
                }

                log.info("Seeded {} rentals", rentalRepository.count());
            }

            // 7. Seed Damage Reports (Harita için farklı zone'larda)
            if (damageReportRepository.count() == 0) {
                log.info("Seeding damage reports...");

                Vehicle egea = vehicleRepository.findByPlateNumberAndDeletedFalse("34ABC123").orElse(null);
                Vehicle passat = vehicleRepository.findByPlateNumberAndDeletedFalse("34DEF456").orElse(null);

                Rental completedRental = rentalRepository.findByRentalNumber("RENT-2025-100").orElse(null);

                if (egea != null) {
                    // Aktif hasarlar (harita için)
                    DamageReport damage1 = DamageReport.create(
                        egea.getId(),
                        null,
                        LocalDate.now().minusDays(10),
                        DamageType.SCRATCH,
                        DamageLocation.FRONT_BUMPER,
                        DamageSeverity.MINOR,
                        "Ön tampon sağ köşesinde 10cm çizik",
                        Money.tl(1500),
                        "Ahmet Yılmaz"
                    );
                    damageReportRepository.save(damage1);

                    DamageReport damage2 = DamageReport.create(
                        egea.getId(),
                        null,
                        LocalDate.now().minusDays(5),
                        DamageType.DENT,
                        DamageLocation.LEFT_FRONT_DOOR,
                        DamageSeverity.MODERATE,
                        "Sol ön kapıda göçük",
                        Money.tl(2500),
                        "Mehmet Demir"
                    );
                    damageReportRepository.save(damage2);

                    DamageReport damage3 = DamageReport.create(
                        egea.getId(),
                        null,
                        LocalDate.now().minusDays(2),
                        DamageType.CRACK,
                        DamageLocation.WINDSHIELD,
                        DamageSeverity.MAJOR,
                        "Ön camda çatlak",
                        Money.tl(3500),
                        "Ayşe Kaya"
                    );
                    damageReportRepository.save(damage3);

                    // Onarılmış hasar
                    DamageReport repairedDamage = DamageReport.create(
                        egea.getId(),
                        null,
                        LocalDate.now().minusDays(30),
                        DamageType.SCRATCH,
                        DamageLocation.REAR_BUMPER,
                        DamageSeverity.MINOR,
                        "Arka tampon çizik",
                        Money.tl(1200),
                        "Ahmet Yılmaz"
                    );
                    repairedDamage.markAsRepaired(LocalDate.now().minusDays(20), Money.tl(1000));
                    damageReportRepository.save(repairedDamage);
                }

                if (passat != null && completedRental != null) {
                    // Kiralama ile ilişkili hasar
                    DamageReport rentalDamage = DamageReport.create(
                        passat.getId(),
                        completedRental.getId(),
                        LocalDate.now().minusDays(25),
                        DamageType.DENT,
                        DamageLocation.FRONT_RIGHT_FENDER,
                        DamageSeverity.MODERATE,
                        "Sağ ön çamurlukta göçük",
                        Money.tl(2000),
                        "Ayşe Yılmaz"
                    );
                    damageReportRepository.save(rentalDamage);

                    // Dashboard için ek hasarlar (farklı zone'larda)
                    DamageReport damage4 = DamageReport.create(
                        passat.getId(),
                        null,
                        LocalDate.now().minusDays(15),
                        DamageType.SCRATCH,
                        DamageLocation.REAR_RIGHT_FENDER,
                        DamageSeverity.MINOR,
                        "Sağ arka çamurlukta çizik",
                        Money.tl(1800),
                        "Mehmet Kaya"
                    );
                    damageReportRepository.save(damage4);

                    DamageReport damage5 = DamageReport.create(
                        passat.getId(),
                        null,
                        LocalDate.now().minusDays(8),
                        DamageType.CRACK,
                        DamageLocation.LEFT_MIRROR,
                        DamageSeverity.MODERATE,
                        "Sol aynada çatlak",
                        Money.tl(1200),
                        "Ali Özkan"
                    );
                    damageReportRepository.save(damage5);
                }

                // Dashboard için diğer araçlara hasar ekle
                Vehicle megane = vehicleRepository.findByPlateNumberAndDeletedFalse("06GHI789").orElse(null);
                Vehicle xc90 = vehicleRepository.findByPlateNumberAndDeletedFalse("35JKL012").orElse(null);

                if (megane != null) {
                    DamageReport damage6 = DamageReport.create(
                        megane.getId(),
                        null,
                        LocalDate.now().minusDays(20),
                        DamageType.DENT,
                        DamageLocation.RIGHT_FRONT_DOOR,
                        DamageSeverity.MAJOR,
                        "Sağ ön kapıda büyük göçük",
                        Money.tl(4000),
                        "Zeynep Arslan"
                    );
                    damageReportRepository.save(damage6);
                }

                if (xc90 != null) {
                    DamageReport damage7 = DamageReport.create(
                        xc90.getId(),
                        null,
                        LocalDate.now().minusDays(12),
                        DamageType.SCRATCH,
                        DamageLocation.ROOF,
                        DamageSeverity.MINOR,
                        "Tavanda hafif çizik",
                        Money.tl(2500),
                        "Can Yıldız"
                    );
                    damageReportRepository.save(damage7);
                }

                log.info("Seeded {} damage reports", damageReportRepository.count());
            }

            // 8. Seed Maintenance Records (Harita için farklı zone'larda)
            if (maintenanceRecordRepository.count() == 0) {
                log.info("Seeding maintenance records...");

                Vehicle egea = vehicleRepository.findByPlateNumberAndDeletedFalse("34ABC123").orElse(null);
                Vehicle passat = vehicleRepository.findByPlateNumberAndDeletedFalse("34DEF456").orElse(null);
                Vehicle megane = vehicleRepository.findByPlateNumberAndDeletedFalse("06GHI789").orElse(null);

                if (egea != null) {
                    // Boyama (Zone 1 ve 4)
                    MaintenanceRecord paint1 = MaintenanceRecord.create(
                        egea.getId(),
                        MaintenanceType.PAINT,
                        LocalDate.now().minusDays(20),
                        14500,
                        Money.tl(3000),
                        "Oto Boya Servisi",
                        "Ön tampon ve sol ön çamurluk boyama",
                        Arrays.asList(1, 4),
                        Arrays.asList("Ön tampon", "Sol ön çamurluk"),
                        "Beyaz"
                    );
                    maintenanceRecordRepository.save(paint1);

                    // Tamir (Zone 6)
                    MaintenanceRecord repair1 = MaintenanceRecord.create(
                        egea.getId(),
                        MaintenanceType.REPAIR,
                        LocalDate.now().minusDays(15),
                        14800,
                        Money.tl(2000),
                        "Kaporta Servisi",
                        "Sol ön kapı göçük düzeltme",
                        Arrays.asList(6),
                        Arrays.asList("Sol ön kapı"),
                        null
                    );
                    maintenanceRecordRepository.save(repair1);

                    // Servis bakımı
                    MaintenanceRecord service1 = MaintenanceRecord.create(
                        egea.getId(),
                        MaintenanceType.SERVICE,
                        LocalDate.now().minusDays(10),
                        15000,
                        Money.tl(1500),
                        "Fiat Yetkili Servis",
                        "Periyodik bakım - yağ değişimi, filtre değişimi",
                        null,
                        Arrays.asList("Motor yağı", "Yağ filtresi", "Hava filtresi"),
                        null
                    );
                    maintenanceRecordRepository.save(service1);
                }

                if (passat != null) {
                    // Parça değişimi (Zone 1)
                    MaintenanceRecord partReplacement = MaintenanceRecord.create(
                        passat.getId(),
                        MaintenanceType.PART_REPLACEMENT,
                        LocalDate.now().minusDays(25),
                        26000,
                        Money.tl(4500),
                        "Volkswagen Yetkili Servis",
                        "Ön tampon değişimi",
                        Arrays.asList(1),
                        Arrays.asList("Ön tampon", "Ön tampon montaj parçaları"),
                        null
                    );
                    maintenanceRecordRepository.save(partReplacement);

                    // Fren servisi
                    MaintenanceRecord brakeService = MaintenanceRecord.create(
                        passat.getId(),
                        MaintenanceType.BRAKE_SERVICE,
                        LocalDate.now().minusDays(15),
                        26500,
                        Money.tl(2500),
                        "Volkswagen Yetkili Servis",
                        "Fren balata ve disk değişimi",
                        Arrays.asList(4, 10),
                        Arrays.asList("Ön fren balata", "Arka fren balata", "Fren diskleri"),
                        null
                    );
                    maintenanceRecordRepository.save(brakeService);
                }

                if (megane != null) {
                    // Kaporta işi (Zone 6, 12)
                    MaintenanceRecord bodyWork = MaintenanceRecord.create(
                        megane.getId(),
                        MaintenanceType.BODY_WORK,
                        LocalDate.now().minusDays(40),
                        6000,
                        Money.tl(5000),
                        "Renault Yetkili Servis",
                        "Yan kapılar kaporta işi",
                        Arrays.asList(6, 12),
                        Arrays.asList("Sol yan kapılar", "Sağ yan kapılar"),
                        null
                    );
                    maintenanceRecordRepository.save(bodyWork);
                }

                // Dashboard için ek bakım kayıtları
                Vehicle xc90 = vehicleRepository.findByPlateNumberAndDeletedFalse("35JKL012").orElse(null);
                Vehicle bmw5 = vehicleRepository.findByPlateNumberAndDeletedFalse("34MNO345").orElse(null);
                Vehicle mercedesS = vehicleRepository.findByPlateNumberAndDeletedFalse("06PQR678").orElse(null);

                if (xc90 != null) {
                    // Lastik değişimi
                    MaintenanceRecord tireChange = MaintenanceRecord.create(
                        xc90.getId(),
                        MaintenanceType.TIRE_CHANGE,
                        LocalDate.now().minusDays(30),
                        28000,
                        Money.tl(8000),
                        "Volvo Yetkili Servis",
                        "4 mevsim lastik değişimi",
                        Arrays.asList(1, 4, 7, 10),
                        Arrays.asList("Sol ön lastik", "Sağ ön lastik", "Sol arka lastik", "Sağ arka lastik"),
                        null
                    );
                    maintenanceRecordRepository.save(tireChange);

                    // Yağ değişimi
                    MaintenanceRecord oilChange = MaintenanceRecord.create(
                        xc90.getId(),
                        MaintenanceType.OIL_CHANGE,
                        LocalDate.now().minusDays(5),
                        30000,
                        Money.tl(2000),
                        "Volvo Yetkili Servis",
                        "Motor yağı ve filtre değişimi",
                        null,
                        Arrays.asList("Motor yağı", "Yağ filtresi"),
                        null
                    );
                    maintenanceRecordRepository.save(oilChange);
                }

                if (bmw5 != null) {
                    // Elektrik tamiri
                    MaintenanceRecord electricalRepair = MaintenanceRecord.create(
                        bmw5.getId(),
                        MaintenanceType.ELECTRICAL_REPAIR,
                        LocalDate.now().minusDays(18),
                        12000,
                        Money.tl(3500),
                        "BMW Yetkili Servis",
                        "Elektrik sistemi bakımı ve tamiri",
                        null,
                        Arrays.asList("Akü", "Alternatör"),
                        null
                    );
                    maintenanceRecordRepository.save(electricalRepair);
                }

                if (mercedesS != null) {
                    // Muayene
                    MaintenanceRecord inspection = MaintenanceRecord.create(
                        mercedesS.getId(),
                        MaintenanceType.INSPECTION,
                        LocalDate.now().minusDays(45),
                        5000,
                        Money.tl(500),
                        "Mercedes-Benz Yetkili Servis",
                        "Periyodik muayene",
                        null,
                        null,
                        null
                    );
                    maintenanceRecordRepository.save(inspection);
                }

                log.info("Seeded {} maintenance records", maintenanceRecordRepository.count());
            }

            // 9. Seed Vehicle Status History
            if (vehicleStatusHistoryRepository.count() == 0) {
                log.info("Seeding vehicle status history...");

                Vehicle egea = vehicleRepository.findByPlateNumberAndDeletedFalse("34ABC123").orElse(null);
                Vehicle passat = vehicleRepository.findByPlateNumberAndDeletedFalse("34DEF456").orElse(null);

                Rental activeRental = rentalRepository.findByRentalNumber("RENT-2026-001").orElse(null);
                Rental completedRental = rentalRepository.findByRentalNumber("RENT-2025-100").orElse(null);

                if (egea != null) {
                    // Egea için geçmiş
                    VehicleStatusHistory history1 = VehicleStatusHistory.create(
                        egea.getId(),
                        null,
                        VehicleStatus.AVAILABLE,
                        "RENTAL",
                        null,
                        "Araç sisteme eklendi",
                        "system"
                    );
                    setChangedAt(history1, Instant.now().minus(Duration.ofDays(100)));
                    vehicleStatusHistoryRepository.save(history1);

                    if (activeRental != null) {
                        VehicleStatusHistory history2 = VehicleStatusHistory.create(
                            egea.getId(),
                            VehicleStatus.AVAILABLE,
                            VehicleStatus.RENTED,
                            "RENTAL",
                            activeRental.getId(),
                            "Kiralama aktif edildi: " + activeRental.getRentalNumber(),
                            "operator_istanbul"
                        );
                        setChangedAt(history2, Instant.now().minus(Duration.ofDays(5)));
                        vehicleStatusHistoryRepository.save(history2);
                    }
                }

                if (passat != null && completedRental != null) {
                    // Passat için geçmiş
                    VehicleStatusHistory history1 = VehicleStatusHistory.create(
                        passat.getId(),
                        null,
                        VehicleStatus.AVAILABLE,
                        "RENTAL",
                        null,
                        "Araç sisteme eklendi",
                        "system"
                    );
                    setChangedAt(history1, Instant.now().minus(Duration.ofDays(100)));
                    vehicleStatusHistoryRepository.save(history1);

                    VehicleStatusHistory history2 = VehicleStatusHistory.create(
                        passat.getId(),
                        VehicleStatus.AVAILABLE,
                        VehicleStatus.RENTED,
                        "RENTAL",
                        completedRental.getId(),
                        "Kiralama aktif edildi: " + completedRental.getRentalNumber(),
                        "operator_istanbul"
                    );
                    setChangedAt(history2, Instant.now().minus(Duration.ofDays(30)));
                    vehicleStatusHistoryRepository.save(history2);

                    VehicleStatusHistory history3 = VehicleStatusHistory.create(
                        passat.getId(),
                        VehicleStatus.RENTED,
                        VehicleStatus.AVAILABLE,
                        "RENTAL",
                        completedRental.getId(),
                        "Kiralama tamamlandı: " + completedRental.getRentalNumber(),
                        "operator_istanbul"
                    );
                    setChangedAt(history3, Instant.now().minus(Duration.ofDays(23)));
                    vehicleStatusHistoryRepository.save(history3);
                }

                log.info("Seeded {} vehicle status history records", vehicleStatusHistoryRepository.count());
            }

            log.info("Data seeding completed successfully!");
            log.info("Default credentials:");
            log.info("  ADMIN - username: admin, password: Admin123");
            log.info("  OPERATOR (Istanbul) - username: operator_istanbul, password: Operator123");
            log.info("  OPERATOR (Ankara) - username: operator_ankara, password: Operator123");
            log.info("");
            log.info("Seeded data summary:");
            log.info("  - Vehicles: {} (including Egea 34ABC123)", vehicleRepository.count());
            log.info("  - Customers: {} (4 personal, 3 company)", customerRepository.count());
            log.info("  - Rentals: {} (1 active, 1 reserved, 1 overdue, 1 cancelled, 3 completed)", rentalRepository.count());
            log.info("  - Damage Reports: {} (for map visualization - multiple zones)", damageReportRepository.count());
            log.info("  - Maintenance Records: {} (for map visualization - multiple zones)", maintenanceRecordRepository.count());
            log.info("  - Vehicle Status History: {}", vehicleStatusHistoryRepository.count());
            log.info("");
            log.info("Dashboard test data:");
            log.info("  - Egea (34ABC123): 3 active damages, 3 maintenance records");
            log.info("  - Passat (34DEF456): Multiple damages and maintenance records");
            log.info("  - Various rental statuses for dashboard widgets");
        };
    }

    @SuppressWarnings("java:S3011")
    private void setChangedAt(VehicleStatusHistory history, Instant changedAt) {
        try {
            java.lang.reflect.Field field = VehicleStatusHistory.class.getDeclaredField("changedAt");
            field.setAccessible(true);
            field.set(history, changedAt);
        } catch (Exception e) {
            log.warn("Could not set changedAt for VehicleStatusHistory: {}", e.getMessage());
        }
    }
}
