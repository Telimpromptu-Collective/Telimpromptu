// small helper function for selecting element by id
let id = id => document.getElementById(id);

//Establish the WebSocket connection and set up event handlers
let ws = null;
let username = null;
let playerCount = 0;

// Add event listeners to button and input field
id("connect").addEventListener("click", function () {
    ws = new WebSocket("ws://" + location.hostname + ":" + location.port + "/games/" + id("lobbyid").value);
    ws.onopen = function () {

        username = id("username").value
        var msg = {
            type: "createUser",
            username: username
        };
        ws.send(JSON.stringify(msg));
    }

    ws.onmessage = function (msg) {
        // just so the error text dissapears something happens
        id("errorText").hidden = true;

        let data = JSON.parse(msg.data);
        console.log(data);
        switch (data.type) {
            case "error":
                id("errorText").hidden = false;
                id("errorText").innerHTML = data.message;
                break;

            case "usernameUpdate":
                id("userlist").innerHTML = data.statuses.map(status =>
                    "<li>" +
                    status.username + (status.connected ? "" : " -dc") + (status.username === username ? " <b>(you)</b>" : "") +
                    "</li>"
                ).join("");

                playerCount = data.statuses.length;
                break;

            case "connectionSuccess":
                id("connectionForm").hidden = true;
                id("inLobby").hidden = false;
                break;

            case "gameStarted":
                id("connectionForm").hidden = true;
                id("inLobby").hidden = true;
                id("inGame").hidden = false;
                break;

            case "newPrompts":
                id("promptList").innerHTML += data.scriptPrompts.map(prompt  =>
                    '<div id="prompt-' + prompt.id + '">' +
                    '    <label for="prompt-' + prompt.id + '-textarea">' + prompt.description + '</label>' +
                    '    <br>' +
                    '    <textarea class="prompt-textarea" id="prompt-' + prompt.id + '-textarea" rows="5"></textarea>' +
                    '    <br>' +
                    '    <button class="submit-prompt" id="prompt-' + prompt.id + '-submit">Submit</button>' +
                    '    <br>' +
                    '</div>' +
                    '<br>'
                ).join("");

                data.scriptPrompts.forEach(prompt =>
                    // this might leak on some browsers... maybe i should unbind but who cares LOL
                    id('prompt-' + prompt.id + '-submit').addEventListener("click", function () {
                        var msg = {
                            type: "promptResponse",
                            response: id('prompt-' + prompt.id + '-textarea').value,
                            id: prompt.id
                        };
                        ws.send(JSON.stringify(msg));
                        id('prompt-' + prompt.id).remove();
                    })
                );
                break;
            }
        }

    ws.onclose = () => console.log("WebSocket connection closed");
});

id("startGame").addEventListener("click", function () {
    var msg = {
        type: "startGame"
    };
    ws.send(JSON.stringify(msg));
});

