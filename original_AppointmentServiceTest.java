import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for AppointmentService.
 * Tests cover add, duplicate add, null input, delete semantics, retrieval,
 * unmodifiable map view, and defensive copy behavior related to Date.
 */
public class AppointmentServiceTest {
    private AppointmentService service;
    private Appointment appA;
    private Appointment appB;

    @BeforeEach
    public void setUp() {
        service = new AppointmentService();
        Date future = new Date(System.currentTimeMillis() + 200000);
        appA = new Appointment("aA", future, "Appointment A");
        appB = new Appointment("aB", future, "Appointment B");
    }

    // addAppointment should return true and the appointment should be retrievable
    @Test
    public void testAddAppointmentSuccess() {
        assertTrue(service.addAppointment(appA));
        assertEquals(appA, service.getAppointment("aA"));
    }

    // Service should reject null input to addAppointment
    @Test
    public void testAddNullThrows() {
        assertThrows(IllegalArgumentException.class, () -> service.addAppointment(null));
    }

    // Adding two appointments with same id should throw and preserve the original
    @Test
    public void testAddDuplicateThrows() {
        assertTrue(service.addAppointment(appA));
        Appointment dup = new Appointment("aA", new Date(System.currentTimeMillis() + 300000), "Dup");
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> service.addAppointment(dup));
        assertEquals("Duplicate ID", ex.getMessage());
    }

    // deleteAppointment returns true when present, false when missing
    @Test
    public void testDeleteAppointment() {
        service.addAppointment(appA);
        assertTrue(service.deleteAppointment("aA"));
        assertNull(service.getAppointment("aA"));
        assertFalse(service.deleteAppointment("nonexistent"));
    }

    // Deleting the same id twice returns true then false
    @Test
    public void testDeleteIdempotent() {
        service.addAppointment(appA);
        assertTrue(service.deleteAppointment("aA"));
        assertFalse(service.deleteAppointment("aA"));
    }

    // getAllAppointments should reflect all stored entries
    @Test
    public void testAddMultipleAndGetAll() {
        service.addAppointment(appA);
        service.addAppointment(appB);
        Map<String, Appointment> all = service.getAllAppointments();
        assertEquals(2, all.size());
        assertTrue(all.containsKey("aA"));
        assertTrue(all.containsKey("aB"));
    }

    // The returned map from getAllAppointments must be unmodifiable
    @Test
    public void testGetAllIsUnmodifiable() {
        service.addAppointment(appA);
        Map<String, Appointment> all = service.getAllAppointments();
        assertThrows(UnsupportedOperationException.class, () -> all.put("x", appB));
    }

    // getAppointment should return null for unknown IDs
    @Test
    public void testGetAppointmentReturnsNullWhenMissing() {
        assertNull(service.getAppointment("no-such-id"));
    }

    // The service should store the same instance that was added, not a copy
    @Test
    public void testStoredAppointmentIsSameInstance() {
        service.addAppointment(appA);
        Appointment stored = service.getAppointment("aA");
        // appointment is immutable in fields, equality check verifies storage
        assertSame(appA, stored);
    }

    // Mutating the Date object passed to the constructor must not affect the stored appointment date
    @Test
    public void testAddingAppointmentThenMutatingOriginalDateDoesNotAffectStored() {
        Date original = new Date(System.currentTimeMillis() + 250000);
        Appointment created = new Appointment("mut01", original, "Mutable test");
        service.addAppointment(created);
        // mutate original date object used to construct the appointment
        original.setTime(0L);
        // stored appointment date should remain the same as it was at creation
        assertNotEquals(0L, service.getAppointment("mut01").getAppointmentDate().getTime());
    }

    // Duplicate check that unknown id returns null; included for clarity
    @Test
    public void testGetAppointmentReturnsNullWhenMissing_IdNotFound() {
        assertNull(service.getAppointment("missing"));
    }

}
