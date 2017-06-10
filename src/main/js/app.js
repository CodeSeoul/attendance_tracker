'use strict';

// Strongly inspired by https://github.com/spring-guides/tut-react-and-spring-data-rest
// tag::vars[]
const React = require('react');
const ReactDOM = require('react-dom')
const client = require('./client');
// end::vars[]

// tag::app[]
class App extends React.Component {

	constructor(props) {
		super(props);
		this.state = {events: []};
	}

	componentDidMount() {
		client({method: 'GET', path: '/api/v1/events'}).done(response => {
			this.setState({events: response.entity._embedded.events});
		});
	}

	render() {
		return (
			<EventList events={this.state.events}/>
		)
	}
}
// end::app[]

// tag::event-list[]
class EventList extends React.Component{
	render() {
		var events = this.props.events.map(event =>
			<Event key={event._links.self.href} event={event}/>
		);
		return (
			<table>
				<tbody>
					<tr>
						<th>Event Series</th>
						<th>Event Name</th>
						<th>Start Date</th>
						<th>End Date</th>
					</tr>
					{events}
				</tbody>
			</table>
		)
	}
}
// end::event-list[]

// tag::event[]
class Event extends React.Component{
	render() {
		return (
			<tr>
			    <td>{this.props.event.eventSeries.name}</td>
				<td>{this.props.event.name}</td>
				<td>{this.props.event.eventStart}</td>
				<td>{this.props.event.eventEnd}</td>
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