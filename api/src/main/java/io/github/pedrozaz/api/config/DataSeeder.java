package io.github.pedrozaz.api.config;

import io.github.pedrozaz.api.entity.Interaction;
import io.github.pedrozaz.api.entity.Restaurant;
import io.github.pedrozaz.api.entity.User;
import io.github.pedrozaz.api.repository.InteractionRepository;
import io.github.pedrozaz.api.repository.RestaurantRepository;
import io.github.pedrozaz.api.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;


@Slf4j
@Component
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RestaurantRepository restaurantRepository;
    private final InteractionRepository interactionRepository;
    private final Random random = new Random();

    @Autowired
    public DataSeeder(UserRepository userRepository, RestaurantRepository restaurantRepository, InteractionRepository interactionRepository) {
        log.info("Init DataSeeder ...");
        this.userRepository = userRepository;
        this.restaurantRepository = restaurantRepository;
        this.interactionRepository = interactionRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (userRepository.count() > 0) {
            log.warn("Database already seeded. Skipping...");
            return;
        }

        log.info("Seeding database with synthetic data....");

        String[] cuisines = {"Japanese", "Italian", "Burger", "Vegan", "Brazilian"};
        List<Restaurant> restaurants = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            String cuisine = cuisines[random.nextInt(cuisines.length)];
            Restaurant r = Restaurant.builder()
                    .restaurantId(UUID.randomUUID().toString())
                    .name(cuisine + " Place " + i)
                    .cuisineType(cuisine)
                    .priceRange(random.nextInt(5) + 1)
                    .build();
            restaurants.add(r);
        }
        restaurantRepository.saveAll(restaurants);

        List<User> users = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            User u = new User(UUID.randomUUID().toString(), "User " + i, LocalDateTime.now());
            users.add(u);
        }
        userRepository.saveAll(users);

        List<Interaction> interactions = new ArrayList<>();
        for (User user : users) {
            String favoriteCuisine = cuisines[random.nextInt(cuisines.length)];
            int reviewsCount = random.nextInt(11) + 10;

            for (int j = 0; j < reviewsCount; j++) {
                Restaurant r = restaurants.get(random.nextInt(restaurants.size()));

                int rating;
                if (r.getCuisineType().equals(favoriteCuisine)) {
                    rating = random.nextInt(2) + 4;
                } else {
                    rating = random.nextInt(5) + 1;
                }

                interactions.add(Interaction.builder()
                        .userId(user.getUserId())
                        .restaurantId(r.getRestaurantId())
                        .rating(rating)
                        .build()
                );
            }
        }
        interactionRepository.saveAll(interactions);
        log.info("Seeding completed: {} interactions created.", interactions.size());
    }
}

