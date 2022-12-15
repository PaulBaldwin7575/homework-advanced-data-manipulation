package com.sample.hotel.listener;

import com.sample.hotel.entity.Booking;
import io.jmix.core.event.EntityChangedEvent;
import io.jmix.core.event.EntitySavingEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class BookingEventListener {

    @EventListener
    public void onBookingSaving(EntitySavingEvent<Booking> event) {
        Booking booking = event.getEntity();
        booking.setDepartureDate(booking.getArrivalDate().plusDays(booking.getNightsOfStay()));
    }
}