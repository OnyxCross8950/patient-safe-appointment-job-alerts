package healthtech;

import java.net.http.HttpClient;

/** Runnable example of a scheduled health check with patient-safe notification. */
public final class ScheduledHealthJob {
    public static void main(String[] args) throws Exception {
        String key = System.getenv("INFRAI_API_KEY");
        if (key == null || key.isBlank()) throw new IllegalStateException("Set INFRAI_API_KEY first");
        AppointmentWorkflow workflow = new AppointmentWorkflow();
        AppointmentWorkflow.Appointment appointment = new AppointmentWorkflow.Appointment(
                "patient-42", "appt-2026-08-19", java.time.Instant.parse("2026-08-19T09:00:00Z"), true);
        boolean reminderJobHealthy = true;
        AppointmentWorkflow.Decision decision = workflow.decide(appointment, reminderJobHealthy);
        System.out.println("Appointment " + appointment.appointmentId() + ": " + decision);
        if (decision == AppointmentWorkflow.Decision.HOLD_FOR_REVIEW) {
            new InfraiErrorReporter(HttpClient.newHttpClient(), key)
                    .capture("appointment-reminder", new IllegalStateException("reminder held for review"));
        }
    }
}
