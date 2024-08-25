package com.subash.api.service;

import java.util.List;

import com.subash.api.model.Appointment;
import com.subash.api.model.CarePlan;
import com.subash.api.model.EHR;
import com.subash.api.model.PsychiatristAvailability;
import com.subash.api.model.PsychiatristLogin;

public interface PsychiatristService {

	public String addJewellery(PsychiatristLogin psychiatristLogin);
	
	public String addCarePlan(CarePlan carePlan);
	
	public String addPsychiatristAvailability(PsychiatristAvailability psychiatristAvailability);

	public PsychiatristLogin getJewellery(int id);

	public List<PsychiatristLogin> getAllJewellery();
	
	public List<Appointment> getAllAppointment(int id);

	public void updateJewellery(PsychiatristLogin psychiatristLogin);

	public void deleteJewellery(int id);
	
	public EHR saveEHR(EHR ehr);
	
	
	public PsychiatristAvailability getSlotByIdAndDate(String availableDate,String psychiaId);
}
