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
            title: "Base Pattern Design Approval",
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
            title: "Delete Base Pattern",
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
                    App.toastMsg(`<u><a href="${document.referrer}">Go back</a></u> to create a new Base Pattern`, 'info', '.toastMsg')
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
});

function reloadPage(data, id) {
    App.toastMsg(`${id} : Design Approved`, 'success', '.toastMsg', true);
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
            $('.basePattern').text(`${basePattern[i].id} : ${basePattern[i].baseName}`);
            $('.typeDataBP').text(` (Type : ${patternType.toUpperCase()})`);
            $('.basePatternDescription').text(basePattern[i].baseDescription);
            $('.basePatternForces').text(basePattern[i].baseForces);
            $('.basePatternBenefits').text(basePattern[i].baseBenefits);

            if (patternType == 'pre-defined') {
                $('.deleteBP').hide();
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
                App.toastMsg('No Design Created', 'failed', '.toastMsg');
                $('.svgDiv').hide();
            }
            if (patternType == 'pre-defined') {
                // $('.deleteSD').hide();
                $('.edit').hide();
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
        App.toastMsg("Base Pattern is not Approved", 'failed', '.toastMsg');
        if (isApprover) {
            $('.approve').attr("disabled", false);
        } else {
            App.toastMsg("Base Pattern is not Approved", 'failed', '.toastMsg');
        }
    }
}