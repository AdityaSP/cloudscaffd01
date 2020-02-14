import { App } from './app.js';

var urldata = App.urlParams(true);
console.log(urldata);
var psid = urldata['psid'];
var sdid = urldata['sdid'];
var bpid, isSolutionDesignApproved, isAdmin = false, isDeployer = false, isApprover = false, isPlanner = false, idToBeApproved, xml;

$(function () {
    $('.py-3').contents().filter(function () {
        return this.nodeType === 3;
    }).remove();

    if (urldata['bpid']) { bpid = urldata['bpid'] };
    let userRole = $('.userRoleName').text();
    let userName = $('.userName').text();

    // Fetch and Rendering Problem Statement
    App.loader(".probStatementForm");
    App.genericFetch('getProblemStatements', "POST", { "psid": psid }, renderProblemStmt, psid);

    $('.deploy').attr("disabled", true);
    $('.approve').attr("disabled", true);

    // Fetch and Rendering Solution Design
    if (sdid) {
        App.loader(".solutionDesignForm");
        App.genericFetch('getSolutionDesign', "POST", { "sdid": sdid }, renderSolutionDesign, sdid);
    }

    // Fetch and Rendering Base Pattern if bpid exits
    if (bpid) {
        // App.loader(".basePatternForm");
        App.genericFetch('getBasePattern', "POST", { "bpid": bpid }, renderBasePattern, bpid);
    } else {
        $('.basePatternForm').hide();
    }

    switch (userRole) {
        case "Administrator": {
            isAdmin = true;
            $('.approve').attr("disabled", true);
            $('.deploy').attr("disabled", true);
        }; break;
        case "Deployer": {
            isDeployer = true;
            $('.approve').attr("disabled", true);
        }; break;
        case "Approver": {
            isApprover = true;
            $('.deploy').attr("disabled", true);
            $('.edit').attr("disabled", true);
        }; break;
        case "Planner": {
            isPlanner = true;
            $('.approve').attr("disabled", true);
            $('.deploy').attr("disabled", true);
        }; break;
        default: break;
    }

    console.log(`Role: ${userRole}, isSolutionDesignApproved: ${isSolutionDesignApproved}, isApprover: ${isApprover}, isDeployer: ${isDeployer}`);

    $('.deploy').on('click', function (e) {
        let data = App.xmlToJson(new DOMParser().parseFromString(xml, 'text/xml'));
        console.log(data);// console.log(xml);
        // App.genericFetch("deploySolutionDesign", "POST", data, "", "", "", "");

        // After successfull Deployment Change status to 'Deployed-Successful'
        // App.genericFetch('approveSolutionDesign', "POST", urldata, reloadPage, sdid, "", ""); // urlData includes status

    });

    // IF approved display only  deploy and edit
    $('.approve').on('click', function (e) {
        bootbox.confirm({
            title: "Solution Design Approval",
            message: "Please confirm to approve design",
            buttons: {
                cancel: {
                    label: '<i class="fa fa-times"></i> Cancel'
                },
                confirm: {
                    label: '<i class="fa fa-check"></i> Confirm'
                }
            },
            callback: function (result) {
                if (result) {
                    App.genericFetch('approveSolutionDesign', "POST", urldata, reloadPage, sdid, "", "");
                }
            }
        });
    });

    $('.deleteSD').on('click', function (e) {
        bootbox.confirm({
            title: "Delete Solution Design",
            message: "Are sure you want to delete?",
            buttons: {
                cancel: {
                    label: '<i class="fa fa-times"></i> Cancel'
                },
                confirm: {
                    label: '<i class="fa fa-check"></i> Confirm'
                }
            },
            callback: function (result) {
                if (result) {
                    let queryStr = `psid=${psid}`;
                    App.genericFetch('deleteSolutionDesign', "POST", { "sdid": sdid }, "", "", "", "");
                    $('.solutionDesignForm').hide(); $('.svgDiv').hide();
                    App.toastMsg('Go back to create a new solution design', 'info', '.toastMsg')
                    $('.edit').attr("disabled", true);
                    $('.deploy').attr("disabled", true);
                    $('.title').text("Problem Statement");
                    urldata["sdid"] = null
                } else {
                    console.log(result);
                }
            }
        });
    });

    $('.edit').on('click', function (evt) {
        let urlParam;
        if (psid != null && bpid != null && sdid != null) {
            urlParam = `psid=${psid}&bpid=${bpid}&sdid=${sdid}`;
        } else if (psid != null && bpid != null) {
            urlParam = `psid=${psid}&bpid=${bpid}`;
        } else if (psid != null && sdid != null) {
            urlParam = `psid=${psid}&sdid=${sdid}`;
        } else {
            urlParam = `psid=${psid}`;
        }
        window.location.href = `graphEditor?${window.btoa(urlParam)}`
    });
});

