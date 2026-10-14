import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import java.util.Date;
import java.util.List;
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

    // getAppointmentsInRange should return only appointments whose date falls
    // within the given window, inclusive of both endpoints.
    @Test
    public void testGetAppointmentsInRangeReturnsMatchingAppointments() {
        long now = System.currentTimeMillis();
        Date inRange1 = new Date(now + 100000);
        Date inRange2 = new Date(now + 200000);
        Date outOfRange = new Date(now + 900000);

        Appointment first = new Appointment("r1", inRange1, "In range 1");
        Appointment second = new Appointment("r2", inRange2, "In range 2");
        Appointment third = new Appointment("r3", outOfRange, "Out of range");
        service.addAppointment(first);
        service.addAppointment(second);
        service.addAppointment(third);

        List<Appointment> results = service.getAppointmentsInRange(inRange1, inRange2);
        assertEquals(2, results.size());
        assertTrue(results.contains(first));
        assertTrue(results.contains(second));
        assertFalse(results.contains(third));
    }

    // A range with no stored appointments in it should return an empty list, not null.
    @Test
    public void testGetAppointmentsInRangeWithNoMatchesReturnsEmptyList() {
        service.addAppointment(appA);
        long now = System.currentTimeMillis();
        Date farStart = new Date(now + 5000000);
        Date farEnd = new Date(now + 6000000);

        List<Appointment> results = service.getAppointmentsInRange(farStart, farEnd);
        assertNotNull(results);
        assertTrue(results.isEmpty());
    }

    // Calling getAppointmentsInRange on an empty service should return an empty list.
    @Test
    public void testGetAppointmentsInRangeOnEmptyServiceReturnsEmptyList() {
        long now = System.currentTimeMillis();
        Date start = new Date(now + 100000);
        Date end = new Date(now + 200000);

        List<Appointment> results = service.getAppointmentsInRange(start, end);
        assertNotNull(results);
        assertTrue(results.isEmpty());
    }

    // The endpoints of the range are inclusive, so an appointment scheduled exactly
    // on the start or end date should still be included.
    @Test
    public void testGetAppointmentsInRangeIsInclusiveOfEndpoints() {
        Date boundary = new Date(System.currentTimeMillis() + 100000);
        Appointment onBoundary = new Appointment("b1", boundary, "On the boundary");
        service.addAppointment(onBoundary);

        List<Appointment> results = service.getAppointmentsInRange(boundary, boundary);
        assertEquals(1, results.size());
        assertTrue(results.contains(onBoundary));
    }

    // Null start or end should be rejected rather than silently treated as unbounded.
    @Test
    public void testGetAppointmentsInRangeNullBoundsThrows() {
        Date someDate = new Date(System.currentTimeMillis() + 100000);
        assertThrows(IllegalArgumentException.class, () -> service.getAppointmentsInRange(null, someDate));
        assertThrows(IllegalArgumentException.class, () -> service.getAppointmentsInRange(someDate, null));
    }

    // A start date after the end date is an invalid range and should throw.
    @Test
    public void testGetAppointmentsInRangeStartAfterEndThrows() {
        long now = System.currentTimeMillis();
        Date start = new Date(now + 200000);
        Date end = new Date(now + 100000);
        assertThrows(IllegalArgumentException.class, () -> service.getAppointmentsInRange(start, end));
    }

    // Two appointments sharing the exact same date should both be returned by a
    // range query that covers that date, confirming the date index groups by date
    // rather than only keeping one appointment per date.
    @Test
    public void testGetAppointmentsInRangeGroupsAppointmentsOnSameDate() {
        Date sharedDate = new Date(System.currentTimeMillis() + 150000);
        Appointment first = new Appointment("s1", sharedDate, "First on this date");
        Appointment second = new Appointment("s2", sharedDate, "Second on this date");
        service.addAppointment(first);
        service.addAppointment(second);

        List<Appointment> results = service.getAppointmentsInRange(sharedDate, sharedDate);
        assertEquals(2, results.size());
        assertTrue(results.contains(first));
        assertTrue(results.contains(second));
    }

    // After deleting an appointment, it should no longer show up in a range query
    // that covers its date, confirming the date index stays in sync with the ID index.
    @Test
    public void testDeletedAppointmentExcludedFromRangeQuery() {
        Date date = new Date(System.currentTimeMillis() + 100000);
        Appointment toDelete = new Appointment("d1", date, "Will be deleted");
        service.addAppointment(toDelete);

        assertTrue(service.deleteAppointment("d1"));

        List<Appointment> results = service.getAppointmentsInRange(date, date);
        assertTrue(results.isEmpty());
    }

}