
export const App = {

    urlParams: function () {
        let search = location.search.substring(1), urlParams;

        if (search.includes('psid')) {
            urlParams = JSON.parse('{"' + search.replace(/&/g, '","').replace(/=/g, '":"') + '"}', function (key, value) { return key === "" ? value : decodeURIComponent(value) });
            return urlParams;
        } else {
            search = this.decrypt(search.replace("%3D", "="));
            urlParams = JSON.parse('{"' + search.replace(/&/g, '","').replace(/=/g, '":"') + '"}', function (key, value) { return key === "" ? value : decodeURIComponent(value) });
            return urlParams;
        }
    },
    getTypeOfPattern: function (ids) {
        let typeOfPattern, id, psid = ids['psid'], bpid = ids['bpid'], sdid = ids['sdid'];

        if (sdid != null && bpid != null) {
            typeOfPattern = "solution_design";
            id = sdid;
        }
        else {
            if (ids['bpid']) {
                typeOfPattern = "base_pattern";
                id = bpid;
            }
            if (ids['sdid']) {
                typeOfPattern = "solution_design";
                id = sdid;
            }
        }
        return { typeOfPattern, id };
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
            success: function (res) {
                console.log(res);
                if (renderFunction) {
                    renderFunction(res.data, rparams);
                }
            },
            error: function (err) {
                console.log(err);
                if (errFunction) {
                    errFunction(eparams);
                }
            }
        });
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
    toastMsg: function (msg, type, place, time) {
        let toast;
        switch (type) {
            case 'success': {
                toast = `<div class="alert alert-success" role="alert">
                            <div class="row vertical-align">
                                <div class="col-1 text-center">
                                    <i class="fa fa-check fa-2x"></i> 
                                </div>
                            <div class="col-11">
                                ${msg}
                            </div>
                        </div>`;
            }; break;
            case 'failed': {
                toast = `<div class="alert alert-danger" role="alert">
                            <div class="row vertical-align">
                                <div class="col-1 text-center">
                                    <i class="fa fa-exclamation-triangle fa-2x"></i> 
                                </div>
                            <div class="col-11">
                                ${msg}
                            </div>
                        </div>`;
            }; break;
            case 'info': {
                toast = `<div class="alert alert-info" role="alert">
                            <div class="row vertical-align">
                                <div class="col-1 text-center">
                                    <i class="fa fa-info fa-2x"></i> 
                                </div>
                            <div class="col-11">
                                ${msg}
                            </div>
                        </div>`;
            }; break;
            default: {
                toast = `<div class="text-center alert alert-secondary" role="alert">
                            ${msg}
                        </div>`;
            }
        }
        if (place) {
            $(place).html(toast);
            $(place).show();
            if (time) {
                setTimeout(function () {
                    $(place).fadeOut(800);
                }, 3000);
            }
        } else {
            alert(msg);
        }
    },
    addToastMsgDiv: function (place) {
        $('.toastMsg').remove();
        let div = `<div class="toastMsg mx-2 mt-2"></div>`;
        $(place).after(div);
        return "toastMsg";
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
};
window.App = App;

// $(document).ready(function () {
// let userRole = $('.userRoleName').text();
// console.log(userRole)
// if (userRole != 'Administrator' || userRole != 'Deployer') {
//     document.querySelector('.geMenubar').removeChild(document.querySelector('.geMenubar').children[6]);
// }
// });