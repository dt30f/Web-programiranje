package rs.raf.demo.repositories.rsvp;

import rs.raf.demo.entities.Rsvp;

public interface RsvpRepository {
    public void addRsvp(Rsvp rsvp);
    public int getAllForRsvpId(int rsvpId);
}
