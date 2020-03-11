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
        $('.viewDeploymentSummaryBtn').hide();//TODO:uncommment
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
                        let data = {
                            'sdid': sdid,
                            'psid': psid
                        };
                        // console.log(data)
                        checkCompilationData()
                        // data = App.xmlToJson(new DOMParser().parseFromString(xml, 'text/xml'));
                        // App.genericFetch("#", "POST", data, checkCompilationData, "", "", "");

                        // After successfull Deployment Change status to 'Deployed-Successful'
                        // App.genericFetch('approveSolutionDesign', "POST", urldata, reloadPage, sdid, "", ""); // urlData includes status
                    }
                }
            });
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
                    } else {
                        console.log(result);
                    }
                }
            });
        });

        $(".probStatement").hover(
            function () {
                $('.linkIcon').show();
            }, function () {
                $('.linkIcon').hide();
            }
        );

        $(".basePattern").hover(
            function () {
                $('.linkIconPT').show();
            }, function () {
                $('.linkIconPT').hide();
            }
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
        // $('.edit').attr("disabled", false);
    } else {
        App.toastMsg("Solution Design is not Approved", 'failed', '.toastMsg', false);

        if (isApprover) {
            $('.approve').attr("disabled", false);
            idToBeApproved = id;
        } else {
            App.toastMsg("Solution Design is not Approved", 'failed', '.toastMsg');
        }
    }

    if (isDeployer) {
        if (isSolutionDesignApproved == "approved") {
            $('.deploy').attr("disabled", false);
            $('.viewDeploymentSummaryBtn').show();//TODO: Check
            $('.proceedBtn').hide();
        }
        else {
            console.log("cannot deploy");
        }
    }
}

function checkCompilationData(data) {
    // IF data has comiplation error or message show Deploment summary modal and ask for proceed then call deployment api
    if (data) {// TODO : check if data has comiplation error
        $('#viewDeploymentSummaryModal').modal('show');
        $('#proceedBtn').show();

        // Display all the compilation errors in modal

        $('#proceedBtn').on('click', function (e) {
            // Call Deployment API
            deploySolutionDesign();
        });
    } else {
        // Call Deployment API
        deploySolutionDesign();
    }
}

function deploySolutionDesign() {
    App.modalFormResponse({ 'message': 'success', 'info': `${sdid}, ${psid}` }, { 'submitBtn': 'proceedBtn', 'closeBtn': 'closeBtnForDeploymentSummary' });
    // App.genericFetch('#', 'POST', { 'sdid': sdid, 'psid': psid }, App.modalFormResponse, { 'submitBtn': 'proceedBtn', 'closeBtn': 'closeBtnForDeploymentSummary' }, App.outputResponse, "ERROR!");
}