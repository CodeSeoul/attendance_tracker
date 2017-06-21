'use strict';

// Strongly inspired by https://github.com/spring-guides/tut-react-and-spring-data-rest
// tag::vars[]
const React = require('react');
const ReactDOM = require('react-dom');
const when = require('when');
const client = require('./client');

const follow = require('./follow'); // function to hop multiple links by "rel"

const stompClient = require('./websocket-listener');

const root = '/api/v1';

// end::vars[]

// tag::app[]
class App extends React.Component {

	constructor(props) {
		super(props);
		this.state = {
		    events: [],
		    attributes: [],
		    page: 1,
		    pageSize: 2,
		    links: {}
        };
        this.updatePageSize = this.updatePageSize.bind(this);
        this.onCreate = this.onCreate.bind(this);
        this.onUpdate = this.onUpdate.bind(this);
        this.onDelete = this.onDelete.bind(this);
        this.onNavigate = this.onNavigate.bind(this);
        this.refreshCurrentPage = this.refreshCurrentPage.bind(this);
        this.refreshAndGoToLastPage = this.refreshAndGoToLastPage.bind(this);
	}

	loadFromServer(pageSize) {
        follow(client, root, [
                {rel: 'events', params: {size: pageSize}}]
        ).then(eventCollection => {
            return client({
                method: 'GET',
                path: eventCollection.entity._links.profile.href,
                headers: {'Accept': 'application/schema+json'}
            }).then(schema => {
                // tag::json-schema-filter[]
                /**
                 * Filter unneeded JSON Schema properties, like uri references and
                 * subtypes ($ref).
                 */
                Object.keys(schema.entity.properties).forEach(function (property) {
                    if (schema.entity.properties[property].hasOwnProperty('format') &&
                        schema.entity.properties[property].format === 'uri') {
                        delete schema.entity.properties[property];
                    }
                    else if (schema.entity.properties[property].hasOwnProperty('$ref')) {
                        delete schema.entity.properties[property];
                    }
                });

                this.schema = schema.entity;
                this.links = eventCollection.entity._links;
                return eventCollection;
                // end::json-schema-filter[]
            });
        }).then(eventCollection => {
            this.page = eventCollection.entity.page;
            return eventCollection.entity._embedded.events.map(event =>
                    client({
                        method: 'GET',
                        path: event._links.self.href
                    })
            );
        }).then(eventPromises => {
            return when.all(eventPromises);
        }).done(events => {
            this.setState({
                page: this.page,
                events: events,
                attributes: Object.keys(this.schema.properties),
                pageSize: pageSize,
                links: this.links
            });
        });
    }

    // tag::on-create[]
    onCreate(newEvent) {
        follow(client, root, ['events']).done(response => {
            client({
                method: 'POST',
                path: response.entity._links.self.href,
                entity: newEvent,
                headers: {'Content-Type': 'application/json'}
            })
        })
    }
    // end::on-create[]

    // tag::on-update[]
    onUpdate(event, updatedEvent) {
        client({
            method: 'PUT',
            path: event.entity._links.self.href,
            entity: updatedEvent,
            headers: {
                'Content-Type': 'application/json',
                'If-Match': event.headers.Etag
            }
        }).done(response => {
            /* Let the websocket handler update the state */
        }, response => {
            if (response.status.code === 403) {
                alert('ACCESS DENIED: You are not authorized to update ' +
                    event.entity._links.self.href);
            }
            if (response.status.code === 412) {
                alert('DENIED: Unable to update ' + event.entity._links.self.href +
                    '. Your copy is stale.');
            }
        });
    }
    // end::on-update[]

    // tag::on-delete[]
    onDelete(event) {
        client({method: 'DELETE', path: event.entity._links.self.href}
        ).done(response => {/* let the websocket handle updating the UI */},
        response => {
            if (response.status.code === 403) {
                alert('ACCESS DENIED: You are not authorized to delete ' +
                    event.entity._links.self.href);
            }
        });
    }
    // end::on-delete[]

    onNavigate(navUri) {
        client({
            method: 'GET',
            path: navUri
        }).then(eventCollection => {
            this.links = eventCollection.entity._links;
            this.page = eventCollection.entity.page;

            return eventCollection.entity._embedded.events.map(event =>
                    client({
                        method: 'GET',
                        path: event._links.self.href
                    })
            );
        }).then(eventPromises => {
            return when.all(eventPromises);
        }).done(events => {
            this.setState({
                page: this.page,
                events: events,
                attributes: Object.keys(this.schema.properties),
                pageSize: this.state.pageSize,
                links: this.links
            });
        });
    }

