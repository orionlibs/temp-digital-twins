$(document).ready(function ()
{
    $('body').on('click', '#refresh-connection-configurations-table-button', function(e)
    {
        window.location.reload();
    });


    $('body').on('click', '#start-test-MQTT-publishes-button', function(e)
    {
        orionCommon.makeGetAJAXCall('http://localhost:8080/wapi/v1/connection-configurations/MQTT/scheduled-test-publishes', connectionConfigurations.processScheduleTestMQTTPublishes);
    });


    $('body').on('click', '#stop-test-MQTT-publishes-button', function(e)
    {
        orionCommon.makeDeleteAJAXCall('http://localhost:8080/wapi/v1/connection-configurations/MQTT/scheduled-test-publishes', connectionConfigurations.processScheduleTestMQTTPublishesStop);
    });


    const connectionConfigurationJSONFilesToUpload = document.getElementById('connection-configuration-json-files-to-upload');

    connectionConfigurationJSONFilesToUpload.addEventListener('change', async () =>
    {
        orionCommon.uploadFile('http://localhost:8080/wapi/v1/connection-configurations/MQTT/uploads/json', 'connection-configuration-json-files-to-upload', connectionConfigurations.processConnectionConfigurationAdditionViaJSON);
    });


    $('body').on('click', '#add-connection-configuration-button', function(e)
    {
        const dataToSend =
        {
            dataSourceId: $("#input-dataSourceId").val(),
            dataSourceType: $("#input-dataSourceType").val(),
            //apiUrl: $("#input-apiUrl").val(),
            //apiKey: $("#input-apiKey").val(),
            //httpMethod: $("#input-httpMethod").val(),
            brokerUrl: $("#input-brokerUrl").val(),
            brokerPort: $("#input-brokerPort").val(),
            topic: $("#input-topic").val(),
            clientId: $("#input-clientId").val(),
            qualityOfServiceLevel: $("#input-qualityOfServiceLevel").val()
            //username: $("#input-username").val(),
            //password: $("#input-password").val()
        };

        orionCommon.makePostAJAXCall('http://localhost:8080/wapi/v1/connection-configurations/MQTT', dataToSend, connectionConfigurations.processConnectionConfigurationAddition);
    });


    populateTableOfConnectionConfigurations();


    $('body').on('click', '#update-connection-configuration-button', function(e)
    {
        const dataToSend =
        {
            connectionConfigurationID: $("#inputedit-connectionConfigurationID").val(),
            dataSourceId: $("#inputedit-dataSourceId").val(),
            dataSourceType: $("#inputedit-dataSourceType").val(),
            //apiUrl: $("#inputedit-apiUrl").val(),
            //apiKey: $("#inputedit-apiKey").val(),
            //httpMethod: $("#inputedit-httpMethod").val(),
            brokerUrl: $("#inputedit-brokerUrl").val(),
            brokerPort: $("#inputedit-brokerPort").val(),
            topic: $("#inputedit-topic").val(),
            clientId: $("#inputedit-clientId").val(),
            qualityOfServiceLevel: $("#inputedit-qualityOfServiceLevel").val()
            //username: $("#inputedit-username").val(),
            //password: $("#inputedit-password").val()
        };

        orionCommon.makePutAJAXCall('http://localhost:8080/wapi/v1/connection-configurations/MQTT', dataToSend, connectionConfigurations.processConnectionConfigurationUpdate);
    });


    $('body').on('click', '#disconnect-from-MQTT-broker-server-modal', function(e)
    {
        e.preventDefault();
        $('#disconnect-from-MQTT-broker-server-modal').modal('show');
    });


    $('body').on('click', '#disconnect-from-MQTT-broker-server-button', function(e)
    {
        orionCommon.makeGetAJAXCall('http://localhost:8080/wapi/v1/connection-configurations/MQTT/disconnections/' + $("#input-connection-ID-generated").val(), connectionConfigurations.processDisconnectionFromMQTTBrokerServer);
    });
});


