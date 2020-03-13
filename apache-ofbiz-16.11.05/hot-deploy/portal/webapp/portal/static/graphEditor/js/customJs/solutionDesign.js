import { App } from './app.js';
// import { Deployment } from './solutionDesignDeploy.js';

var urldata = App.urlParams(true);
console.log(urldata);
var psid = urldata['psid'];
var sdid = urldata['sdid'];
var bpid, isSolutionDesignApproved, isAdmin = false, isDeployer = false, isApprover = false, isPlanner = false, idToBeApproved, xml;

$(function () {
    $('.py-3').contents().filter(function () {
        return this.nodeType === 3;
    }).remove();
    $('.toast').remove();

    if (!App.isEmpty(urldata)) {

        $('[data-toggle="tooltip"]').tooltip();

        if (urldata['bpid']) { bpid = urldata['bpid'] };
        let userRole = $('.userRoleName').text();
        let userName = $('.userName').text();

        // Fetch and Rendering Problem Statement
        App.loader(".probStatementForm");
        App.genericFetch('getProblemStatements', "POST", { "psid": psid }, renderProblemStmt, psid);

        $('.deploy').attr("disabled", true);
        $('.viewDeploymentSummaryBtn').hide();
        $('#proceedBtn').hide();
        $('.approve').attr("disabled", true);

        $('#viewDeploymentSummaryModal').on('hidden.bs.modal', function (e) {
            $('#proceedBtn').hide();
        });

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
                // $('.approve').attr("disabled", true);
                $('.approve').hide();
                $('.requestApprove').show(); //TODO: add to all roles
                $('.deploy').attr("disabled", true);
            }; break;
            case "Deployer": {
                isDeployer = true;
                $('.approve').attr("disabled", true);
                $('.edit').attr("disabled", true);
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
            confirmDeployAlertBox();
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
                        App.genericFetch('approveSolutionDesign', "POST", urldata, approveSolutionDesignStatus, sdid, "", "");
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
                        App.genericFetch('deleteSolutionDesign', "POST", { "sdid": sdid }, "", "", "", "");
                        $('.solutionDesignForm').hide(); $('.svgDiv').hide();
                        App.toastMsg(`<u><a href="javascript:(function(){window.history.back();})()">Go back</a></u> to create a new solution design`, 'info', '.toastMsg')
                        $('.edit').attr("disabled", true);
                        $('.deploy').attr("disabled", true);
                        $('.title').text("Problem Statement");
                        urldata["sdid"] = null
                    }
                }
            });
        });

        $(".probStatement").hover(
            function () { $('.linkIcon').show(); }, // mouse enter
            function () { $('.linkIcon').hide(); }  // mouse leave
        );

        $(".basePattern").hover(
            function () { $('.linkIconPT').show(); },
            function () { $('.linkIconPT').hide(); }
        );

        if (userRole == "Planner" || userRole == "Administrator") {
            $('#saveChangesBtn').on('click', function (e) {
                let solutionDesignName = $('#solutionDesignProblem').val(),
                    solutionDesignDesc = $('#solutionDesignDescription').val(),
                    solutionForces = $('#solutionDesignForces').val(),
                    solutionConsequences = $('#solutionDesignConsequences').val(),
                    formData = {
                        "solutionDesignName": solutionDesignName,
                        "solutionDesignDesc": solutionDesignDesc,
                        "solutionForces": solutionForces,
                        "solutionConsequences": solutionConsequences,
                        "sdid": sdid,
                    };
                console.log(formData);
                if (!App.isEmpty(solutionDesignName) && !App.isEmpty(solutionDesignDesc) && !App.isEmpty(solutionForces) && !App.isEmpty(solutionConsequences)) {
                    App.genericFetch('editSolutionDesign', 'POST', formData, App.modalFormResponse, { 'submitBtn': 'saveChangesBtn', 'closeBtn': 'closeBtnForEditModal' }, "", "");
                } else {
                    App.toastMsg('Please Enter all the details', 'failed', '.formToastMsg', true);
                }
            });

            // IF approved display only  deploy and edit
            // Only Admin and Planner can request approval
            $('.requestApprove').on('click', function (e) {
                bootbox.confirm({
                    title: "Solution Design Approval",
                    message: "Request to approve design",
                    buttons: {
                        cancel: {
                            label: '<i class="fa fa-times"></i> Cancel'
                        },
                        confirm: {
                            label: '<i class="fa fa-check"></i> Request'
                        }
                    },
                    callback: function (result) {
                        if (result) {
                            App.genericFetch('#', "POST", urldata, "", "", "", "");
                        }
                    }
                });
            });
        } else {
            // TODO:
            $('.editSD').hide();
            $('.deleteSD').hide();
        }

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
    } else {
        $('.title').html(`No Data Found`);
        $('.basePatternForm').hide(); $('.solutionDesignForm').hide();
        $('.approve').hide(); $('.edit').hide(); $('.deploy').hide();
        $('.svgDiv').hide();
    }
});

