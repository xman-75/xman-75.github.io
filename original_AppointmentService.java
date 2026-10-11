import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * In-memory appointment service.
 * addAppointment: throws on null input or duplicate ID, returns true on success
 * deleteAppointment: returns true if removed, false otherwise
 * getAppointment: returns stored appointment or null
 * getAllAppointments: returns an unmodifiable view for test convenience
 *
 * This implementation uses a HashMap which is fine for the assignment. For concurrency,
 * consider ConcurrentHashMap and putIfAbsent in the future.
 */
public class AppointmentService {
    private final Map<String, Appointment> appointments = new HashMap<>();

    /**
     * Adds the given appointment if the ID does not already exist.
     * Returns true on success.
     * Throws IllegalArgumentException for null appointment or duplicate ID.
     */
    public boolean addAppointment(Appointment appointment) {
        if (appointment == null) {
            throw new IllegalArgumentException("Appointment cannot be null");
        }
        String id = appointment.getAppointmentId();
        if (id == null) {
            throw new IllegalArgumentException("Appointment ID cannot be null");
        }
        if (appointments.containsKey(id)) {
            throw new IllegalArgumentException("Duplicate ID");
        }
        appointments.put(id, appointment);
        return true;
    }

    /**
     * Deletes the appointment with the given ID.
     * Returns true if an appointment was removed, false otherwise.
     */
    public boolean deleteAppointment(String appointmentId) {
        return appointments.remove(appointmentId) != null;
    }

    /**
     * Returns the Appointment for the given ID, or null if not found.
     */
    public Appointment getAppointment(String appointmentId) {
        return appointments.get(appointmentId);
    }

    /**
     * Unmodifiable view for tests and debugging.
     */
    public Map<String, Appointment> getAllAppointments() {
        return Collections.unmodifiableMap(appointments);
    }
}
