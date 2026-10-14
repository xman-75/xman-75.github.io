import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * In-memory appointment service.
 * addAppointment: throws on null input or duplicate ID, returns true on success
 * deleteAppointment: removes by ID and returns true when removed
 * getAppointment: returns Appointment or null when missing
 * getAllAppointments: returns an unmodifiable view for test convenience
 * getAppointmentsInRange: returns appointments with a date between start and end, inclusive
 *
 * Appointments are indexed two ways: a HashMap by ID for O(1) exact lookups, and a TreeMap
 * by date so a range query doesn't require scanning every appointment. Without the TreeMap,
 * a range query is O(n); with it, subMap makes it O(log n + k).
 *
 * This implementation uses a HashMap and TreeMap which is fine for the assignment. For concurrency,
 * consider ConcurrentHashMap / ConcurrentSkipListMap in the future.
 */
public class AppointmentService {
    private final Map<String, Appointment> idIndex = new HashMap<>();
    private final TreeMap<Date, List<Appointment>> dateIndex = new TreeMap<>();

    /**
     * Adds the given appointment if the ID does not already exist.
     * Returns true on success.
     * Throws IllegalArgumentException for null appointment or duplicate ID.
     * Also indexes the appointment by date for range queries.
     */
    public boolean addAppointment(Appointment appointment) {
        if (appointment == null) {
            throw new IllegalArgumentException("Appointment cannot be null");
        }
        String id = appointment.getAppointmentId();
        if (id == null) {
            throw new IllegalArgumentException("Appointment ID cannot be null");
        }
        if (idIndex.containsKey(id)) {
            throw new IllegalArgumentException("Duplicate ID");
        }

        idIndex.put(id, appointment);

        Date date = appointment.getAppointmentDate();
        dateIndex.computeIfAbsent(date, d -> new ArrayList<>()).add(appointment);

        return true;
    }

    /**
     * Deletes the appointment with the given ID.
     * Returns true if an appointment was removed, false otherwise.
     * Also removes it from the date index, clearing the date's entry once empty.
     */
    public boolean deleteAppointment(String appointmentId) {
        Appointment removed = idIndex.remove(appointmentId);
        if (removed == null) {
            return false;
        }

        Date date = removed.getAppointmentDate();
        List<Appointment> onDate = dateIndex.get(date);
        if (onDate != null) {
            onDate.remove(removed);
            if (onDate.isEmpty()) {
                dateIndex.remove(date);
            }
        }

        return true;
    }

    /**
     * Returns the Appointment for the given ID, or null if not found.
     */
    public Appointment getAppointment(String appointmentId) {
        return idIndex.get(appointmentId);
    }

    /**
     * Unmodifiable view for tests and debugging.
     */
    public Map<String, Appointment> getAllAppointments() {
        return Collections.unmodifiableMap(idIndex);
    }

    /**
     * Returns appointments with a date between start and end, inclusive.
     * Throws IllegalArgumentException for a null bound or start after end.
     * Uses TreeMap#subMap instead of scanning every appointment.
     */
    public List<Appointment> getAppointmentsInRange(Date start, Date end) {
        if (start == null || end == null) {
            throw new IllegalArgumentException("Start and end dates cannot be null");
        }
        if (start.after(end)) {
            throw new IllegalArgumentException("Start date cannot be after end date");
        }

        List<Appointment> results = new ArrayList<>();
        for (List<Appointment> onDate : dateIndex.subMap(start, true, end, true).values()) {
            results.addAll(onDate);
        }
        return results;
    }
}