function approveSolutionDesignStatus(data, id) {
    App.toastMsg(`${id} : Solution Design Approved`, 'success', '.toastMsg', true);
    $('.approve').hide();
}

function renderProblemStmt(problemList, psid) {
    for (let i = 0; i < problemList.length; i++) {
        if (psid == problemList[i].id) {
            let urlParam = `psid=${problemList[i].id}`
            $('.probStatementLink').attr({
                href: `problemPatternSearch?${App.encrypt(urlParam)}`
            });
            $('.probStatement').text(`${problemList[i].id} : ${problemList[i].problemStatement}`);
            $('.probStatementDescription').text(problemList[i].problemDescription);
        }
    }
}

function renderBasePattern(basePattern, bpid) {
    if (basePattern.length > 0) {
        for (let i = 0; i < basePattern.length; i++) {
            let patternType = basePattern[i].type;
            if (bpid == basePattern[i].id) {
                let urlParam = `psid=${basePattern[i].psid}&bpid=${basePattern[i].id}`
                $('.basePatternLink').attr({
                    href: `basePattern?${App.encrypt(urlParam)}`
                });

                $('.basePattern').text(`${basePattern[i].id} : ${basePattern[i].baseName}`);
                $('.typeDataBP').text(` (Type : ${patternType.toUpperCase()})`);
                $('.basePatternDescription').text(basePattern[i].baseDescription);
                $('.basePatternForces').text(basePattern[i].baseForces);
                $('.basePatternBenefits').text(basePattern[i].baseBenefits);
                if (basePattern[i].svg) {
                    // if (basePattern[i].status == 'approved') {
                    $('.BPsvgDiv').append(basePattern[i].svg);
                    $('.BPStatus').text(`Status : ${basePattern[i].status.toUpperCase()}`);
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
    } else {
        $('.title').text('Problem Statement');
        $('.basePatternForm').hide();
        $('.edit').hide(); $('.svgDiv').hide();
    }
}

function renderSolutionDesign(solutionDesign, sdid) {
    if (!App.isEmpty(solutionDesign) && solutionDesign.length > 0) {
        for (let i = 0; i < solutionDesign.length; i++) {
            let patternType = solutionDesign[i].type;
            if (sdid == solutionDesign[i].id) {
                let solutionDesignName = solutionDesign[i].solutionDesignName,
                    solutionDesignDescription = solutionDesign[i].solutionDesignDesc,
                    solutionDesignForces = solutionDesign[i].solutionForces,
                    solutionDesignConsequences = solutionDesign[i].solutionConsequences;

                $('.solutionDesign').text(`${solutionDesign[i].id} : ${solutionDesignName}`);
                $('.typeDataSD').text(` (Type : ${patternType.toUpperCase()})`);
                $('.solutionDesignDescription').text(solutionDesignDescription);
                $('.solutionDesignForces').text(solutionDesignForces);
                $('.solutionDesignConsequences').text(solutionDesignConsequences);

                // Setting data to form for modifying.
                $('#solutionDesignProblem').val(solutionDesignName);
                $('#solutionDesignDescription').val(solutionDesignDescription);
                $('#solutionDesignForces').val(solutionDesignForces);
                $('#solutionDesignConsequences').val(solutionDesignConsequences);

                if (solutionDesign[i].xml) { xml = solutionDesign[i].xml };

                if (patternType == 'pre-defined') {
                    $('.deleteSD').hide();
                    $('.editSD').hide();
                    $('.edit').hide();
                }

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
                    App.toastMsg('No Solution Design Created', 'failed', '.toastMsg', false);
                    $('.svgDiv').hide();
                    // $('.edit').attr("disabled", false);
                }
            }
        }
    } else {
        $('.title').text('Problem Statement')
        $('.solutionDesignForm').hide();
        $('.edit').hide(); $('.svgDiv').hide();
    }
}

function checkImageAproval(isSolutionDesignApproved, id) {
    console.log(isSolutionDesignApproved);

    if (isSolutionDesignApproved == "approved") {
        $('.approve').hide();
        // Fetch Logs
        getLogs();
    } else {
        App.toastMsg("Solution Design is not Approved", 'failed', '.toastMsg', false);

        if (isApprover) {
            $('.approve').attr("disabled", false);
            idToBeApproved = id;
        } else {
            App.toastMsg("Solution Design is not Approved", 'failed', '.toastMsg', false);
        }
    }

    if (isDeployer) {
        if (isSolutionDesignApproved == "approved") {
            $('.deploy').attr("disabled", false);
        }
        else { console.log("cannot deploy"); }
    }
}

function getLogs() {
    //TODO: Check if deployment summary/log is available, if present show them in modal
    App.genericFetch('getScaffoldSolutionDesignlogs', 'POST', { 'sdid': sdid }, renderDataToModal, "", "", "");
}

function renderDataToModal(logs) {

    console.log(logs)
    // Display all the Logs in modal
    if (logs.message == 'success') {
        $('.viewDeploymentSummaryBtn').show();

        let logList = logs.scaffoldLogList;

        for (let i = 0; i < logList.length; i++) {
            let compileLog = JSON.parse(logList[i].compileLogs),
                runtimeLog = JSON.parse(logList[i].runtimeLogs),
                table;

            if (!App.isEmpty(compileLog)) {
                for (let j = 0; j < compileLog.length; j++) {
                    table = `<tr>
                                <th scope="row">${j + 1}</th>
                                <td>${compileLog[j].componentData.label}</td>
                                <td>${compileLog[j].creationDetails.AttachTime}</td>
                                <td>${compileLog[j].comments}</td>
                            </tr>`;
                    $('.compileTabTable').append(table);
                }
            } else {
                console.log('compileLog is empty');
                $('.compileTabDataInTableDiv').hide();
                $('.compileTabData').html(`<span class="m-2 compileDivTitle">No logs found</span>`);
            }

            if (!App.isEmpty(runtimeLog)) {
                for (let k = 0; k < runtimeLog.length; k++) {
                    table = `<tr>
                                <th scope="row">${k + 1}</th>
                                <td>${runtimeLog[k].componentData.label}</td>
                                <td>${runtimeLog[k].creationDetails.AttachTime}</td>
                                <td>${runtimeLog[k].comments}</td>
                            </tr>`;
                    $('.runtimeTabTable').append(table);
                }
            } else {
                console.log('runtimeLog is empty');
                $('.runtimeTabDataInTableDiv').hide();
                $('.runtimeTabData').html(`<span class="m-2 compileDivTitle">No logs found</span>`);
            }

            $('.deploymentStatus').text(logList[i].csStatus.toUpperCase());
            // $('.compileTabData').text(logList[i].compileLogs);
            // $('.runtimeTabData').text(logList[i].runtimeLogs);
        }
    } else {
        console.log("Pattern Approved but not deployed");
        $('.runtimeTabData').text('No logs found');
        $('.compileTabData').text('No logs found');
    }
}

function compileDesign(str) {
    let successResponseRenderMethod;

    if (str && str == 'recompile') {
        successResponseRenderMethod = deploySolutionDesign;
    } else {
        loadingModal('Compilation is in progress...');
        successResponseRenderMethod = checkCompilationData;
    }
    // Compile Graph Design
    App.genericFetch('compileScaffoldSolutionDesign', 'POST', { 'sdid': sdid }, successResponseRenderMethod, "", "", "");
}

function checkCompilationData(compileData) {
    // IF data has comiplation log and if not present hide the modal's complie tab
    closeLoadingModal();
    if (!compileData) {//.message == 'success') {
        // After Compilation open deployment summary modal then ask for proceed
        $('#viewDeploymentSummaryModal').modal('show');
        $('#proceedBtn').show();

        $('#proceedBtn').on('click', function (e) {

            // close the running modal
            $('#viewDeploymentSummaryModal').modal('hide');
            //change the span text status

            // recompile and Call Deployment API
            compileDesign('recompile');
        });

    } else {
        // show error message in modal if possible
        alertModal('Compilation Failed!!!');
    }
}

function deploySolutionDesign() {
    // Show loading modal
    loadingModal('Deployment In progress...');
    App.genericFetch('deployScaffoldSolutionDesign', 'POST', { 'sdid': sdid, 'psid': psid }, checkDeploymentData, "success", App.outputResponse, "error");
    // After successfull Deployment Change status to 'Deployed-Successful'
    // Display all the Logs in modal
}

function checkDeploymentData(data, param, res) {
    closeLoadingModal();

    if (param == "success") {//(data.message == 'success') {
        alertModal('Deployment Successful');
        // App.modalFormResponse({ 'message': 'success', 'info': `${ sdid }, ${ psid } ` }, { 'submitBtn': 'proceedBtn', 'closeBtn': 'closeBtnForDeploymentSummary' });

        // remove all buttons like edit, deploy, approve,request
        $('#allButtonsDiv').hide();
    } else {
        alertModal('Deployment Failed');
    }
}

function confirmDeployAlertBox() {
    bootbox.confirm({
        title: "Deploy Solution Design",
        message: "Please confirm to deploy design",
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
                compileDesign();
                // After compilation change the status to compiled
            }
        }
    });
}

var dialog;
function loadingModal(msg) {
    dialog = bootbox.dialog({
        message: `<p class="text-center mb-0"><i class="fa fa-spin fa-cog"></i>  ${msg}</p>`,
        closeButton: false
    });
}
function closeLoadingModal() {
    dialog.modal('hide');
}

function alertModal(msg) {
    bootbox.dialog({
        message: `<p class="text-center alert mb-0 h3">${msg}</p>`,
        buttons: {
            cancel: {
                label: 'Close',
                className: 'btn-danger'
            }
        }
    });
}