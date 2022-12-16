package com.sample.hotel.app;

import com.sample.hotel.entity.Booking;
import com.sample.hotel.entity.Room;
import com.sample.hotel.entity.RoomReservation;
import io.jmix.core.DataManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDate;
import java.util.List;

@Component
public class BookingService {

    @PersistenceContext
    private EntityManager entityManager;
    @Autowired
    private DataManager dataManager;

    /**
     * Check if given room is suitable for the booking.
     * 1) Check that sleeping places is enough to fit numberOfGuests.
     * 2) Check that there are no reservations for this room at the same range of dates.
     * Use javax.persistence.EntityManager and JPQL query for querying database.
     *
     * @param booking booking
     * @param room room
     * @return true if checks are passed successfully
     */
    @Transactional
    public boolean isSuitable(Booking booking, Room room) {
        if (booking.getNumberOfGuests() > room.getSleepingPlaces()) {
            return false;
        }
        List<RoomReservation> reservations = entityManager
                .createQuery("select r from RoomReservation r where r.room = :room")
                .setParameter("room", room)
                .getResultList();
        LocalDate arrivalBookingDate = booking.getArrivalDate();
        LocalDate departureBookingDate = booking.getDepartureDate();
        for (RoomReservation reservation : reservations) {
            LocalDate arrivalReserveDate = reservation.getBooking().getArrivalDate().plusDays(1);
            LocalDate departureReserveDate = reservation.getBooking().getDepartureDate();
            List<LocalDate> datesReserve = arrivalReserveDate.datesUntil(departureReserveDate).toList();
            if (datesReserve.contains(arrivalBookingDate) || datesReserve.contains(departureBookingDate)){
                return false;
            }
        }
        return true;
    }

    /**
     * Check that room is suitable for the booking, and create a reservation for this room.
     * @param room room to reserve
     * @param booking hotel booking
     * Wrap operation into a transaction (declarative or manual).
     *
     * @return created reservation object, or null if room is not suitable
     */
    @Transactional
    public RoomReservation reserveRoom(Booking booking, Room room) {
        if (isSuitable(booking, room)) {
            RoomReservation roomReservation = dataManager.create(RoomReservation.class);
            roomReservation.setBooking(booking);
            roomReservation.setRoom(room);
            dataManager.save(roomReservation);
            return roomReservation;
        }
        return null;
    }
}