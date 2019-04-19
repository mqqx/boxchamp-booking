import React, {Component} from 'react';
import './App.css';

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
			dayOfWeek: '',
			classType: '',
			time: ''
		};

		this.handleChange = this.handleChange.bind(this);
		this.handleSubmit = this.handleSubmit.bind(this);
	}

	handleChange(event) {
		const target = event.target;
		const value = target.value;
		const name = target.name;

		this.setState({
			[name]: value
		});
	}

	handleSubmit(event) {
		alert('Your favorite flavor is: ' + this.state.value);
		event.preventDefault();
	}

	render() {
		return (
			<form onSubmit={this.handleSubmit}>
				<DayOfWeekSelect
					value={this.state.dayOfWeek}
					onChange={this.handleChange}
				/><br/>
				<ClassSelect
					value={this.state.classType}
					onChange={this.handleChange}
				/><br/>
				<TimeSelect
					value={this.state.time}
					onChange={this.handleChange}
				/><br/>
				<input type="submit" value="Submit"/>
			</form>
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
			.then(response => response.json())
			.then(data => this.setState({daysOfWeek: data}));
	};

	render() {
		return (
			<label>
				Wochentag:
				<select
					name="dayOfWeek"
					value={this.props.dayOfWeek}
					onChange={this.props.onChange}>
					<option value=''/>
					{this.state.daysOfWeek.map(dayOfWeek => <option key={dayOfWeek}>{dayOfWeek}</option>)}
				</select>
			</label>
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
			.then(response => response.json())
			.then(data => this.setState({classTypes: data}));
	};

	render() {
		return (
			<label>
				Klasse:
				<select
					name="classType"
					value={this.props.classType}
					onChange={this.props.onChange}>
					<option value=''/>
					{this.state.classTypes.map(classType => <option key={classType}>{classType}</option>)}
				</select>
			</label>
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
			.then(response => response.json())
			.then(data => this.setState({times: data}));
	};

	render() {
		return (
			<label>
				Uhrzeit:
				<select
					name="time"
					value={this.props.time}
					onChange={this.props.onChange}>
					<option value=''/>
					{this.state.times.map(time => <option key={time}>{time}</option>)}
				</select>
			</label>
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