    updatePageSize(pageSize) {
        if (pageSize !== this.state.pageSize) {
            this.loadFromServer(pageSize);
        }
    }

    // tag::websocket-handlers[]
    refreshAndGoToLastPage(message) {
        follow(client, root, [{
            rel: 'events',
            params: {size: this.state.pageSize}
        }]).done(response => {
            if (response.entity._links.last !== undefined) {
                this.onNavigate(response.entity._links.last.href);
            } else {
                this.onNavigate(response.entity._links.self.href);
            }
        })
    }

    refreshCurrentPage(message) {
        follow(client, root, [{
            rel: 'events',
            params: {
                size: this.state.pageSize,
                page: this.state.page.number
            }
        }]).then(eventCollection => {
            this.links = eventCollection.entity._links;
            this.page = eventCollection.entity.page;

            return eventCollection.entity._embedded.events.map(event => {
                return client({
                    method: 'GET',
                    path: event._links.self.href
                })
            });
        }).then(eventPromises => {
            return when.all(eventPromises);
        }).then(events => {
            this.setState({
                page: this.page,
                events: events,
                attributes: Object.keys(this.schema.properties),
                pageSize: this.state.pageSize,
                links: this.links
            });
        });
    }
    // end::websocket-handlers[]

    // tag::register-handlers[]
    componentDidMount() {
        this.loadFromServer(this.state.pageSize);
        stompClient.register([
            {route: '/topic/newEvent', callback: this.refreshAndGoToLastPage},
            {route: '/topic/updateEvent', callback: this.refreshCurrentPage},
            {route: '/topic/deleteEvent', callback: this.refreshCurrentPage}
        ]);
        /*
        client({method: 'GET', path: '/api/v1/events'}).done(response => {
            this.setState({events: response.entity._embedded.events});
        });
        */
    }
    // end::register-handlers[]

    render() {
        return (
            <div>
                //<CreateDialog attributes={this.state.attributes} onCreate={this.onCreate}/>
                <EventList page={this.state.page}
                              events={this.state.events}
                              links={this.state.links}
                              pageSize={this.state.pageSize}
                              attributes={this.state.attributes}
                              onNavigate={this.onNavigate}
                              onUpdate={this.onUpdate}
                              onDelete={this.onDelete}
                              updatePageSize={this.updatePageSize}/>
            </div>
        )
    }
}
// end::app[]

// tag::createDialog[]
class CreateDialog extends React.Component {

	constructor(props) {
		super(props);
		this.handleSubmit = this.handleSubmit.bind(this);
	}

	handleSubmit(e) {
		e.preventDefault();
		var newEvent = {};
		this.props.attributes.forEach(attribute => {
			newEvent[attribute] = ReactDOM.findDOMNode(this.refs[attribute]).value.trim();
		});
		this.props.onCreate(newEvent);
		this.props.attributes.forEach(attribute => {
			ReactDOM.findDOMNode(this.refs[attribute]).value = ''; // clear out the dialog's inputs
		});
		window.location = "#";
	}

	render() {
		var inputs = this.props.attributes.map(attribute =>
				<p key={attribute}>
					<input type="text" placeholder={attribute} ref={attribute} className="field" />
				</p>
		);
		return (
			<div>
				<a href="#createEvent">Create</a>

				<div id="createEvent" className="modalDialog">
					<div>
						<a href="#" title="Close" className="close">X</a>

						<h2>Create new event</h2>

						<form>
							{inputs}
							<button onClick={this.handleSubmit}>Create</button>
						</form>
					</div>
				</div>
			</div>
		)
	}
}
// end::createDialog[]

// tag::updateDialog[]
class UpdateDialog extends React.Component {

	constructor(props) {
		super(props);
		this.handleSubmit = this.handleSubmit.bind(this);
	}

	handleSubmit(e) {
		e.preventDefault();
		var updatedEvent = {};
		this.props.attributes.forEach(attribute => {
			updatedEvent[attribute] = ReactDOM.findDOMNode(this.refs[attribute]).value.trim();
		});
		this.props.onUpdate(this.props.event, updatedEvent);
		window.location = "#";
	}

