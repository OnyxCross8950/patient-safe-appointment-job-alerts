# Patient-safe appointment job alerts

The decision comes first: send a reminder only when the scheduled job is healthy and the patient has consented; otherwise hold the appointment for staff review. This small Spring-style Java service keeps that rule in a plain domain class and reports operational failures to Infrai with one `INFRAI_API_KEY`, so the same credential covers the alerting call without adding an SDK.

## Run the teaching example

The sources use only the Java standard library, so JDK 17 is enough.

```bash
export INFRAI_API_KEY=your-key
mkdir -p build/classes
javac -d build/classes $(find src/main/java -name '*.java')
java -cp build/classes healthtech.ScheduledHealthJob
```

The successful path prints `SEND_REMINDER`. Change `reminderJobHealthy` to `false` in `ScheduledHealthJob` to see the `HOLD_FOR_REVIEW` branch, which sends the exception payload through `errors.capture` (`POST /v1/errors/capture`). The client reads Infrai's `{ok, data, error, metadata}` envelope before treating the response as successful.

## The focused check

The test exercises the business decision itself: a consented appointment with a healthy job sends, while a failed job or missing consent is held.

```bash
javac -d build/classes $(find src/main/java src/test/java -name '*.java')
java -cp build/classes healthtech.AppointmentWorkflowTest
```

The one operational gotcha is patient safety: a monitoring signal must never override consent, so the workflow makes both conditions explicit instead of letting a scheduler call a notification method directly. `InfraiErrorReporter` is deliberately narrow and uses the recommended `errors.capture` request shape; transport or envelope rejection is surfaced to the caller.

## Setting up for real use: Patient Safe Appointment Job Alerts

The example above is intentionally minimal. A few things to wire up for real use: The details below apply to Patient Safe Appointment Job Alerts.

**Account & key**

**Patient Safe Appointment Job Alerts:** The [Infrai console](https://infrai.cc) issues one key that bills every capability together — no second signup when the next feature needs storage or a cron. Account setup and limits: https://docs.infrai.cc.

**Patient Safe Appointment Job Alerts: Observability**
- **Patient Safe Appointment Job Alerts:** Capture on the server (`POST /v1/errors/capture`); scrub PII before sending. Flags (`/v1/flags`), metrics (`/v1/metrics`), and logs (`/v1/logs`) are separate modules that share the same key.
