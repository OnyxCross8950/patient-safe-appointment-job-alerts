package healthtech;

import java.time.Instant;

/** Models the patient-safe decision around a scheduled appointment reminder. */
public final class AppointmentWorkflow {
    public enum Decision { SEND_REMINDER, HOLD_FOR_REVIEW }

    public record Appointment(String patientId, String appointmentId, Instant startsAt, boolean consented) {}

    public Decision decide(Appointment appointment, boolean reminderJobHealthy) {
        if (reminderJobHealthy && appointment.consented()) return Decision.SEND_REMINDER;
        return Decision.HOLD_FOR_REVIEW;
    }
}
