package org.learnteachcode.seoul.attendance.tracker.frontend;

import org.learnteachcode.seoul.attendance.tracker.api.event.EventRepository;
import org.springframework.stereotype.Controller;

@Controller
public class EventWebController {

    private EventRepository eventRepository;

    public EventWebController(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    /*@GetMapping(path = "/events/select")
    public String selectEvent(Model model) {
        List<Event> eventList = eventRepository.findAll();
        model.addAttribute("eventList", eventList);
        return "events/select";
    }*/
}
