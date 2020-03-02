
export const App = {
    userRole: $('.userRoleName').text(),
    userName: $('.userName').text(),
    urlParams: function () {
        let search = location.search.substring(1), urlParams;

        if (search.includes('psid') || search.includes('tagid')) {
            try {
                urlParams = JSON.parse('{"' + search.replace(/&/g, '","').replace(/=/g, '":"') + '"}', function (key, value) { return key === "" ? value : decodeURIComponent(value) });
                return urlParams;
            } catch (error) {
                console.log(error);
                return {};
            }
        } else {
            search = this.decrypt(search.replace("%3D", "="));
            try {
                urlParams = JSON.parse('{"' + search.replace(/&/g, '","').replace(/=/g, '":"') + '"}', function (key, value) { return key === "" ? value : decodeURIComponent(value) });
                return urlParams;
            } catch (error) {
                console.log(error);
                return {};
            }

        }
    },
    getTypeOfPattern: function (ids) {
        let typeOfPattern, id, psid = ids['psid'], bpid = ids['bpid'], sdid = ids['sdid'];

        if (sdid != null && bpid != null) {
            typeOfPattern = "solution_design";
            id = sdid;
            return { typeOfPattern, id };
        }
        else {
            if (ids['bpid']) {
                typeOfPattern = "base_pattern";
                id = bpid;
                return { typeOfPattern, id };
            }
            if (ids['sdid']) {
                typeOfPattern = "solution_design";
                id = sdid;
                if (bpid) {
                    return { typeOfPattern, id, bpid };
                } else {
                    return { typeOfPattern, id };
                }

            }
        }
    },
    encrypt: function (enc) {
        return window.btoa(enc);
    },
    decrypt: function (dec) {
        return window.atob(dec);
    },
    genericFetch: function (url, method, data, renderFunction, rparams, errFunction, eparams) {
        // console.log("Fetching...");
        $.ajax({
            method: method,
            url: url,
            data: data,
            // async: false,
            cache: false,
            success: function (res) {
                console.log(res);
                if (renderFunction) {
                    renderFunction(res.data, rparams, res);
                    App.clearLoader();
                }
            },
            error: function (err) {
                console.log(err);
                if (errFunction) {
                    errFunction(eparams, err);
                }
            }
        });
    },
    outputResponse: function (data) {
        console.log(data);
    },
    loader: function (place) {
        const load = `<div class="loader"><center><svg width="65" height="65" viewBox="0 0 38 38" xmlns="http://www.w3.org/2000/svg" stroke="#4f4c4c">
	    <g fill="none" fill-rule="evenodd">
	        <g transform="translate(1 1)" stroke-width="2">
	            <circle stroke-opacity=".5" cx="18" cy="18" r="18"/>
	            <path d="M36 18c0-9.94-8.06-18-18-18">
	                <animateTransform
	                    attributeName="transform"
	                    type="rotate"
	                    from="0 18 18"
	                    to="360 18 18"
	                    dur="1s"
	                    repeatCount="indefinite"/>
	            </path>
	    		</g>
	    	</g>
            </svg></center></div>`;
        $(place).append(load);
    },
    clearLoader() {
        const loader = $(`.loader`);
        if (loader) { loader.remove(); }
    },
    modalFormResponse: function (data) {
        // console.log(data);
        if (data && data.message == "success") {
            $('.modalBody').addClass('alert alert-success m-2');
            $('.modalBody').html(`<b>Success!</b> ${data.info.toUpperCase()}`);
            $('#closeBtn').hide();
            $('#saveChangesBtn').hide();
            setTimeout(function () {
                window.location.reload();
            }, 1500);
        } else {
            $('.modalBody').addClass('alert alert-danger m-2');
            $('.modalBody').html(`<b>Failed</b> : ${data.info.toUpperCase()}`);
            $('#closeBtn').hide();
            $('#saveChangesBtn').hide();
            setTimeout(function () {
                window.location.reload();
            }, 3000);
        }
    },
    isEmpty: function (data) {
        if (typeof (data) == 'number' || typeof (data) == 'boolean') { return false; }
        if (typeof (data) == 'undefined' || data === null) { return true; }
        if (typeof (data.length) != 'undefined') { return data.length == 0; }
        let count = 0;
        for (let i in data) {
            if (data.hasOwnProperty(i)) { count++; }
        }
        return count == 0;
    },
    infoIconSvg: `<svg aria-hidden="true" class="svg-icon iconLightbulb" width="18" height="18" viewBox="0 0 18 18"><path d="M9.5.5a.5.5 0 0 0-1 0v.25a.5.5 0 0 0 1 0V.5zm5.6 2.1a.5.5 0 0 0-.7-.7l-.25.25a.5.5 0 0 0 .7.7l.25-.25zM1 7.5c0-.28.22-.5.5-.5H2a.5.5 0 0 1 0 1h-.5a.5.5 0 0 1-.5-.5zm14.5 0c0-.28.22-.5.5-.5h.5a.5.5 0 0 1 0 1H16a.5.5 0 0 1-.5-.5zM2.9 1.9c.2-.2.5-.2.7 0l.25.25a.5.5 0 1 1-.7.7L2.9 2.6a.5.5 0 0 1 0-.7z" fill-opacity=".4"></path><path opacity=".4" d="M7 16h4v1a1 1 0 0 1-1 1H8a1 1 0 0 1-1-1v-1z" fill="#3F3F3F"></path><path d="M15 8a6 6 0 0 1-3.5 5.46V14a1 1 0 0 1-1 1h-3a1 1 0 0 1-1-1v-.54A6 6 0 1 1 15 8zm-4.15-3.85a.5.5 0 0 0-.7.7l2 2a.5.5 0 0 0 .7-.7l-2-2z" fill="#FFC166"></path></svg>`,
    successIconSvg: `<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 367.8 367.8"><path d="M183.9 0c101.6 0 183.9 82.3 183.9 183.9s-82.3 183.9-183.9 183.9S0 285.5 0 183.9l0 0C-0.3 82.6 81.6 0.3 182.9 0 183.2 0 183.6 0 183.9 0z" fill="#3BB54A"/><polygon points="285.8 133.2 155.2 263.8 82 191.2 111.8 162 155.2 204.8 256 104 " fill="#D4E1F4"/></svg>`,
    failedIconSvg: `<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 368 368"><path d="M314.1 54.1c71.8 71.8 71.7 188.3-0.1 260.1s-188.3 71.7-260.1-0.1c-71.7-71.8-71.7-188.2 0-260 71.4-71.8 187.5-72.2 259.3-0.8C313.5 53.6 313.8 53.9 314.1 54.1z" fill="#D7443E"/><polygon points="275.4 124.7 215.9 184.2 275.4 243.8 243.6 275.7 184 216.1 124.5 275.7 92.6 243.8 152.1 184.2 92.6 124.7 124.5 92.8 184 152.4 243.6 92.8 " fill="#D4E1F4"/></svg>`,
    toastMsg: function (msg, type, place, time) {
        let toast;
        switch (type) {
            case 'success': { // <i class="fa fa-check fa-2x"></i>
                toast = `<div class="alert alert-success text-center m-0 mx-auto" role="alert" style="max-width: 400px;">
                            <div class="row vertical-align">
                                <div class="col-2 text-center">
                                    ${this.successIconSvg}
                                </div>
                            <div class="col-10 p-1">
                                ${msg}
                            </div>
                        </div>`;
            }; break;
            case 'failed': { //<i class="fa fa-exclamation-triangle fa-2x"></i>
                toast = `<div class="alert alert-danger text-center my-0 mx-auto" role="alert" style="max-width: 400px;">
                            <div class="row vertical-align">
                                <div class="col-2 text-center">
                                     ${this.failedIconSvg}
                                </div>
                            <div class="col-10 p-1">
                                ${msg}
                            </div>
                        </div>`;
            }; break;
            case 'info': { //<i class="fa fa-info fa-2x" style="width:25px;height:25px;"></i>
                toast = `<div class="alert alert-info text-center my-0 mx-auto" role="alert" style="max-width: 400px;">
                            <div class="row vertical-align">
                                <div class="col-2 text-center">
                                     ${this.infoIconSvg}
                                </div>
                            <div class="col-10 p-1">
                                ${msg}
                            </div>
                        </div>`;
            }; break;
            default: {
                toast = `<div class="text-center alert bg-light " role="alert">
                            ${msg}
                        </div>`;
            }
        }
        if (place) {
            $(place).html(toast);
            $(place).show();
            if (time) {
                if (time > 0) { time = time } else { time = true; }
                setTimeout(function () {
                    $(place).fadeOut(800);
                }, 3000);
            }
        } else {
            this.toastAlert(msg);
            // alert(msg);
        }
    },
    addToastMsgDiv: function (place) {
        $('.toastMsg').remove();
        let div = `<div class="toastMsg mx-2 mt-2"></div>`;
        $(place).after(div);
        return "toastMsg";
    },
    customToastMessage: function (msg, type, place, isTrue) {
        App.toastMsg(`${msg}`, type, place, isTrue);
    },
    toastAlert: function (msg) {
        let toast = `<div aria-live="polite" aria-atomic="true" style="position: relative; min-height: 200px;">
            <div class="toast" style="position: absolute; top: 0; right: 0;">
              <div class="toast-header">
                <strong class="mr-auto">Notification</strong>
                <button type="button" class="ml-2 mb-1 close" data-dismiss="toast" aria-label="Close">
                  <span aria-hidden="true">&times;</span>
                </button>
              </div>
              <div class="toast-body">
                ${msg}
              </div>
            </div>
          </div>`;
        $('.container-fluid').append(toast);
    },
    clearInput(place) {
        $(place).val('');
    },
    dataNotFound(place) {
        App.toastMsg('Data Not Found', "failed", place, false);
    },
    svgToPng(data) {
        var svgString = data;
        var dataURI = "data:image/svg+xml;base64," + window.btoa(svgString);
        var ctx = canvas.getContext("2d");
        var image = new Image();
        image.onload = function () {
            ctx.drawImage(image, 0, 0, image.width, image.height);
        }
        image.src = dataURI;
        image.onload = function () {
            ctx.drawImage(image, 0, 0, image.width, image.height);
            // toBlob(callback, mimeType, qualityArgument);
            canvas.toBlob(function (blob) {
                var newImg = document.createElement("img"),
                    url = URL.createObjectURL(blob);
                newImg.onload = function () {
                    URL.revokeObjectURL(url);
                };
                newImg.src = url;
            }, "image/jpeg", 0.8);
            var event = new MouseEvent('click', {
                'view': window,
                'bubbles': true,
                'cancelable': true
            });
            console.log(canvas.toDataURL('image/png'));
            // console.log(data);
            var a = document.createElement('a');
            a.download = filename;
            a.href = canvas.toDataURL('image/png');
            document.body.appendChild(a);
            a.click();
        }
    },
    xmlToJson(xml) { // Need to pass Dom parsed xml string i.e new DOMParser().parseFromString(xml, 'text/xml') 
        // Create the return object
        var obj = {};
        if (xml.nodeType == 1) { // element
            // do attributes
            if (xml.attributes.length > 0) {
                obj["attributes"] = {};
                for (var j = 0; j < xml.attributes.length; j++) {
                    var attribute = xml.attributes.item(j);
                    obj["attributes"][attribute.nodeName] = attribute.nodeValue;
                }
            }
        } else if (xml.nodeType == 3) { // text
            obj = xml.nodeValue;
        }
        // do children
        if (xml.hasChildNodes()) {
            for (var i = 0; i < xml.childNodes.length; i++) {
                var item = xml.childNodes.item(i);
                var nodeName = item.nodeName;
                if (typeof (obj[nodeName]) == "undefined") {
                    obj[nodeName] = this.xmlToJson(item);
                } else {
                    if (typeof (obj[nodeName].push) == "undefined") {
                        var old = obj[nodeName];
                        obj[nodeName] = [];
                        obj[nodeName].push(old);
                    }
                    obj[nodeName].push(this.xmlToJson(item));
                }
            }
        }
        return obj;
    },
    getUniqueArray(array) {
        let uniqueArray = [], removedSpace = [], arrWithoutEmptyData = [];
        for (let i = 0; i < array.length; i++) {
            if (uniqueArray.indexOf(array[i]) === -1) {
                uniqueArray.push(array[i].replace(/[^\w\s]/gi, ''));
            }
        }
        for (let i = 0; i < uniqueArray.length; i++) { removedSpace.push(uniqueArray[i].trim(' ')); }
        for (let i = 0; i < removedSpace.length; i++) {
            if (removedSpace[i] != "") { arrWithoutEmptyData.push(removedSpace[i].toLowerCase()); }
        }
        return arrWithoutEmptyData;
    },
    checkForSpecialChar: function (str) {
        let specialChars = "<>@!#$%^&*()_+[]{}?:;|'\"\\,./~`-=";
        console.log(str)
        for (let j = 0; j < specialChars.length; j++) {
            if (str.indexOf(specialChars[j]) > -1) {
                return true
            }
        }
    },
};
window.App = App;