function reloadPage(data, id) {
    App.toastMsg(`${id} : Design Approved`, 'success', '.toastMsg', true);
    $('.approve').hide();
}

function renderProblemStmt(problemList, psid) {
    for (let i = 0; i < problemList.length; i++) {
        if (psid == problemList[i].id) {
            $('.probStatement').text(`PS ${problemList[i].id} : ${problemList[i].problemStatement}`);
            $('.probStatementDescription').text(problemList[i].problemDescription);
        }
    }
}

function renderBasePattern(basePattern, bpid) {
    for (let i = 0; i < basePattern.length; i++) {
        if (bpid == basePattern[i].id) {
            $('.basePattern').text(`BP ${basePattern[i].id} : ${basePattern[i].baseName}`);
            $('.basePatternDescription').text(basePattern[i].baseDescription);
            if (basePattern[i].svg) {
                // if (basePattern[i].status == 'approved') {
                $('.BPsvgDiv').append(basePattern[i].svg);
                $('.BPsvgDiv > svg').attr({
                    "width": "800px",
                    "height": "550px"
                });
                // } else {
                //     $('.viewBpImage').hide();
                // }
            } else {
                $('.viewBpImage').hide();
            }
        }
    }
}

function renderSolutionDesign(solutionDesign, sdid) {
    for (let i = 0; i < solutionDesign.length; i++) {
        if (sdid == solutionDesign[i].id) {
            $('.solutionDesign').text(`SD ${solutionDesign[i].id} : ${solutionDesign[i].solutionDesignName}`);
            $('.solutionDesignDescription').text(solutionDesign[i].solutionDesignDesc);

            if (solutionDesign[i].xml) { xml = solutionDesign[i].xml };

            if (solutionDesign[i].svg) {
                psid = solutionDesign[i].psid;
                if (solutionDesign[i].bpid) { bpid = solutionDesign[i].bpid }

                // $("#solutionDesignImg")[0].src = solutionDesign[i].png;
                //$("#solutionDesignImg")[0].srcset = solutionDesign[i].svg;
                $('.svgDiv').append(solutionDesign[i].svg);
                $('svg').attr({
                    "min-width": "100px",
                    "min-height": "100px"
                });

                //Check If Solution Design is apporoved or not
                isSolutionDesignApproved = solutionDesign[i].status;
                checkImageAproval(isSolutionDesignApproved, solutionDesign[i].id);
            } else {
                // $('.edit').attr("disabled", true);
                // $("#solutionDesignImg")[0].alt = "Image Not Found";
                App.toastMsg('No Design Created', 'failed', '.toastMsg');
                $('.svgDiv').hide();
                // $('.svg').attr('src', '../static/graphEditor/images/No_image_available.svg.svg');
            }
        }
    }
}

function checkImageAproval(isSolutionDesignApproved, id) {
    console.log(isSolutionDesignApproved);

    if (isSolutionDesignApproved == "approved") {
        $('.approve').hide();
        // $('.edit').attr("disabled", false);
    } else {
        App.toastMsg("Solution Design is not Approved", 'failed', '.toastMsg');

        if (isApprover) {
            $('.approve').attr("disabled", false);
            idToBeApproved = id;
        } else {
            App.toastMsg("Solution Design is not Approved", 'failed', '.toastMsg');
        }
    }

    if (isDeployer || isAdmin) {
        if (isSolutionDesignApproved == "approved") {
            $('.deploy').attr("disabled", false);
        }
        else {
            console.log("cannot deploy");
        }

    }
}