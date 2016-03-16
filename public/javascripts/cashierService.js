function CashierService($rootScope, $timeout, wsUrl) {
	
	var wsSession;
	
	function handler(event, data) {
		$rootScope.$broadcast(event, data);
		$rootScope.$digest();
	}

	WsSession.connect(wsUrl, handler).done(function(ws) {
		wsSession = ws;
	});

	this.getRestoInfo = function() {
		wsSession.send('RestoInfo');
	};
	
	this.sendMessage = function(table, text) {
		wsSession.send('TableMessage', {
			table: table,
			message: text
		});
	};
	
	this.openTable = function(id) {
		wsSession.send('CreateTableServiceSession', id);
	};
	
	this.closeTable = function(id) {
		wsSession.send('CloseTableServiceSession', id);
	};
	
}
