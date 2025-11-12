package rs.raf.demo;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.jackson.internal.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.ServerProperties;
import rs.raf.demo.filters.AuthFilter;
import rs.raf.demo.repositories.category.CategoryRepository;
import rs.raf.demo.repositories.category.CategoryRepositoryImpl;
import rs.raf.demo.repositories.comments.CommentsRepository;
import rs.raf.demo.repositories.comments.CommentsRepositoryImpl;
import rs.raf.demo.repositories.events.EventRepository;
import rs.raf.demo.repositories.events.EventRepositoryImpl;
import rs.raf.demo.repositories.rsvp.RsvpRepository;
import rs.raf.demo.repositories.rsvp.RsvpRepositoryImpl;
import rs.raf.demo.repositories.tags.TagRepository;
import rs.raf.demo.repositories.tags.TagRepositoryImpl;
import rs.raf.demo.repositories.user.UserRepository;
import rs.raf.demo.repositories.user.UserRepositoryImpl;
import rs.raf.demo.services.*;

import javax.inject.Singleton;
import javax.ws.rs.ApplicationPath;

@ApplicationPath("/api")
public class HelloApplication extends ResourceConfig {

    public HelloApplication() {
        // Uključujemo validaciju
        property(ServerProperties.BV_SEND_ERROR_IN_RESPONSE, true);

        // Registracija servisa i repozitorijuma u DI container-u
        AbstractBinder binder = new AbstractBinder() {
            @Override
            protected void configure() {
                // Eventi
                bind(EventRepositoryImpl.class).to(EventRepository.class).in(Singleton.class);
                bindAsContract(EventsService.class);

                // Kategorije
                bind(CategoryRepositoryImpl.class).to(CategoryRepository.class).in(Singleton.class);
                bindAsContract(CategoryService.class);

                // Komentari
                bind(CommentsRepositoryImpl.class).to(CommentsRepository.class).in(Singleton.class);
                bindAsContract(CommentsService.class);

                // Korisnici
                bind(UserRepositoryImpl.class).to(UserRepository.class).in(Singleton.class);
                bindAsContract(UserService.class);

                // Tagovi
                bind(TagRepositoryImpl.class).to(TagRepository.class).in(Singleton.class);

                //RSVP
                bind(RsvpRepositoryImpl.class).to(RsvpRepository.class).in(Singleton.class);
                bindAsContract(RsvpService.class);
            }
        };
        register(binder);

        // Registracija filtera
        register(AuthFilter.class);
        register(rs.raf.demo.filters.CORSFilter.class);

        // Registracija Jackson provider-a sa podrškom za java.time
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());

        JacksonJaxbJsonProvider provider = new JacksonJaxbJsonProvider();
        provider.setMapper(mapper);
        register(provider);

        // Registracija svih resursa u package-u rs.raf.demo.resources
        packages("rs.raf.demo.resources");
    }
}
