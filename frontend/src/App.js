import React, {Component} from 'react';
import './App.css';
import Form from "react-bootstrap/Form";
import Button from "react-bootstrap/Button";
import Col from "react-bootstrap/Col";

class App extends Component {

	render() {
		return (
			<div className="App">
				<BookingForm/>
				<BookingTable/>
			</div>
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

		fetch('/bookings', {
			method: 'POST',
			headers: {
				Accept: 'application/json',
				'Content-Type': 'application/json',
			},
			body: JSON.stringify(this.state.booking)
		})
			.then((response) =>
				response.json()
			)
			.then((data) => {
				this.setState({
					booking: data
				});
			})
			.catch((error) => {
				console.error(error);
			});
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
		this.state = {
			bookings: []
		};
	}

	componentDidMount() {
		fetch('/bookings')
			.then(response => response.json())
			.then(data => this.setState({bookings: data}));
	};

	render() {
		const bookings = this.state.bookings.map(booking =>
			<BookingRow key={booking.id} value={booking}/>
		);

		return (
			<table className="table">
				<thead>
				<tr>
					<th>Wochentag</th>
					<th>Klasse</th>
					<th>Uhrzeit</th>
				</tr>
				</thead>
				<tbody>
				{bookings}
				</tbody>
			</table>
		)
	}
}

class BookingRow extends Component {

	render() {
		return (
			<>
				<tr>
					<td>{this.props.value.dayOfWeek}</td>
					<td>{this.props.value.classType}</td>
					<td>{this.props.value.time}</td>
				</tr>
			</>
		)
	}
}

export default App;