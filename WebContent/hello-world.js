var botui = new BotUI('reminder-bot');//initializing botui
var showHistory = false; //flag to check if the user wants to see the history.

botui.message
.bot('Do you have a query I can help you with?')
.then(function () {
	return botui.action.button({
		delay: 1000,
		action: [{
			text: 'Yes',
			value: 'yes'
		}, {
			text: 'No!',
			value: 'no'
		}]
	})
}).then(function (res) {
	if(res.value == 'yes') {
		showReminderInput();
	} else {
		botui.message.bot('Okay.');
		connection.close();
	}
});

/**
 * Method to get user inputs.
 */
var showReminderInput = function () {
	data = "";	
	botui.message
	.bot({
		delay: 500,
		content: 'Write your query below:'
	})
	.then(function (res) {
		return botui.action.text({
			delay: 1000,
			action: {
				placeholder: 'write something...'
			}
		})

	}).then(function (res) {
		connection.sendMessage(res);
	});
}

/**
 * Method to display results from the bot/server side.
 */
var getResults = function (data) {
	botui.message
	.bot({
		delay: 1000,
		content: "Please wait, while we get results for your query. Thank you!"
	})
	.then(function () {
		if(data == " " || data == "[]"){
			output = "Sorry, I could not find any results for your search. Please try a different search"
		}else{
			results = JSON.parse(data);
			var output = '<div><h5>Please find your required results below:<h5/><br/><ul>'; 
			if(showHistory){
				for (var i = 0; i < results.length; i++) {
					output += '<li>'+JSON.stringify(results[i]) + '<br /></li>';
				}
				showHistory = false;
			}else{
				for (var i = 0; i < results.length; i++) {
					output += '<li>'+results[i] + '<br /></li>';
				}
			}

			output += '</ul></div>'
		}
		botui.message
		.bot({
			delay: 1000,
			type: 'html',
			content: output
		})
		.then(function () {


			return botui.action.button({
				delay: 1000,
				action: [{
					icon: 'plus',
					text: 'Have another query?',
					value: 'yes'
				},{
					text: 'Can\'t find what you are looking for?',
					value: 'no'
				}]
			})
		})
		.then(function (res) {
			if(res.value == 'yes') {
				showReminderInput();
			} else {
				return botui.action.button({
					delay: 1000,
					action: [{
						text: 'Would you like to see your search history?',
						value: 'yes'
					},{
						text: 'Exit the chat.',
						value: 'no'
					}]
				}).then(function (res) {
					if(res.value == 'yes') {
						connection.sendMessage( {"type":"text","value":"showHistory"});
						showHistory = true;
					} else {
						botui.message.bot('Okay. Have a nice day!');
						connection.close();
					}
				})
			}
		});
	});

}
