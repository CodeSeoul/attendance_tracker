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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.core.annotation.HandleAfterCreate;
import org.springframework.data.rest.core.annotation.HandleAfterDelete;
import org.springframework.data.rest.core.annotation.HandleAfterSave;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.hateoas.EntityLinks;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import static org.learnteachcode.seoul.attendance.tracker.util.WebSocketConfiguration.MESSAGE_PREFIX;


/**
 * @author Greg Turnquist
 */
// tag::code[]
@Component
@RepositoryEventHandler(Event.class)
public class EventHandler {

    private final SimpMessagingTemplate websocket;

    private final EntityLinks entityLinks;

    @Autowired
    public EventHandler(SimpMessagingTemplate websocket, EntityLinks entityLinks) {
        this.websocket = websocket;
        this.entityLinks = entityLinks;
    }

    @HandleAfterCreate
    public void newEvent(Event event) {
        this.websocket.convertAndSend(
                MESSAGE_PREFIX + "/newEvent", getPath(event));
    }

    @HandleAfterDelete
    public void deleteEvent(Event event) {
        this.websocket.convertAndSend(
                MESSAGE_PREFIX + "/deleteEvent", getPath(event));
    }

    @HandleAfterSave
    public void updateEvent(Event event) {
        this.websocket.convertAndSend(
                MESSAGE_PREFIX + "/updateEvent", getPath(event));
    }

    /**
     * Take an {@link Event} and get the URI using Spring Data REST's {@link EntityLinks}.
     *
     * @param event
     */
    private String getPath(Event event) {
        return this.entityLinks.linkForSingleResource(event.getClass(),
                event.getId()).toUri().getPath();
    }

}
// end::code[]