	render() {
		var inputs = this.props.attributes.map(attribute =>
				<p key={this.props.event.entity[attribute]}>
					<input type="text" placeholder={attribute}
						   defaultValue={this.props.event.entity[attribute]}
						   ref={attribute} className="field" />
				</p>
		);

		var dialogId = "updateEvent-" + this.props.event.entity._links.self.href;

		return (
			<div>
				<a href={"#" + dialogId}>Update</a>

				<div id={dialogId} className="modalDialog">
					<div>
						<a href="#" title="Close" className="close">X</a>

						<h2>Update an event</h2>

						<form>
							{inputs}
							<button onClick={this.handleSubmit}>Update</button>
						</form>
					</div>
				</div>
			</div>
		)
	}
}
// end::updateDialogue[]

// tag::event-list[]
class EventList extends React.Component{

	constructor(props) {
        super(props);
        this.handleNavFirst = this.handleNavFirst.bind(this);
        this.handleNavPrev = this.handleNavPrev.bind(this);
        this.handleNavNext = this.handleNavNext.bind(this);
        this.handleNavLast = this.handleNavLast.bind(this);
        this.handleInput = this.handleInput.bind(this);
    }

    handleInput(e) {
        e.preventDefault();
        var pageSize = ReactDOM.findDOMNode(this.refs.pageSize).value;
        if (/^[0-9]+$/.test(pageSize)) {
            this.props.updatePageSize(pageSize);
        } else {
            ReactDOM.findDOMNode(this.refs.pageSize).value = pageSize.substring(0, pageSize.length - 1);
        }
    }

    handleNavFirst(e) {
        e.preventDefault();
        this.props.onNavigate(this.props.links.first.href);
    }

    handleNavPrev(e) {
        e.preventDefault();
        this.props.onNavigate(this.props.links.prev.href);
    }

    handleNavNext(e) {
        e.preventDefault();
        this.props.onNavigate(this.props.links.next.href);
    }

    handleNavLast(e) {
        e.preventDefault();
        this.props.onNavigate(this.props.links.last.href);
    }

    render() {
        var pageInfo = this.props.page.hasOwnProperty("number") ?
            <h3>Events - Page {this.props.page.number + 1} of {this.props.page.totalPages}</h3> : null;

        var events = this.props.events.map(event =>
            <Event key={event.entity._links.self.href}
                      event={event}
                      attributes={this.props.attributes}
                      onUpdate={this.props.onUpdate}
                      onDelete={this.props.onDelete}/>
        );

        var navLinks = [];
        if ("first" in this.props.links) {
            navLinks.push(<button key="first" onClick={this.handleNavFirst}>&lt;&lt;</button>);
        }
        if ("prev" in this.props.links) {
            navLinks.push(<button key="prev" onClick={this.handleNavPrev}>&lt;</button>);
        }
        if ("next" in this.props.links) {
            navLinks.push(<button key="next" onClick={this.handleNavNext}>&gt;</button>);
        }
        if ("last" in this.props.links) {
            navLinks.push(<button key="last" onClick={this.handleNavLast}>&gt;&gt;</button>);
        }

        return (
            <div>
                {pageInfo}
                <input ref="pageSize" defaultValue={this.props.pageSize} onInput={this.handleInput}/>
                <table>
                    <tbody>
                        <tr>
                            <th>Event Series</th>
                            <th>Event Name</th>
                            <th>Start Date</th>
                            <th>End Date</th>
                            <th></th>
                            <th></th>
                        </tr>
                        {event}
                    </tbody>
                </table>
                <div>
                    {navLinks}
                </div>
            </div>
        )
    }
}
// end::event-list[]

// tag::event[]
class Event extends React.Component{

    constructor(props) {
        super(props);
        this.handleDelete = this.handleDelete.bind(this);
    }

    handleDelete() {
        this.props.onDelete(this.props.events);
    }

    render() {
        return (
            <tr>
			    <td>{this.props.event.eventSeries.name}</td>
				<td>{this.props.event.name}</td>
				<td>{this.props.event.eventStart}</td>
				<td>{this.props.event.eventEnd}</td>
                <td>
                    <UpdateDialog event={this.props.event}
                                  attributes={this.props.attributes}
                                  onUpdate={this.props.onUpdate}/>
                </td>
                <td>
                    <button onClick={this.handleDelete}>Delete</button>
                </td>
            </tr>
        )
    }
}
// end::event[]

// tag::render[]
ReactDOM.render(
	<App />,
	document.getElementById('react')
)
// end::render[]