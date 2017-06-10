/*
 * Copyright 2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/*
 * Modified by Learn Teach Code Seoul, Bryan "Beege" Berry
 */
package org.learnteachcode.seoul.attendance.tracker.util;

import org.learnteachcode.seoul.attendance.tracker.api.event.Event;
import org.learnteachcode.seoul.attendance.tracker.api.event.EventRepository;
import org.learnteachcode.seoul.attendance.tracker.api.eventseries.EventSeries;
import org.learnteachcode.seoul.attendance.tracker.api.eventseries.EventSeriesRepository;
import org.learnteachcode.seoul.attendance.tracker.api.organizer.Organizer;
import org.learnteachcode.seoul.attendance.tracker.api.organizer.OrganizerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * @author Greg Turnquist
 */
// tag::code[]
@Component
public class DatabaseLoader implements CommandLineRunner {

    private final EventRepository events;
    private final OrganizerRepository organizers;
    private final EventSeriesRepository eventSeriesRepository;

    @Autowired
    public DatabaseLoader(EventRepository eventRepository,
                          OrganizerRepository organizerRepository,
                          EventSeriesRepository eventSeriesRepository) {

        this.events = eventRepository;
        this.organizers = organizerRepository;
        this.eventSeriesRepository = eventSeriesRepository;
    }

    @Override
    public void run(String... strings) throws Exception {
        EventSeries socialSeries = new EventSeries("Social");
        EventSeries beginnerJavaSeries = new EventSeries("Beginner Java");

        Organizer greg = this.organizers.save(new Organizer("greg", "turnquist",
                "ROLE_MANAGER"));
        Organizer oliver = this.organizers.save(new Organizer("oliver", "gierke",
                "ROLE_MANAGER"));

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("greg", "doesn't matter",
                        AuthorityUtils.createAuthorityList("ROLE_MANAGER")));

        eventSeriesRepository.save(socialSeries);
        eventSeriesRepository.save(beginnerJavaSeries);

        this.events.save(new Event("Spring Social",
                LocalDateTime.now().minusDays(2).atZone(ZoneId.systemDefault()),
                LocalDateTime.now().minusDays(1).atZone(ZoneId.systemDefault()),
                socialSeries, greg));

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("oliver", "doesn't matter",
                        AuthorityUtils.createAuthorityList("ROLE_MANAGER")));

        this.events.save(new Event("Beginner Java - Conditions",
                LocalDateTime.now().minusDays(2).atZone(ZoneId.systemDefault()),
                LocalDateTime.now().minusDays(1).atZone(ZoneId.systemDefault()),
                beginnerJavaSeries, oliver));

        SecurityContextHolder.clearContext();
    }
}
// end::code[]