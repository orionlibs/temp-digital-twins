$(document).ready(function ()
{
    let socket = new SockJS('http://localhost:8080/websocket');
    let stompClient = Stomp.over(socket);
    stompClient.heartbeat.outgoing = 0;
    stompClient.heartbeat.incoming = 0;
    stompClient.reconnect_delay = 5000;

    //stompClient.connect({username: 'user', password: 'password'}, function(frame)
    stompClient.connect({}, function(frame)
    {
        console.log('Connected: ' + frame);
        stompClient.subscribe('/topic/mqtt/iot-devices-live/summaries', function(message)
        {
            if(message.body)
            {
                let messageBody = JSON.parse(message.body);
                $("#messages_arrived").val($("#messages_arrived").val() + ($("#messages_arrived").val() ? '\n' : '') + messageBody.mqttMessageArrived);
            }
        });
    });

    window.onbeforeunload = function()
    {
        if(stompClient)
        {
            stompClient.disconnect();
        }
    };
});