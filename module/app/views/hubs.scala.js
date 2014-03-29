function getSocket() {
	var WS = window['MozWebSocket'] ? MozWebSocket : WebSocket;
	var socket = new WS("ws://localhost:9000/signalJ/Join"); //"at sign routes.PlaySockets.join().webSocketURL(request)"
	return socket;
}

var socket = getSocket();
var uuid;
var id = 0;
var map = {};

var receiveEvent = function(event) {
    var data = JSON.parse(event.data);
    uuid = data.uuid;
    
    // Handle errors
    if(data.error) {
        chatSocket.close();
        console.log("Error: " + data.error);
        return;
    }
    if(data.type === "init") {
    	uuid = data.uuid;
    }
    if(data.type === "methodReturn") {
    	var f = map[data.id];
    	if(f != undefined) f(data.returnValue);
    }
    console.log("Message from server: %O", data);
};

socket.onmessage = receiveEvent;

function systemsend(message) {
	id = id + 1;
	message.uuid = uuid;
	message.id = '' + id;
	var str = JSON.stringify(message);
	console.log("Message to server: %O", message);
	socket.send(str);
}