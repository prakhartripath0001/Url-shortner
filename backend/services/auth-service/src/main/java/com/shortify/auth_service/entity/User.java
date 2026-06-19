package com.shortify.auth_service.entity;

import com.prakhar.authservice.enums.Role;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role = Role.USER;

    @Column(name = "is_enabled", nullable = false)
    private boolean isEnabled = false;

    @Column(name = "is_locked", nullable = false)
    private boolean isLocked = false;

    @Column(name = "created_at", updatable = false, insertable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", updatable = false, insertable = false)
    private LocalDateTime updatedAt;

    // --- Core URL Shortener Extensions (Optional but Recommended) ---
    // If you plan to add the URL entity into this specific microservice later,
    // you would uncomment the relationship below. For a clean distributed architecture,
    // keeping them decoupled via user_id is often cleaner, but here is the JPA link:

    /*
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ShortenedUrl> urls = new ArrayList<>();
    */
}