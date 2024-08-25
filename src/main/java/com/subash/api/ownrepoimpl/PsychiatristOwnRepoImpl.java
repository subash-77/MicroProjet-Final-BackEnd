package com.subash.api.ownrepoimpl;

import java.util.List;
import org.springframework.stereotype.Repository;

import com.subash.api.model.Appointment;
import com.subash.api.model.CarePlan;
import com.subash.api.model.EHR;
import com.subash.api.model.PsychiatristAvailability;
import com.subash.api.model.PsychiatristLogin;
import com.subash.api.model.User;
import com.subash.api.ownrepo.PsychiatristOwnRepo;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;

@Repository
@Transactional
public class PsychiatristOwnRepoImpl implements PsychiatristOwnRepo {

	EntityManager entityManager;

	public PsychiatristOwnRepoImpl(EntityManager entityManager) {
		super();
		this.entityManager = entityManager;
	}

	@Override
	public void save(PsychiatristLogin psychiatristLogin) {
		entityManager.merge(psychiatristLogin);
	}

	@Override
	public PsychiatristLogin findById(int id) {
		return entityManager.find(PsychiatristLogin.class, id);
	}

	@Override
	public List<PsychiatristLogin> findAll() {
		return entityManager.createQuery("from PsychiatristLogin", PsychiatristLogin.class).getResultList();
	}

	@Override
	public void update(PsychiatristLogin psychiatristLogin) {
		entityManager.merge(psychiatristLogin);

		String email = psychiatristLogin.getEmail();
		String password = psychiatristLogin.getPassword();

		User user = entityManager.createQuery("SELECT u FROM User u WHERE u.email = :email", User.class)
				.setParameter("email", email).getSingleResult();

		if (user != null) {
// Update the password
			user.setPassword(password); // Encrypt the password here if needed
			entityManager.merge(user);
		} else {
			throw new RuntimeException("User with email " + email + " not found");
		}

	}

	@Override
	public void deleteById(int id) {
		PsychiatristLogin psychiatristLogin = entityManager.find(PsychiatristLogin.class, id);
		if (psychiatristLogin != null) {
			entityManager.remove(psychiatristLogin);
		}
	}

	@Override
	public List<Appointment> findAllAppointment(int id) {
		return entityManager.createQuery(
				"SELECT a FROM Appointment a WHERE a.psychiatristId = :psychiatristId AND a.status = 'scheduled'",
				Appointment.class).setParameter("psychiatristId", id).getResultList();
	}

	@Override
	public void save(CarePlan carePlan) {
		entityManager.merge(carePlan);

	}

	@Override
	public EHR saveEhr(EHR ehr) {
		return entityManager.merge(ehr);
	}

	@Override
	public void save(PsychiatristAvailability psychiatristAvailability) {
//		entityManager.merge(psychiatristAvailability);
		String availableDate = psychiatristAvailability.getAvailableDate();
		int psychiaId = psychiatristAvailability.getPsychiaId();

		// Check if an entry with the same availableDate and psychiaId exists
		TypedQuery<PsychiatristAvailability> query = entityManager.createQuery(
				"SELECT p FROM PsychiatristAvailability p WHERE p.availableDate = :availableDate AND p.psychiaId = :psychiaId",
				PsychiatristAvailability.class);
		query.setParameter("availableDate", availableDate);
		query.setParameter("psychiaId", psychiaId);

		PsychiatristAvailability existingAvailability = query.getResultStream().findFirst().orElse(null);

		if (existingAvailability != null) {
			// Update existing entry
			existingAvailability.setSlot1(psychiatristAvailability.getSlot1());
			existingAvailability.setSlot2(psychiatristAvailability.getSlot2());
			existingAvailability.setSlot3(psychiatristAvailability.getSlot3());
			entityManager.merge(existingAvailability);
		} else {
			// Save new entry
			entityManager.persist(psychiatristAvailability);
		}
	}

	@Override
	public PsychiatristAvailability findSlotByIdAndDate(String availableDate, String psychiaId) {
		// Define the HQL query
		String hql = "FROM PsychiatristAvailability WHERE availableDate = :availableDate AND psychiaId = :psychiaId";

		// Create a TypedQuery using EntityManager
		TypedQuery<PsychiatristAvailability> query = entityManager.createQuery(hql, PsychiatristAvailability.class);

		// Set query parameters
		query.setParameter("availableDate", availableDate);
		query.setParameter("psychiaId", psychiaId);

		// Execute the query and return the result
		return query.getResultStream().findFirst().orElse(null); // Return null if no result is found
	}

}