let handleRenderTableData = function() {
	let table = $('#connection-configuration-summaries-table').DataTable({
		dom: "<'row mb-3'<'col-sm-4'l><'col-sm-8 text-end'<'d-flex justify-content-end'fB>>>t<'d-flex align-items-center mt-3'<'me-auto'i><'mb-0'p>>",
    lengthMenu: [ 10, 20, 30, 40, 50 ],
		responsive: true,
		buttons: [
			{ extend: 'print', className: 'btn btn-outline-default btn-sm ms-2' },
			{ extend: 'csv', className: 'btn btn-outline-default btn-sm' }
		]
	});
};


function populateTableOfConnectionConfigurations()
{
    fetch('http://localhost:8080/wapi/v1/connection-configurations/summaries')
    .then(response =>
    {
        if(!response.ok){throw new Error('Network response was not ok ' + response.statusText);}
        return response.json();
    })
    .then(data =>
    {
        let tableBodyHTML = "";
        let index = 1;

        data.connectionConfigurations.forEach(item =>
        {
            tableBodyHTML += '<tr>';
            tableBodyHTML += '<td><button id="edit-connection-configuration-ID-' + item.connectionConfigurationID + ' "type="submit" class="btn btn-outline-info">Edit</button></td>';
            tableBodyHTML += '<td><button id="connect-to-broker-with-ID-' + item.connectionConfigurationID + ' "type="submit" class="btn btn-outline-theme">Connect</button></td>';
            tableBodyHTML += '<td><button id="start-broker-server-with-ID-' + item.connectionConfigurationID + ' "type="submit" class="btn btn-outline-theme">Start Server</button></td>';
            tableBodyHTML += '<td>' + index + '</td>';
            tableBodyHTML += '<td>' + item.dataSourceId + '</td>';
            tableBodyHTML += '<td>' + item.dataSourceType + '</td>';

            if(item.topic == null)
            {
                tableBodyHTML += '<td></td>';
            }
            else
            {
                tableBodyHTML += '<td>' + item.topic + '</td>';
            }

            if(item.clientId == null)
            {
                tableBodyHTML += '<td></td>';
            }
            else
            {
                tableBodyHTML += '<td>' + item.clientId + '</td>';
            }

            tableBodyHTML += '<td><button id="delete-connection-configuration-ID-' + item.connectionConfigurationID + ' "type="submit" class="btn btn-outline-danger">Delete</button></td>';
            tableBodyHTML += '</tr>';
            index++;
        });

        $('#connection-configurations-summaries-body').html(tableBodyHTML);
        handleRenderTableData();

        $('body').on('click', '#add-connection-configuration-form-modal-button', function(e)
        {
            e.preventDefault();
            $('#add-connection-configuration-form-modal').modal('show');
        });


        $('body').on('click', '[id^="edit-connection-configuration-ID-"]', function(e)
        {
            e.preventDefault();
            let connectionConfigurationIDToUse = $(this).prop("id").substring("edit-connection-configuration-ID-".length);
            $("#inputedit-connectionConfigurationID").val();
            $("#inputedit-dataSourceId").val();
            $("#inputedit-dataSourceType").val();
            $("#inputedit-apiUrl").val();
            $("#inputedit-apiKey").val();
            $("#inputedit-httpMethod").val();
            $("#inputedit-brokerUrl").val();
            $("#inputedit-brokerPort").val();
            $("#inputedit-topic").val();
            $("#inputedit-clientId").val();
            $("#inputedit-qualityOfServiceLevel").val();
            $("#inputedit-username").val();
            $("#inputedit-password").val();
            orionCommon.makeGetAJAXCall('http://localhost:8080/wapi/v1/connection-configurations/details/' + connectionConfigurationIDToUse, connectionConfigurations.processConnectionConfigurationEditModalPopulation);
        });


        $('body').on('click', '[id^="connect-to-broker-with-ID-"]', function(e)
        {
            e.preventDefault();
            let connectionConfigurationIDToUse = $(this).prop("id").substring("connect-to-broker-with-ID-".length);
            orionCommon.makeGetAJAXCall('http://localhost:8080/wapi/v1/connection-configurations/MQTT/connections/' + connectionConfigurationIDToUse, connectionConfigurations.processConnectionToBroker);
        });


        $('body').on('click', '[id^="start-broker-server-with-ID-"]', function(e)
        {
            e.preventDefault();
            let connectionConfigurationIDToUse = $(this).prop("id").substring("start-broker-server-with-ID-".length);
            orionCommon.makeGetAJAXCall('http://localhost:8080/wapi/v1/connection-configurations/MQTT/servers/starts/' + connectionConfigurationIDToUse, connectionConfigurations.processBrokerServerStart);
        });


        $('body').on('click', '[id^="delete-connection-configuration-ID-"]', function(e)
        {
            e.preventDefault();
            let connectionConfigurationIDToUse = $(this).prop("id").substring("delete-connection-configuration-ID-".length);
            orionCommon.makeDeleteAJAXCall('http://localhost:8080/wapi/v1/connection-configurations?connectionConfigurationID=' + connectionConfigurationIDToUse, connectionConfigurations.processConnectionConfigurationDeletion);
        });
    })
    .catch(error =>{document.getElementById("connection-configurations-summaries-body").innerHTML = 'Failed to load data:' + error;});
}


