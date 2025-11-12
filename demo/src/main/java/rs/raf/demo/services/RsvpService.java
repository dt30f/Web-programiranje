package rs.raf.demo.services;

import rs.raf.demo.entities.Rsvp;
import rs.raf.demo.repositories.rsvp.RsvpRepository;

import javax.inject.Inject;

public class RsvpService {
    @Inject private RsvpRepository rsvpRepository;
    public void addRsvp(Rsvp rsvp) {rsvpRepository.addRsvp(rsvp);}
    public int getRsvp(int id) {return rsvpRepository.getAllForRsvpId(id);}
}
