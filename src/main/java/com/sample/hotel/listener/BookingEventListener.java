package com.sample.hotel.listener;

import com.sample.hotel.entity.Booking;
import com.sample.hotel.entity.RoomReservation;
import io.jmix.core.DataManager;
import io.jmix.core.event.EntityChangedEvent;
import io.jmix.core.event.EntitySavingEvent;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Component
public class BookingEventListener {

    private static final Logger log = org.slf4j.LoggerFactory.getLogger(BookingEventListener.class);
    @Autowired
    private DataManager dataManager;

    @EventListener
    public void onBookingSaving(EntitySavingEvent<Booking> event) {
        Booking booking = event.getEntity();
        booking.setDepartureDate(booking.getArrivalDate().plusDays(booking.getNightsOfStay()));
    }

    @EventListener
    public void onBookingChangedAfterCommit(EntityChangedEvent<Booking> event) {
            try {
                if (event.getType() == EntityChangedEvent.Type.UPDATED
                        && event.getChanges().isChanged("status")) {
                    Booking booking = dataManager.load(event.getEntityId()).one();
                    RoomReservation reservation = booking.getRoomReservation();
                    dataManager.remove(reservation);
                }
            } catch (Exception e) {
                log.error("Error handling Booking changes after commit", e);
            }
    }
}