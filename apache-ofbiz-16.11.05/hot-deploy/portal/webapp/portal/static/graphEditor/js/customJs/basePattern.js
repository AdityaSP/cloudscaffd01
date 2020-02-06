import { App } from './app.js';

var urldata = App.urlParams();
var psid = urldata['psid'];
var sdid = urldata['sdid'];
var bpid, isBasePatternApproved, isDeployer = false, isApprover = false;

$(function () {
    $('.py-3').contents().filter(function () {
        return this.nodeType === 3;
    }).remove();

    if (urldata['bpid']) { bpid = urldata['bpid'] };

    // Fetch and Rendering Base Pattern
    App.genericFetch('getBasePattern', "POST", { "bpid": bpid }, renderBasePattern, bpid);

    // Fetch and Rendering Problem Statement
    App.genericFetch('getProblemStatements', "POST", { "psid": psid }, renderProblemStmt, psid);

    $('.deploy').attr("disabled", true);
    $('.approve').hide();

    let userRole = $('.userRoleName').text();

    if (userRole == 'Administrator' || userRole == 'Deployer') {
        isDeployer = true;
    } else if (userRole == 'Administrator' || userRole == 'Approver') {
        isApprover = true;
    } else {
        isApprover = false;
        isDeployer = false;
    }

    console.log(userRole, `isBasePatternApproved: ${isBasePatternApproved}, isApprover: ${isApprover}, isDeployer: ${isDeployer}`);

    $('.deploy').on('click', function (evt) {
        console.log("deploy")
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
});
function renderProblemStmt(problemList, psid) {
    for (let i = 0; i < problemList.length; i++) {
        if (psid == problemList[i].id) {
            $('.probStatement').text(`${problemList[i].id} : ${problemList[i].problemStatement}`);
            $('.probStatementDescription').text(problemList[i].problemDescription);
        }
    }
}

function renderBasePattern(basePattern, bpid) {
    for (let i = 0; i < basePattern.length; i++) {
        psid = basePattern[i].psid;
        $('.basePattern').text(`${basePattern[i].id} : ${basePattern[i].baseName}`);
        $('.basePatternDescription').text(basePattern[i].baseDescription);

        if (basePattern[i].svg) {
            // let imgURL = basePattern[i].png;
            // console.log(imgURL);
            // $("#basePatternImg")[0].src = imgURL;
            // console.log(basePattern[i].svg);

            $('.svgDiv').append(basePattern[i].svg);
            $('svg').attr({
                "width": "100%",
                "height": "100%"
            });
            //Check If Solution Design is apporoved or not
            isBasePatternApproved = basePattern[i].status;
            checkImageAproval(isBasePatternApproved);
        } else {
//            $('.edit').attr("disabled", true);
            App.toastMsg('No Design Created', 'failed', '.toastMsg');
        }
    }
}

function checkImageAproval(isBasePatternApproved) {
    console.log(isBasePatternApproved);
    if (isBasePatternApproved == "approved") {
        $('.approve').hide();
    } else {
        App.toastMsg("Base Pattern is not Approved", 'failed', '.toastMsg');
        if (isApprover) {
            $('.approve').show();

            // IF approved display only  deploy and edit
            $('.approve').on('click', function () {
                App.toastMsg("approved");

                $('.toastMsg').hide();
                $('.approve').hide();
            });
        } else {
            App.toastMsg("Base Pattern is not Approved", 'failed', '.toastMsg');
        }
    }
    if (isDeployer) {
        if (isBasePatternApproved == "approved") {
            $('.deploy').attr("disabled", false);
//            $('.edit').attr("disabled", false);
        }
    }
}