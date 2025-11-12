package rs.raf.demo.repositories.events;


import rs.raf.demo.entities.Event;
import rs.raf.demo.entities.dto.EventDto;

import java.util.List;

public interface EventRepository {

    public List<EventDto> getHomeEvents(int page, int size);
    public List<EventDto> getMostPopularEvents();
    public List<EventDto> getEventsByCategory(String category);
    public EventDto getEventById(int id);
    public void likeEvent(int eventId);
    public void dislikeEvent(int eventId);
    public void viewEvent(int eventId);
    public List<EventDto> getEventsByTag(String tag);
    public List<EventDto> getEventsByTag3(String tag);

    public List<EventDto> getMostReactionsEvents();
    public List<EventDto> getAllEvents();
    public void deleteEvent(int eventId);
    public void editEvent(EventDto eventDto);
    public void addNewEvent(EventDto eventDto);
    public int totalEvents();

}
