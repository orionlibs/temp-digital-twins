$(document).ready(function ()
{
    $('body').on('click', '#refresh-connection-configurations-table-button', function(e)
    {
        window.location.reload();
    });


    $('body').on('click', '#add-connection-configuration-button', function(e)
    {
        const dataToSend =
        {
            dataSourceId: $("#input-dataSourceId").val(),
            dataSourceType: $("#input-dataSourceType").val(),
            apiUrl: $("#input-apiUrl").val(),
            apiKey: $("#input-apiKey").val(),
            httpMethod: $("#input-httpMethod").val(),
            brokerUrl: $("#input-brokerUrl").val(),
            topic: $("#input-topic").val(),
            clientId: $("#input-clientId").val(),
            username: $("#input-username").val(),
            password: $("#input-password").val()
        };

        orionCommon.makePostAJAXCall('http://localhost:8080/wapi/v1/connection-configurations', dataToSend, connectionConfigurations.processSuccessfulConnectionConfigurationAddition);
    });


    populateTableOfConnectionConfigurations();


    $('body').on('click', '#update-connection-configuration-button', function(e)
    {
        const dataToSend =
        {
            connectionConfigurationID: $("#inputedit-connectionConfigurationID").val(),
            dataSourceId: $("#inputedit-dataSourceId").val(),
            dataSourceType: $("#inputedit-dataSourceType").val(),
            apiUrl: $("#inputedit-apiUrl").val(),
            apiKey: $("#inputedit-apiKey").val(),
            httpMethod: $("#inputedit-httpMethod").val(),
            brokerUrl: $("#inputedit-brokerUrl").val(),
            topic: $("#inputedit-topic").val(),
            clientId: $("#inputedit-clientId").val(),
            username: $("#inputedit-username").val(),
            password: $("#inputedit-password").val()
        };

        orionCommon.makePutAJAXCall('http://localhost:8080/wapi/v1/connection-configurations', dataToSend, connectionConfigurations.processSuccessfulConnectionConfigurationUpdate);
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
            tableBodyHTML += '<td><button id="edit-connection-configuration-ID-' + item.connectionConfigurationID + ' "type="submit" class="btn btn-outline-theme">Edit</button></td>';
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

        $('body').on('click', '#open-add-connection-configuration-form-modal-button', function(e)
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
            $("#inputedit-topic").val();
            $("#inputedit-clientId").val();
            $("#inputedit-username").val();
            $("#inputedit-password").val();
            orionCommon.makeGetAJAXCall('http://localhost:8080/wapi/v1/connection-configurations/details/' + connectionConfigurationIDToUse, connectionConfigurations.processSuccessfulConnectionConfigurationEditModalPopulation);
        });


        $('body').on('click', '[id^="delete-connection-configuration-ID-"]', function(e)
        {
            e.preventDefault();
            let connectionConfigurationIDToUse = $(this).prop("id").substring("delete-connection-configuration-ID-".length);
            orionCommon.makeDeleteAJAXCall('http://localhost:8080/wapi/v1/connection-configurations?connectionConfigurationID=' + connectionConfigurationIDToUse, connectionConfigurations.processSuccessfulConnectionConfigurationDeletion);
        });
    })
    .catch(error =>{document.getElementById("connection-configurations-summaries-body").innerHTML = 'Failed to load data:' + error;});
}


let connectionConfigurations =
{
    processSuccessfulConnectionConfigurationAddition : function(jsonResponse)
    {
        $(".close-modal-button").click();
        $("#refresh-connection-configurations-table-button").click();
    },


    processSuccessfulConnectionConfigurationEditModalPopulation : function(data)
    {
        $("#inputedit-connectionConfigurationID").val(data.connectionConfiguration.connectionConfigurationID);
        $("#inputedit-dataSourceId").val(data.connectionConfiguration.dataSourceId);
        $("#inputedit-dataSourceType").val(data.connectionConfiguration.dataSourceType);
        $("#inputedit-apiUrl").val(data.connectionConfiguration.apiUrl);
        $("#inputedit-apiKey").val(data.connectionConfiguration.apiKey);
        $("#inputedit-httpMethod").val(data.connectionConfiguration.httpMethod);
        $("#inputedit-brokerUrl").val(data.connectionConfiguration.brokerUrl);
        $("#inputedit-topic").val(data.connectionConfiguration.topic);
        $("#inputedit-clientId").val(data.connectionConfiguration.clientId);
        $("#inputedit-username").val(data.connectionConfiguration.username);
        $("#inputedit-password").val(data.connectionConfiguration.password);
        $('#edit-connection-configuration-form-modal').modal('show');
    },


    processSuccessfulConnectionConfigurationUpdate : function(data)
    {
        $(".close-modal-button").click();
        $("#refresh-connection-configurations-table-button").click();
    },


    processSuccessfulConnectionConfigurationDeletion : function(data)
    {
        alert("Deleted");
        $("#refresh-connection-configurations-table-button").click();
    }
}