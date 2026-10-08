package com.food_waste_backend.Entity;

import java.time.LocalDateTime;

import org.jspecify.annotations.Nullable;

import com.food_waste_backend.enums.UserRole;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    private String phone;

    @Enumerated(EnumType.STRING)
    private UserRole role;

    @Builder.Default
    private Boolean enabled = true;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();

        if (enabled == null) {
            enabled = true;
        }

        setCreatedAt(now);
        setUpdatedAt(now);
    }

    @PreUpdate
    protected void onUpdate() {
        setUpdatedAt(LocalDateTime.now());
    }

	public Object getEmail() {
		return null;
	}

	public Object getName() {
		return null;
	}

	public Object getRole() {
		return null;
	}

	public Object getId() {
		return null;
	}

	public void setName(String name2) {
		
	}

	public void setEmail(String email2) {
		
	}

	public String getPassword() {
		return null;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}

	public void setPassword(@Nullable String encode) {
		// TODO Auto-generated method stub
		
	}
}
