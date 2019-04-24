import React, {Component} from 'react';
import './App.css';
import Form from "react-bootstrap/Form";
import Button from "react-bootstrap/Button";
import Col from "react-bootstrap/Col";
import {library} from '@fortawesome/fontawesome-svg-core'
import {faEdit, faTrash} from "@fortawesome/free-solid-svg-icons";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import Table from "react-bootstrap/Table";

library.add(faEdit, faTrash);

class App extends Component {

	render() {
		return (
			<div className="App">
				<Booking/>
			</div>
		);
	}
}

class Booking extends Component {

	constructor(props) {
		super(props);
		this.state = {
			bookings: []
		};

		this.onDelete = this.onDelete.bind(this);
		this.onSubmit = this.onSubmit.bind(this);
	}

	onDelete(booking) {
		fetch('/bookings', {
			method: 'DELETE',
			headers: {
				Accept: 'application/json',
				'Content-Type': 'application/json',
			},
			body: JSON.stringify(booking)
		})
			.then(() => {
				let updatedBookings = this.state.bookings.filter(i => i.id !== booking.id);
				this.setState({
					bookings: updatedBookings
				});
			})
			.catch((error) => {
				console.error(error);
			});
	}

	onSubmit(booking) {

		fetch('/bookings', {
			method: 'POST',
			headers: {
				Accept: 'application/json',
				'Content-Type': 'application/json',
			},
			body: JSON.stringify(booking)
		})
			.then((response) =>
				response.json()
			)
			.then((data) => {
				this.setState({
						bookings: [...this.state.bookings, data]
					}
				);
			})
			.catch((error) => {
				console.error(error);
			});
	}

	componentDidMount() {
		fetch('/bookings')
			.then(response => response.json())
			.then(data =>
				this.setState({
					bookings: data
				})
			);
	};

	render() {
		return (
			<>
				<BookingForm onSubmit={this.onSubmit}/>
				<BookingTable value={this.state.bookings} onDelete={this.onDelete}/>
			</>
		);
	}
}

class BookingForm extends Component {
	constructor(props) {
		super(props);
		this.state = {
			booking: {
				dayOfWeek: '',
				classType: '',
				time: ''
			}
		};

		this.handleChange = this.handleChange.bind(this);
		this.handleSubmit = this.handleSubmit.bind(this);
	}

	handleChange(event) {
		const target = event.target;
		const value = target.value;
		const name = target.name;
		const booking = this.state.booking;
		booking[name] = value;

		this.setState({
			booking: booking
		});
	}

	handleSubmit(event) {
		event.preventDefault();
		this.props.onSubmit(this.state.booking);
	}

	render() {
		return (
			<Form onSubmit={this.handleSubmit}>
				<Form.Row>
					<DayOfWeekSelect
						value={this.state.booking.dayOfWeek}
						onChange={this.handleChange}
					/>
					<ClassSelect
						value={this.state.booking.classType}
						onChange={this.handleChange}
					/>
					<TimeSelect
						value={this.state.booking.time}
						onChange={this.handleChange}
					/>
				</Form.Row>
				<Button variant="primary" type="submit">
					Speichern
				</Button>
			</Form>
		);
	}
}

class DayOfWeekSelect extends Component {
	constructor(props) {
		super(props);

		this.state = {
			daysOfWeek: []
		};
	}

	componentDidMount() {
		fetch('/bookings/daysOfWeek')
			.then(response =>
				response.json()
			)
			.then(data =>
				this.setState({daysOfWeek: data})
			);
	};

	render() {
		return (
			<Form.Group as={Col}>
				<Form.Label>
					Wochentag
				</Form.Label>
				<Form.Control
					required
					as="select"
					name="dayOfWeek"
					value={this.props.value}
					onChange={this.props.onChange}>
					<option value=''/>
					{this.state.daysOfWeek.map(dayOfWeek => <option key={dayOfWeek}>{dayOfWeek}</option>)}
				</Form.Control>
			</Form.Group>
		);
	}
}

class ClassSelect extends Component {
	constructor(props) {
		super(props);

		this.state = {
			classTypes: []
		};
	}

	componentDidMount() {
		fetch('/bookings/classTypes')
			.then(response =>
				response.json()
			)
			.then(data =>
				this.setState({classTypes: data})
			);
	};

	render() {
		return (
			<Form.Group as={Col}>
				<Form.Label>
					Klasse
				</Form.Label>
				<Form.Control
					required
					as="select"
					name="classType"
					value={this.props.value}
					onChange={this.props.onChange}>
					<option value=''/>
					{this.state.classTypes.map(classType => <option key={classType}>{classType}</option>)}
				</Form.Control>
			</Form.Group>
		);
	}
}

class TimeSelect extends Component {
	constructor(props) {
		super(props);

		this.state = {
			times: []
		};
	}

	componentDidMount() {
		fetch('/bookings/times')
			.then(response =>
				response.json()
			)
			.then(data =>
				this.setState({times: data})
			);
	};

	render() {
		return (
			<Form.Group as={Col}>
				<Form.Label>
					Uhrzeit
				</Form.Label>
				<Form.Control
					required
					as="select"
					name="time"
					value={this.props.value}
					onChange={this.props.onChange}>
					<option value=''/>
					{this.state.times.map(time => <option key={time}>{time}</option>)}
				</Form.Control>
			</Form.Group>
		);
	}
}

class BookingTable extends Component {
	constructor(props) {
		super(props);

		this.onDelete = this.onDelete.bind(this);
	}

	onDelete(booking) {
		this.props.onDelete(booking);
	}

	render() {
		const bookings = this.props.value.map(booking =>
			<BookingRow key={booking.id} value={booking} onDelete={this.onDelete}/>
		);

		return (
			<Table>
				<thead>
				<tr>
					<th>Wochentag</th>
					<th>Klasse</th>
					<th>Uhrzeit</th>
					<th/>
					<th/>
				</tr>
				</thead>
				<tbody>
				{bookings}
				</tbody>
			</Table>
		)
	}
}

class BookingRow extends Component {

	constructor(props) {
		super(props);
		this.handleDelete = this.handleDelete.bind(this);
	}

	handleDelete() {
		this.props.onDelete(this.props.value);
	}

	render() {
		return (
			<tr>
				<td className="align-middle">{this.props.value.dayOfWeek}</td>
				<td className="align-middle">{this.props.value.classType}</td>
				<td className="align-middle">{this.props.value.time}</td>
				<td>
					<Button variant="link"
							onClick={() => console.log('this is:', this)}>
						<FontAwesomeIcon icon="edit"/>
					</Button>
				</td>
				<td>
					<Button variant="link"
							onClick={this.handleDelete}>
						<FontAwesomeIcon icon="trash"/>
					</Button>
				</td>
			</tr>
		)
	}
}

export default App;