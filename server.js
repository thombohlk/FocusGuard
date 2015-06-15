var ws = require("nodejs-websocket")
var server = ws.createServer(function (conn) {
    console.log("New connection")
    conn.on("text", function (str) {
        console.log("Received "+str)
    })
    conn.on("close", function (code, reason) {
        console.log("Connection closed")
    })
}).listen(8080)

function broadcast(server, msg) {
    server.connections.forEach(function (conn) {
        conn.sendText(msg)
    })
}

function endOfFocus() {
    broadcast(server, "false")
}

function startOfFocus() {
    broadcast(server, "true")
}
