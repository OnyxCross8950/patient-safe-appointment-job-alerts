package healthtech;

import java.time.Instant;

public final class AppointmentWorkflowTest {
    public static void main(String[] args) {
        AppointmentWorkflow workflow = new AppointmentWorkflow();
        var consented = new AppointmentWorkflow.Appointment("p1", "a1", Instant.EPOCH, true);
        var declined = new AppointmentWorkflow.Appointment("p2", "a2", Instant.EPOCH, false);
        check(workflow.decide(consented, true) == AppointmentWorkflow.Decision.SEND_REMINDER);
        check(workflow.decide(consented, false) == AppointmentWorkflow.Decision.HOLD_FOR_REVIEW);
        check(workflow.decide(declined, true) == AppointmentWorkflow.Decision.HOLD_FOR_REVIEW);
        System.out.println("appointment decision tests passed");
    }

    private static void check(boolean condition) {
        if (!condition) throw new AssertionError("unexpected appointment decision");
    }
}