let connectionConfigurations =
{
    processConnectionConfigurationAddition : function(jsonResponse)
    {
        $(".close-modal-button").click();
        $("#refresh-connection-configurations-table-button").click();
    },


    processConnectionConfigurationAdditionViaJSON : function(jsonResponse)
    {
        //alert(jsonResponse);
        $(".close-modal-button").click();
        $("#refresh-connection-configurations-table-button").click();
    },


    processConnectionConfigurationEditModalPopulation : function(data)
    {
        $("#inputedit-connectionConfigurationID").val(data.connectionConfiguration.connectionConfigurationID);
        $("#inputedit-dataSourceId").val(data.connectionConfiguration.dataSourceId);
        $("#inputedit-dataSourceType").val(data.connectionConfiguration.dataSourceType);
        //$("#inputedit-apiUrl").val(data.connectionConfiguration.apiUrl);
        //$("#inputedit-apiKey").val(data.connectionConfiguration.apiKey);
        //$("#inputedit-httpMethod").val(data.connectionConfiguration.httpMethod);
        $("#inputedit-brokerUrl").val(data.connectionConfiguration.brokerUrl);
        $("#inputedit-brokerPort").val(data.connectionConfiguration.brokerPort);
        $("#inputedit-topic").val(data.connectionConfiguration.topic);
        $("#inputedit-clientId").val(data.connectionConfiguration.clientId);
        $("#inputedit-qualityOfServiceLevel").val(data.connectionConfiguration.qualityOfServiceLevel);
        //$("#inputedit-username").val(data.connectionConfiguration.username);
        //$("#inputedit-password").val(data.connectionConfiguration.password);
        $('#edit-connection-configuration-form-modal').modal('show');
    },


    processConnectionConfigurationUpdate : function(data)
    {
        $(".close-modal-button").click();
        $("#refresh-connection-configurations-table-button").click();
    },


    processConnectionConfigurationDeletion : function(data)
    {
        alert("Deleted");
        $("#refresh-connection-configurations-table-button").click();
    },


    processBrokerServerStart : function(data)
    {
        alert(data.mqttServerStartStatus);
        $("#refresh-connection-configurations-table-button").click();
    },


    processConnectionToBroker : function(data)
    {
        alert(data.clientConnectionToMQTTServerStatus);
        $("#refresh-connection-configurations-table-button").click();
    },


    processDisconnectionFromMQTTBrokerServer : function(data)
    {
        alert(data.clientConnectionToMQTTServerStatus);
        $(".close-modal-button").click();
    },


    processScheduleTestMQTTPublishes : function(data)
    {
        alert(data.hasErrors);
    },


    processScheduleTestMQTTPublishesStop : function(data)
    {
        alert(data.hasErrors);
    }
}