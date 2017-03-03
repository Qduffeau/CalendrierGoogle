import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.client.util.DateTime;

import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;


public class Calendrier {
	
	com.google.api.services.calendar.Calendar service;
	String calendarId;

	public Calendrier(String calendarId) throws IOException{
		service = Calendrier.getCalendarService();
		this.calendarId = calendarId;
	}
	
    /** Application name. */
    private static final String APPLICATION_NAME =
        "Google Calendar API Java Quickstart";

    /** Directory to store user credentials for this application. */
    private static final java.io.File DATA_STORE_DIR = new java.io.File(
        System.getProperty("user.home"), ".credentials/calendar-java-quickstart");

    /** Global instance of the {@link FileDataStoreFactory}. */
    private static FileDataStoreFactory DATA_STORE_FACTORY;

    /** Global instance of the JSON factory. */
    private static final JsonFactory JSON_FACTORY =
        JacksonFactory.getDefaultInstance();

    /** Global instance of the HTTP transport. */
    private static HttpTransport HTTP_TRANSPORT;

    /** Global instance of the scopes required by this quickstart.
     *
     * If modifying these scopes, delete your previously saved credentials
     * at ~/.credentials/calendar-java-quickstart
     */
    private static final List<String> SCOPES =
        Arrays.asList(CalendarScopes.CALENDAR);

    static {
        try {
            HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            DATA_STORE_FACTORY = new FileDataStoreFactory(DATA_STORE_DIR);
        } catch (Throwable t) {
            t.printStackTrace();
            System.exit(1);
        }
    }
	
