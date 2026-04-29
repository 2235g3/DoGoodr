package com.vidalia.backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "applications", uniqueConstraints = @UniqueConstraint(columnNames = {"volunteer_profile_id", "opportunity_id"}))
public class Application {

	@Id
	@GeneratedValue
	@Column(name = "application_id", columnDefinition = "uuid")
	private UUID id;

	@NotNull(message = "Volunteer profile cannot be null")
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "volunteer_profile_id", nullable = false)
	private VolunteerProfile volunteerProfile;

	@NotNull(message = "Opportunity cannot be null")
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "opportunity_id", nullable = false)
	private Opportunity opportunity;

	@NotNull(message = "Status cannot be null")
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private ApplicationStatus status = ApplicationStatus.APPLIED;

	@NotNull(message = "Date applied cannot be null")
	@Column(name = "date_applied", nullable = false, updatable = false)
	private LocalDateTime dateApplied;

	@Column(name = "decision_date")
	private LocalDateTime decisionDate;

	@PrePersist
	private void onCreate() {
		if (dateApplied == null) {
			dateApplied = LocalDateTime.now();
		}
	}

}
