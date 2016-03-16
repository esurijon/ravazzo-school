function WsSession(websocket) {

	this.send = function(event, data) {
		websocket.send(JSON.stringify({
			event: event, 
			data: data || null
		}));
	};

	this.logout = function() {
		websocket.close();
	};
		
}

WsSession.connect = function(url, handler) {

	var wsDeferred = jQuery.Deferred();

	websocket = new WebSocket(url);

	websocket.onopen = function(evt) {
		console.log(evt);
		var session = new WsSession(websocket);
		wsDeferred.resolve(session);
		handler('WsOpen');
	};

	websocket.onclose = function(evt) {
		console.log(evt);
		handler('WsClose');
	};

	websocket.onerror = function(evt) {
		console.log(evt);
		handler('WsError', evt);
	};

	websocket.onmessage = function(evt) {
		console.log(evt);
		var frame = JSON.parse(evt.data);
		handler(frame.event, frame.data);
	};

	return wsDeferred.promise();

};

