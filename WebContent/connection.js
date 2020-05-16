var connection = new WebSocket('ws://127.0.0.1:4444');

/**
 * Method to log and check the connection to server is open.
 */
connection.onopen = function () {
	console.log('Connected!');
	connection.send('Ping'); // Send the message 'Ping' to the server


};

/**
 * Method to log errors.
 */
connection.onerror = function (error) {
	console.log('WebSocket Error ' + error);
};


/**
 * Method to log and receive messages from the server.
 */
connection.onmessage = function (e) {
	var data = null;
	console.log('Server on message: ' + JSON.stringify(e));
	if(e!= null && e != undefined){
		data =  e.data;
		getResults(data);
	}

};

/**
 * Method to log and send messages to the server.
 */
connection.sendMessage = function (e) {
	console.log('Server send message: ' + JSON.stringify(e));
	connection.send(e.value);
};