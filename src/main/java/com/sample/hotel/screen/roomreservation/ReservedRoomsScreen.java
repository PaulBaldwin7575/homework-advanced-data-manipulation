package com.sample.hotel.screen.roomreservation;

import com.sample.hotel.entity.Client;
import com.sample.hotel.entity.RoomReservation;
import io.jmix.core.DataManager;
import io.jmix.ui.Dialogs;
import io.jmix.ui.action.Action;
import io.jmix.ui.component.GroupTable;
import io.jmix.ui.screen.*;
import org.springframework.beans.factory.annotation.Autowired;

@UiController("ReservedRooms")
@UiDescriptor("reserved-rooms.xml")
@LookupComponent("roomReservationsTable")
public class ReservedRoomsScreen extends StandardLookup<RoomReservation> {
    @Autowired
    private GroupTable<RoomReservation> roomReservationsTable;
    @Autowired
    private Dialogs dialogs;
    @Autowired
    private DataManager dataManager;

    @Subscribe("roomReservationsTable.viewClientEmail")
    public void onRoomReservationsTableViewClientEmail(Action.ActionPerformedEvent event) {
        RoomReservation reservation = roomReservationsTable.getSingleSelected();
        if (reservation == null) {
            return;
        }
        Client client = reservation.getBooking().getClient();
        Client currentClient = dataManager.load(Client.class)
                .query("select c from Client c where c.id = :id")
                .parameter("id", client.getId())
                .one();
        dialogs.createMessageDialog()
                .withCaption("Client email")
                .withMessage(currentClient.getEmail())
                .show();
    }
}