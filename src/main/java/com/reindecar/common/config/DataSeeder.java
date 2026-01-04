package com.reindecar.common.config;

import com.reindecar.common.valueobject.Money;
import com.reindecar.entity.branch.Branch;
import com.reindecar.entity.user.Role;
import com.reindecar.entity.user.User;
import com.reindecar.entity.vehicle.*;
import com.reindecar.repository.branch.BranchRepository;
import com.reindecar.repository.user.UserRepository;
import com.reindecar.repository.vehicle.VehicleCategoryRepository;
import com.reindecar.repository.vehicle.VehicleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;

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

                VehicleCategory eco = VehicleCategory.create(
                    "ECO",
                    "Economic Class",
                    "Fuel efficient, compact cars perfect for city driving",
                    Money.tl(1200),
                    1
                );
                vehicleCategoryRepository.save(eco);

                VehicleCategory comf = VehicleCategory.create(
                    "COMF",
                    "Comfort Class",
                    "Spacious sedans for comfortable long drives",
                    Money.tl(1800),
                    2
                );
                vehicleCategoryRepository.save(comf);

                VehicleCategory suv = VehicleCategory.create(
                    "SUV",
                    "SUV 4x4",
                    "Powerful SUVs for all terrains",
                    Money.tl(2500),
                    3
                );
                vehicleCategoryRepository.save(suv);

                VehicleCategory prm = VehicleCategory.create(
                    "PRM",
                    "Premium Class",
                    "Luxury vehicles for executive travel",
                    Money.tl(3500),
                    4
                );
                vehicleCategoryRepository.save(prm);

                VehicleCategory lux = VehicleCategory.create(
                    "LUX",
                    "Luxury Class",
                    "Top tier luxury cars",
                    Money.tl(5000),
                    5
                );
                vehicleCategoryRepository.save(lux);

                log.info("Seeded {} vehicle categories", vehicleCategoryRepository.count());
            }

            // 4. Seed Vehicles
            if (vehicleRepository.count() == 0) {
                log.info("Seeding vehicles...");

                Branch istanbul = branchRepository.findByCode("IST01").orElse(null);
                Branch ankara = branchRepository.findByCode("ANK01").orElse(null);
                Branch izmir = branchRepository.findByCode("IZM01").orElse(null);

                VehicleCategory eco = vehicleCategoryRepository.findByCode("ECO").orElse(null);
                VehicleCategory comf = vehicleCategoryRepository.findByCode("COMF").orElse(null);
                VehicleCategory suv = vehicleCategoryRepository.findByCode("SUV").orElse(null);
                VehicleCategory prm = vehicleCategoryRepository.findByCode("PRM").orElse(null);
                VehicleCategory lux = vehicleCategoryRepository.findByCode("LUX").orElse(null);

                if (istanbul != null && eco != null) {
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
                        eco.getId(),
                        istanbul.getId(),
                        15000,
                        LocalDate.now().plusYears(1),
                        LocalDate.now().plusYears(2),
                        LocalDate.of(2023, 1, 15),
                        eco.getDefaultDailyPrice(),
                        "Clean condition"
                    );
                    vehicleRepository.save(egea);
                }

                if (istanbul != null && comf != null) {
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
                        comf.getId(),
                        istanbul.getId(),
                        25000,
                        LocalDate.now().plusYears(1),
                        LocalDate.now().plusYears(2),
                        LocalDate.of(2023, 3, 20),
                        comf.getDefaultDailyPrice(),
                        "Business class choice"
                    );
                    vehicleRepository.save(passat);
                }

                if (ankara != null && eco != null) {
                    Vehicle clio = Vehicle.create(
                        "06GHI789",
                        "TR123456789012347",
                        "Renault",
                        "Clio",
                        2024,
                        "Red",
                        FuelType.GASOLINE,
                        Transmission.AUTOMATIC,
                        1000,
                        5,
                        eco.getId(),
                        ankara.getId(),
                        5000,
                        LocalDate.now().plusYears(1),
                        LocalDate.now().plusYears(2),
                        LocalDate.of(2024, 1, 10),
                        eco.getDefaultDailyPrice(),
                        "Brand new"
                    );
                    vehicleRepository.save(clio);
                }

                if (izmir != null && suv != null) {
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
                        suv.getId(),
                        izmir.getId(),
                        30000,
                        LocalDate.now().plusYears(1),
                        LocalDate.now().plusYears(2),
                        LocalDate.of(2023, 6, 15),
                        suv.getDefaultDailyPrice(),
                        "Premium SUV with extra safety"
                    );
                    vehicleRepository.save(xc90);
                }

                if (istanbul != null && prm != null) {
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
                        prm.getId(),
                        istanbul.getId(),
                        8000,
                        LocalDate.now().plusYears(1),
                        LocalDate.now().plusYears(2),
                        LocalDate.of(2024, 2, 1),
                        prm.getDefaultDailyPrice(),
                        "Executive car"
                    );
                    vehicleRepository.save(bmw5);
                }

                 if (ankara != null && lux != null) {
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
                        lux.getId(),
                        ankara.getId(),
                        2000,
                        LocalDate.now().plusYears(1),
                        LocalDate.now().plusYears(2),
                        LocalDate.of(2024, 5, 10),
                        lux.getDefaultDailyPrice(),
                        "Ultimate luxury"
                    );
                    vehicleRepository.save(mercedesS);
                }

                log.info("Seeded {} vehicles", vehicleRepository.count());
            }

            log.info("Data seeding completed successfully!");
            log.info("Default credentials:");
            log.info("  ADMIN - username: admin, password: Admin123");
            log.info("  OPERATOR (Istanbul) - username: operator_istanbul, password: Operator123");
            log.info("  OPERATOR (Ankara) - username: operator_ankara, password: Operator123");
        };
    }
}
