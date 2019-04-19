import React, {Component} from 'react';
import logo from './logo.svg';
import './App.css';

class App extends Component {

	state = {};

	componentDidMount() {
		// setInterval(this.hello, 250);
	}

	hello = () => {
		fetch('/api/hello')
			.then(response => response.text())
			.then(message => {
				this.setState({message: message});
			});
	};

	render() {
		return (
			<div className="App">
				{/*<header className="App-header">*/}
				{/*	<img src={logo} className="App-logo" alt="logo"/>*/}
				{/*	<h1 className="App-title">{this.state.message}</h1>*/}
				{/*</header>*/}
				<p className="App-intro">
					To get started, edit <code>src/App.js</code> and save to reload.
				</p>
				<BookingAdd/>
			</div>
		);
	}
}

class BookingAdd extends Component {
	render() {
		return <div><DaySelect/> <ClassSelect/> <TimeSelect/></div>;
	}
}


class DaySelect extends Component {
	constructor(props) {
		super(props);

		this.state = {
			days: []
		};
	}

	componentDidMount() {
		fetch('/bookings/days')
			.then(response => response.json())
			.then(days => this.setState({days: days}));
	};

	render() {
		return <select>{this.state.days.map(day => <option key={day}>{day}</option>)}</select>;
	}
}

class ClassSelect extends Component {
	constructor(props) {
		super(props);

		this.state = {
			days: []
		};
	}

	componentDidMount() {
		fetch('/bookings/classes')
			.then(response => response.json())
			.then(days => this.setState({days: days}));
	};

	render() {
		return <select>{this.state.days.map(day => <option key={day}>{day}</option>)}</select>;
	}
}

class TimeSelect extends Component {
	constructor(props) {
		super(props);

		this.state = {
			days: []
		};
	}

	componentDidMount() {
		fetch('/bookings/times')
			.then(response => response.json())
			.then(days => this.setState({days: days}));
	};

	render() {
		return <select>{this.state.days.map(day => <option key={day}>{day}</option>)}</select>;
	}
}

export default App;