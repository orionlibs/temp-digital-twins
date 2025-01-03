let orionCommon =
{
    makeGetAJAXCall : function(url, callbackSuccessFunction, callbackFailFunction)
    {
        fetch(url,
        {
            method: 'GET',
            cache: "no-cache",
            mode: "cors",//cors, no-cors, same-origin
            credentials: "include",//include, same-origin, omit
            headers:
            {
                'Content-Type': 'application/json',
                'X-Xsrf-Token': orionCommon.getCookie('XSRF-TOKEN')
            }
        })
        .then(response =>
        {
            if(!response.ok)
            {
                if(callbackFailFunction)
                {
                    callbackFailFunction(response);
                }
                else
                {
                    throw new Error('Error ' + response.statusText);
                }
            }

            return response.json();
        })
        .then(jsonResponse =>
        {
            if(callbackSuccessFunction)
            {
                callbackSuccessFunction(jsonResponse);
            }
        })
        .catch(error =>
        {
            if(callbackFailFunction)
            {
                callbackFailFunction(error);
            }
            else
            {
                alert('There has been a problem with your fetch operation:' + error);
            }
        });
    },


    makePostAJAXCall : function(url, dataToSend, callbackSuccessFunction, callbackFailFunction)
    {
        fetch(url,
        {
            method: 'POST',
            cache: "no-cache",
            mode: "cors",//cors, no-cors, same-origin
            credentials: "include",//include, same-origin, omit
            headers:
            {
                'Content-Type': 'application/json',
                'X-Xsrf-Token': orionCommon.getCookie('XSRF-TOKEN')
            },
            body: JSON.stringify(dataToSend)
        })
        .then(response =>
        {
            if(!response.ok)
            {
                if(callbackFailFunction)
                {
                    callbackFailFunction(response);
                }
                else
                {
                    throw new Error('Error ' + response.statusText);
                }
            }

            return response.json();
        })
        .then(jsonResponse =>
        {
            if(callbackSuccessFunction)
            {
                callbackSuccessFunction(jsonResponse);
            }
        })
        .catch(error =>
        {
            if(callbackFailFunction)
            {
                callbackFailFunction(error);
            }
            else
            {
                alert('There has been a problem with your fetch operation:' + error);
            }
        });
    },


    makePutAJAXCall : function(url, dataToSend, callbackSuccessFunction, callbackFailFunction)
    {
        fetch(url,
        {
            method: 'PUT',
            cache: "no-cache",
            mode: "cors",//cors, no-cors, same-origin
            credentials: "include",//include, same-origin, omit
            headers:
            {
                'Content-Type': 'application/json',
                'X-Xsrf-Token': orionCommon.getCookie('XSRF-TOKEN')
            },
            body: JSON.stringify(dataToSend)
        })
        .then(response =>
        {
            if(!response.ok)
            {
                if(callbackFailFunction)
                {
                    callbackFailFunction(response);
                }
                else
                {
                    throw new Error('Error ' + response.statusText);
                }
            }

            return response.json();
        })
        .then(jsonResponse =>
        {
            if(callbackSuccessFunction)
            {
                callbackSuccessFunction(jsonResponse);
            }
        })
        .catch(error =>
        {
            if(callbackFailFunction)
            {
                callbackFailFunction(error);
            }
            else
            {
                alert('There has been a problem with your fetch operation:' + error);
            }
        });
    },


    makeDeleteAJAXCall : function(url, callbackSuccessFunction, callbackFailFunction)
    {
        fetch(url,
        {
            method: 'DELETE',
            cache: "no-cache",
            mode: "cors",//cors, no-cors, same-origin
            credentials: "include",//include, same-origin, omit
            headers:
            {
                'Content-Type': 'application/json',
                'X-Xsrf-Token': orionCommon.getCookie('XSRF-TOKEN')
            }
        })
        .then(response =>
        {
            if(!response.ok)
            {
                if(callbackFailFunction)
                {
                    callbackFailFunction(response);
                }
                else
                {
                    throw new Error('Error ' + response.statusText);
                }
            }

            return response.json();
        })
        .then(jsonResponse =>
        {
            if(callbackSuccessFunction)
            {
                callbackSuccessFunction(jsonResponse);
            }
        })
        .catch(error =>
        {
            if(callbackFailFunction)
            {
                callbackFailFunction(error);
            }
            else
            {
                alert('There has been a problem with your fetch operation:' + error);
            }
        });
    },


    uploadFile : async function(URL, fileInputID, callbackFunction)
    {
        const fileInput = document.getElementById(fileInputID);
        const files = fileInput.files;
        const formData = new FormData();

        for(let file of files)
        {
            formData.append('files[]', file);
        }

        try
        {
            const response = await fetch(URL,
            {
                method: 'POST',
                body: formData,
            });

            if(response.ok)
            {
                const result = await response.json();

                if(typeof callbackFunction === "function")
                {
                    callbackFunction(result);
                }
            }
            else
            {
                if(typeof callbackFunction === "function")
                {
                    callbackFunction(response.statusText);
                }
            }
        }
        catch(error)
        {
            if(typeof callbackFunction === "function")
            {
                callbackFunction(error);
            }
        }
    },


    getCookie : function(name)
    {
        const cookieValue = document.cookie
            .split('; ')
            .find(cookie => cookie.startsWith(name + '='))
            ?.split('=')[1];
        return cookieValue ? decodeURIComponent(cookieValue) : null;
    },


    fetchComponentData : function(url, elementID)
    {
        fetch(url)
        .then(response =>
        {
            if(!response.ok)
            {
                throw new Error('Network response was not ok ' + response.statusText);
            }

            return response.json();
        })
        .then(data =>
        {
            orionCommon.updateComponent(elementID, data);
        })
        .catch(error =>
        {
            document.getElementById(elementID).innerHTML = 'Failed to load data:' + error;
        });
    },


    updateComponent : function(elementID, data)
    {
        const element = document.getElementById(elementID);
        element.innerHTML = data;
    }
};