    /**
     * Creates an authorized Credential object.
     * @return an authorized Credential object.
     * @throws IOException
     */
    public static Credential authorize() throws IOException {
        // Load client secrets.
        InputStream in =
            Calendrier.class.getResourceAsStream("/client_secret.json");
        GoogleClientSecrets clientSecrets =
            GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow =
                new GoogleAuthorizationCodeFlow.Builder(
                        HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(DATA_STORE_FACTORY)
                .setAccessType("offline")
                .build();
        Credential credential = new AuthorizationCodeInstalledApp(
            flow, new LocalServerReceiver()).authorize("user");
        System.out.println(
                "Credentials saved to " + DATA_STORE_DIR.getAbsolutePath());
        return credential;
    }

    /**
     * Build and return an authorized Calendar client service.
     * @return an authorized Calendar client service
     * @throws IOException
     */
    public static com.google.api.services.calendar.Calendar
        getCalendarService() throws IOException {
        Credential credential = authorize();
        return new com.google.api.services.calendar.Calendar.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }
    
    public List<Event> getEvents(com.google.api.services.calendar.Calendar service) throws IOException{
    	
    	// List the next 10 events from the primary calendar.
        DateTime now = new DateTime(System.currentTimeMillis());
        Events events = service.events().list(calendarId)
            .setMaxResults(10)
            .setTimeMin(now)
            .setOrderBy("startTime")
            .setSingleEvents(true)
            .execute();
        List<Event> items = events.getItems();
        System.out.println("Liste des évènements récupérée");
        return items;
    }
    
    public Event getSingleEvent (String name, String dateDebut) throws IOException{
    	List<Event> listEvent = this.getEvents(service);
    	DateTime start = new DateTime(dateDebut);
    	for(Event event : listEvent){
    		if(event.getSummary().equals(name)&&event.getStart().getDateTime().equals(start)){
    			return event;
    		}   		
    	}
    	return null;
    }
    
    /////////////////////////// AJOUT D'UN EVENEMENT ///////////////////////////
    // Pour l'evenement, debut et fin sont de la forme "AAAA-MM-DDTHH:MinMin:SS.000+01:00"
    // ex: "2000-12-31T23:59:59"
    
    public void addEvent(String titre, String description, String debut, String fin) throws IOException{
    	Event event = new Event()
    			.setSummary(titre)
    			.setDescription(description);    	
    	DateTime start = new DateTime(debut);
    	EventDateTime startEvent = new EventDateTime()
    		    .setDateTime(start)
    		    .setTimeZone("Europe/Paris");
    	event.setStart(startEvent);	    	
    	DateTime end = new DateTime(fin);
    	EventDateTime endEvent = new EventDateTime()
    		    .setDateTime(end)
	    		.setTimeZone("Europe/Paris");
    	event.setEnd(endEvent);    	
    	event = service.events().insert(calendarId, event).execute();
    	System.out.printf("Event created: %s\n", event.getHtmlLink());    	
    }
    
    public void addEvent(String titre, String description, String debut, String fin, EventAttendee[] invites) throws IOException{
    	Event event = new Event()
    			.setSummary(titre)
    			.setDescription(description);    	
    	DateTime start = new DateTime(debut);
    	EventDateTime startEvent = new EventDateTime()
    		    .setDateTime(start)
    		    .setTimeZone("Europe/Paris");
    	event.setStart(startEvent);    	
    	DateTime end = new DateTime(fin);
    	EventDateTime endEvent = new EventDateTime()
    		    .setDateTime(end)
	    		.setTimeZone("Europe/Paris");
    	event.setEnd(endEvent);    	
    	event.setAttendees(Arrays.asList(invites));    	
    	event = service.events().insert(calendarId, event).execute();
    	System.out.printf("Event created: %s\n", event.getHtmlLink());    	
    }
    
    public void addEvent(String titre, String description, String debut, String fin, EventAttendee[] invites, String[] recurrence) throws IOException{
    	Event event = new Event()
    			.setSummary(titre)
    			.setDescription(description);    	
    	DateTime start = new DateTime(debut);
    	EventDateTime startEvent = new EventDateTime()
    		    .setDateTime(start)
    		    .setTimeZone("Europe/Paris");
    	event.setStart(startEvent);    	
    	DateTime end = new DateTime(fin);
    	EventDateTime endEvent = new EventDateTime()
    		    .setDateTime(end)
	    		.setTimeZone("Europe/Paris");
    	event.setEnd(endEvent);    	
    	event.setAttendees(Arrays.asList(invites));	    	
    	event.setRecurrence(Arrays.asList(recurrence));    	
    	event = service.events().insert(calendarId, event).execute();
    	System.out.printf("Event created: %s\n", event.getHtmlLink());
    }
    
    public void addEvent(String titre, String description, String debut, String fin, String[] recurrence) throws IOException{
    	Event event = new Event()
    			.setSummary(titre)
    			.setDescription(description);
    	DateTime start = new DateTime(debut);
    	EventDateTime startEvent = new EventDateTime()
    		    .setDateTime(start)
    		    .setTimeZone("Europe/Paris");
    	event.setStart(startEvent);
    	DateTime end = new DateTime(fin);
    	EventDateTime endEvent = new EventDateTime()
    		    .setDateTime(end)
	    		.setTimeZone("Europe/Paris");
    	event.setEnd(endEvent);
    	event.setRecurrence(Arrays.asList(recurrence));
    	event = service.events().insert(calendarId, event).execute();
    	System.out.printf("Event created: %s\n", event.getHtmlLink());    	
    }
    
    /////////////////////////// SUPPRESSION D'UN EVENEMENT ///////////////////////////   
    
    // Pour supprimer un seul évènement, choisir event.getId().
    // Pour supprimer un évènement récursif, choisir event.getRecurringEventId() dans le main.
    
    public void deleteEvent(String eventId) throws IOException{
    	service.events().delete(calendarId, eventId).execute();
    	System.out.println("Evènement supprimé");
    }
    
    
    /////////////////////////// MODIFICATION D'UN EVENEMENT ///////////////////////////
    
    public void updateSummary(Event event, String toUpdate) throws IOException{
    	event.setSummary(toUpdate);
    	System.out.println("Titre changé");
    }
    
    public void updateStartDate(Event event, String newDate) throws IOException{
    	DateTime start = new DateTime(newDate);
    	EventDateTime startEvent = new EventDateTime()
    		    .setDateTime(start)
    		    .setTimeZone("Europe/Paris");
    	event.setStart(startEvent);
    	System.out.println("Date de début modifiée");
    }
    
    public void updateStartDate(Event event, DateTime newDate) throws IOException{
    	EventDateTime startEvent = new EventDateTime()
    		    .setDateTime(newDate)
    		    .setTimeZone("Europe/Paris");
    	event.setStart(startEvent);
    	System.out.println("Date de début modifiée");
    }
    
    public void updateEndDate(Event event, String newDate) throws IOException{
    	DateTime end = new DateTime(newDate);
    	EventDateTime endEvent = new EventDateTime()
    		    .setDateTime(end)
    		    .setTimeZone("Europe/Paris");
    	event.setEnd(endEvent);
    	System.out.println("Date de fin modifiée");
    }
    
    public void updateEndDate(Event event, DateTime newDate) throws IOException{
    	EventDateTime endEvent = new EventDateTime()
    		    .setDateTime(newDate)
    		    .setTimeZone("Europe/Paris");
    	event.setEnd(endEvent);
    	System.out.println("Date de fin modifiée");
    }
    
    public void updateEventDate(Event event, String newDate) throws IOException{
    	long eventDuration = event.getEnd().getDateTime().getValue() - event.getStart().getDateTime().getValue();
    	System.out.println("Durée de l'évènement : "+eventDuration/60000);
    	// La date et l'heure calculées ici sont sur le faiseau GMT,
    	// En retirant 3600000 ms soit une heure, on convertit cette heure pour le fuseau GMT+1
    	DateTime newEndDate = new DateTime(eventDuration + event.getEnd().getDateTime().getValue()-3600000);
    	System.out.println(newEndDate.toString());
    	updateStartDate(event, newDate);
    	updateEndDate(event, newEndDate);
    	System.out.println("Evènement décalé");
    }
    
    public void updateEventDate(Event event, DateTime newDate) throws IOException{
    	long eventDuration = event.getEnd().getDateTime().getValue() - event.getStart().getDateTime().getValue();
    	System.out.println("Durée de l'évènement : "+eventDuration/60000);
    	DateTime newEndDate = new DateTime(eventDuration + event.getEnd().getDateTime().getValue()-3600000);
    	System.out.println(newEndDate.toString());
    	updateStartDate(event, newDate);
    	updateEndDate(event, newEndDate);
    	System.out.println("Evènement décalé");
    }
    
    public void updateDescription(Event event, String toUpdate) throws IOException{
    	event.setDescription(toUpdate);
    	System.out.println("Description modifiée");
    }
    
    // Cette fonction envoie les modifications faites
    // pour qu'elles apparaissent dans le calendrier
    public void endUpdate(Event event) throws IOException{
    	service.events().update(calendarId, event.getId(), event).execute();
    	System.out.println("Toutes les modifications ont été enregistrées");
    }
    
}
