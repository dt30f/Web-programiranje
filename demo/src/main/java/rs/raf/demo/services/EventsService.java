package rs.raf.demo.services;



import rs.raf.demo.entities.Event;
import rs.raf.demo.entities.dto.EventDto;
import rs.raf.demo.repositories.events.EventRepository;

import javax.inject.Inject;
import java.util.List;

public class EventsService {
    @Inject
    public EventRepository eventRepository;

    public List<EventDto> getHomeEvents(int page, int size) {return eventRepository.getHomeEvents(page, size);}
    public List<EventDto> getMostPopularEvents() {return eventRepository.getMostPopularEvents();}
    public List<EventDto> getEventsByCategory(String category) {return eventRepository.getEventsByCategory(category);}
    public EventDto getEventById(int eventId) {return eventRepository.getEventById(eventId);}
    public void likeEvent(int eventId) {eventRepository.likeEvent(eventId);}
    public void dislikeEvent(int eventId) {eventRepository.dislikeEvent(eventId);}
    public void viewEventById(int eventId) {eventRepository.viewEvent(eventId);}
    public List<EventDto> getEventsByTag(String tag){return eventRepository.getEventsByTag(tag);}

    public List<EventDto> getEventsByTag3(String tag){return eventRepository.getEventsByTag(tag);}
    public List<EventDto> getMostReactionsEvents(){return eventRepository.getMostReactionsEvents();}
    public List<EventDto> getAllEvents(){return eventRepository.getAllEvents();}
    public void deleteEvent(int id){eventRepository.deleteEvent(id);}
    public void editEvent(EventDto eventDto){eventRepository.editEvent(eventDto);}
    public void addNewEvent(EventDto eventDto){eventRepository.addNewEvent(eventDto);}
    public int totalEvents(){return eventRepository.totalEvents();}


}
