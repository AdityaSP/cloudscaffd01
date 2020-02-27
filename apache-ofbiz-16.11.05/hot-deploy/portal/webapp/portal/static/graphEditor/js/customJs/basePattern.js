import { App } from './app.js';

var urldata = App.urlParams();
var psid = urldata['psid'];
var sdid = urldata['sdid'];
var bpid, isBasePatternApproved, isDeployer = false, isApprover = false, isAdmin = false, isPlanner = false;

console.log(urldata);

$(function () {
    $('.py-3').contents().filter(function () {
        return this.nodeType === 3;
    }).remove();

    $('[data-toggle="tooltip"]').tooltip();

    if (urldata['bpid']) { bpid = urldata['bpid'] };

    // Fetch and Rendering Base Pattern
    App.loader(".basePatternForm");
    App.genericFetch('getBasePattern', "POST", { "bpid": bpid }, renderBasePattern, bpid);

    // Fetch and Rendering Problem Statement
    App.loader(".probStatementForm");
    App.genericFetch('getProblemStatements', "POST", { "psid": psid }, renderProblemStmt, psid);

    $('.approve').attr("disabled", true);

    let userRole = $('.userRoleName').text();

    switch (userRole) {
        case "Administrator": {
            isAdmin = true;
            $('.approve').attr("disabled", true);
        }; break;
        case "Deployer": {
            isDeployer = true;
            $('.approve').attr("disabled", true);
        }; break;
        case "Approver": {
            isApprover = true;
            $('.edit').attr("disabled", true);
        }; break;
        case "Planner": {
            isPlanner = true;
            $('.approve').attr("disabled", true);
        }; break;
        default: break;
    }

    console.log(`Role: ${userRole}, isBasePatternApproved: ${isBasePatternApproved}, isApprover: ${isApprover}, isDeployer: ${isDeployer}`);

    // IF approved display only  deploy and edit
    $('.approve').on('click', function () {
        bootbox.confirm({
            title: "Pattern Approval",
            message: "Please confirm to approve Pattern",
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
                    App.genericFetch('approveBasePattern', "POST", urldata, reloadPage, bpid, "", "");
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
        window.location.href = `graphEditor?${App.encrypt(urlParam)}`;
    });

    $('.deleteBP').on('click', function (e) {
        bootbox.confirm({
            title: "Delete Pattern",
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
                    App.genericFetch('deleteBasePattern', "POST", { "bpid": bpid }, "", "", "", "");
                    $('.basePatternForm').hide(); $('.svgDiv').hide();
                    App.toastMsg(`<u><a href="javascript:(function(){window.history.back();})()">Go back</a></u> to create a new Base Pattern`, 'info', '.toastMsg')
                    $('.edit').attr("disabled", true);
                    $('.deploy').attr("disabled", true);
                    $('.title').text("Problem Statement");
                    urldata["bpid"] = null;
                } else {
                    console.log(result);
                }
            }
        });
    });

    if (userRole == "Planner" || userRole == "Administrator") { // || userRole == "Deployer"
        $('#saveChangesBtn').on('click', function (e) {
            let baseName = $('#baseProblem').val(),
                baseDescription = $('#baseProblemDescription').val(),
                baseForces = $('#baseForces').val(),
                baseConsequences = $('#baseConsequences').val(),
                formData = {
                    "baseName": baseName,
                    "baseDescription": baseDescription,
                    "baseForces": baseForces,
                    "baseConsequences": baseConsequences,
                    "bpid": bpid,
                };
            console.log(formData);
            if (!App.isEmpty(baseName) && !App.isEmpty(baseDescription) && !App.isEmpty(baseForces) && !App.isEmpty(baseConsequences)) {
                App.genericFetch('editBasePattern', 'POST', formData, App.modalFormResponse, "", "", "");
            } else {
                App.toastMsg('Please Enter all the details', 'failed', '.formToastMsg', true);
            }
        });
    } else {
        // TODO:
        $('.editBP').hide();
        $('.deleteBP').hide();
    }
});


function reloadPage(data, id) {
    App.toastMsg(`${id} : Pattern Approved`, 'success', '.toastMsg', true);
    $('.approve').hide();
}

function renderProblemStmt(problemList, psid) {
    for (let i = 0; i < problemList.length; i++) {
        if (psid == problemList[i].id) {
            $('.probStatement').text(`${problemList[i].id} : ${problemList[i].problemStatement}`);
            $('.probStatementDescription').text(problemList[i].problemDescription);
        }
    }
}

function renderBasePattern(basePattern, bpid) {
    if (basePattern.length > 0) {
        for (let i = 0; i < basePattern.length; i++) {
            psid = basePattern[i].psid;
            let patternType = basePattern[i].type;

            let baseName = basePattern[i].baseName,
                baseDescription = basePattern[i].baseDescription,
                baseForces = basePattern[i].baseForces,
                basePatternConsequences = basePattern[i].baseConsequences;

            $('.basePattern').text(`${basePattern[i].id} : ${baseName}`);
            $('.typeDataBP').text(` (Type : ${patternType.toUpperCase()})`);
            $('.basePatternDescription').text(baseDescription);
            $('.basePatternForces').text(baseForces);
            $('.basePatternConsequences').text(basePatternConsequences);

            // Setting data to form for modifying.
            $('#baseProblem').val(baseName);
            $('#baseProblemDescription').val(baseDescription);
            $('#baseForces').val(baseForces);
            $('#baseConsequences').val(basePatternConsequences);

            if (patternType == 'pre-defined') {
                $('.deleteBP').hide();
                $('.editBP').hide();
                $('.edit').hide();
            }

            if (basePattern[i].svg) {
                // let imgURL = basePattern[i].png;
                // console.log(imgURL);
                // $("#basePatternImg")[0].src = imgURL;
                // console.log(basePattern[i].svg);

                $('.svgDiv').append(basePattern[i].svg);
                $('svg').attr({
                    "min-width": "100px",
                    "min-height": "100px"
                });
                //Check If Solution Design is apporoved or not
                isBasePatternApproved = basePattern[i].status;
                checkImageAproval(isBasePatternApproved);
            } else {
                App.toastMsg('No Pattern Created', 'failed', '.toastMsg');
                $('.svgDiv').hide();
                $('.edit').attr("disabled", false);
            }
            if (patternType == 'pre-defined') {
                $('.approve').hide();
                $('.title').text('Pre-Defined Pattern');
                $('.edit').hide(); $('.editBP').hide();
            }
        }
    } else {
        $('.title').text('Problem Statement');
        $('.basePatternForm').hide();
        $('.edit').hide(); $('.svgDiv').hide();
    }
}

function checkImageAproval(isBasePatternApproved) {
    console.log(isBasePatternApproved);
    if (isBasePatternApproved == "approved") {
        $('.approve').hide();
    } else {
        App.toastMsg("Pattern is not Approved", 'failed', '.toastMsg');
        if (isApprover) {
            $('.approve').attr("disabled", false);
        } else {
            App.toastMsg("Pattern is not Approved", 'failed', '.toastMsg');
        }
    }
}