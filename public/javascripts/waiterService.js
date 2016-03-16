function WaiterService($rootScope, $timeout, wsUrl) {
	
	var wsSession;
	
	function handler(event, data) {
		$rootScope.$broadcast(event, data);
		$rootScope.$digest();
	}

	WsSession.connect(wsUrl, handler).done(function(ws) {
		wsSession = ws;
	});
	
	this.sendMessage = function(table, text) {
		wsSession.send('TableMessage', {
			table: table,
			message: text
		});
	};
	
}
