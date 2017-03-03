import java.io.IOException;
import java.util.List;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventAttendee;

public class Main {

	public static void main(String[] args) throws IOException{
		// TODO Auto-generated method stub
		Calendrier calendrier = new Calendrier("primary");
		List<Event> events = calendrier.getEvents(calendrier.service);

		Event event = new Event();
		event = calendrier.getSingleEvent("Rendez-vous bidon", "2017-03-03T17:00:00.000+01:00");
		
		if(event!=null)
			//calendrier.deleteEvent(event.getRecurringEventId());
			calendrier.updateEventDate(event, "2017-03-03T17:00:00.000+01:00");
			calendrier.updateDescription(event, "Description modifiée");
			calendrier.endUpdate(event);
		
		/*
		EventAttendee[] invites = new EventAttendee[] {
		    new EventAttendee().setEmail("qduffeau@gmail.com"),
		};

		String[] recurrence = new String[] {"RRULE:FREQ=DAILY;COUNT=3"};
		*/
		calendrier.addEvent("Rendez-vous bidon", null, "2017-03-03T16:00:00.000+01:00", "2017-03-03T18:00:00.000+01:00");
		/*calendrier.addEvent("Test recurrence again", "essai de creation d'evenement avec recurrence", "2017-03-02T16:00:00", "2017-03-02T18:00:00", invites, recurrence);
		*/
	}

}
