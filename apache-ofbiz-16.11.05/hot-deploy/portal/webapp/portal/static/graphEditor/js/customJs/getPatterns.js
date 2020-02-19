import { App } from './app.js';

var problemStatementList, basePatternList, solutionDesignList, tagsList;

$(function () {

    $('[data-toggle="tooltip"]').tooltip();

    var urlParams = App.urlParams(),
        userRole = App.userRole,
        psid = urlParams['psid']; console.log(urlParams, userRole);

    $(".psid").val(psid);

    // Fetch and render Problem Statement
    App.loader(".problemStatementDiv"); App.loader(".basePatternResults"); App.loader(".solutionPatternResults");
    App.genericFetch('getPatternByPsId', "POST", { "psid": psid }, renderProblemStmt, psid);

    if (userRole == "Planner" || userRole == "Administrator") { // || userRole == "Deployer"

        $(".saveChangesBtn").on('click', function (e) {
            let problemStatement = $('#problemStatement').val(),
                problemDescription = $('#problemDescription').val(),
                formData = {
                    "problemStatement": problemStatement,
                    "problemDescription": problemDescription,
                    "psid": psid,
                };
            console.log(formData);
            if (!App.isEmpty(problemStatement) && !App.isEmpty(problemDescription)) {
                // App.genericFetch('editProblemStatement', 'POST', formData, "", "", "", "");
                // response is not rendered
            } else {
                App.toastMsg('Please enter all the details', 'failed', '.formToastMsg', true);
            }
        });

        $("#basePatternFormSubmitBtn").on('click', function (e) {
            let baseName = $('#baseProblem').val(),
                baseDescription = $('#baseProblemDescription').val(),
                baseForces = $('#baseForces').val(),
                baseBenefits = $('#baseBenefits').val(),
                psid = $('.psid').val(),
                formData = {
                    "baseName": baseName,
                    "baseDescription": baseDescription,
                    "baseForces": baseForces,
                    "baseBenefits": baseBenefits,
                    "psid": psid,
                };
            if (!App.isEmpty(baseName) && !App.isEmpty(baseDescription) && !App.isEmpty(baseForces) && !App.isEmpty(baseBenefits)) {
                $('.submitBtn').val('Creating...');
                App.genericFetch('AddBasePattern', 'POST', formData, submitForm, "", "", "");
                $('.submitBtn').attr("disabled", true);
            } else {
                App.toastMsg('Please enter all the details', 'failed', '.toastMsg', true);
            }
        });

        $("#solutionDesignFormSubmitBtn").on('click', function (e) {
            let solutionDesignName = $('#solutionDesignName').val(),
                solutionDesignDesc = $('#solutionDesignDescription').val(),
                solutionForces = $('#solutionForces').val(),
                solutionBenefits = $('#solutionBenefits').val(),
                psid = $('.psid').val(),
                bpid = $('.bpid').val(),
                formData = {
                    "solutionDesignName": solutionDesignName,
                    "solutionDesignDesc": solutionDesignDesc,
                    "solutionForces": solutionForces,
                    "solutionBenefits": solutionBenefits,
                    "psid": psid,
                    "bpid": bpid,
                };
            console.log(formData);
            if (!App.isEmpty(solutionDesignName) && !App.isEmpty(solutionDesignDesc) && !App.isEmpty(solutionForces) &&
                !App.isEmpty(solutionBenefits) && !App.isEmpty(psid)) {
                $('.submitBtn').val('Creating...');
                App.genericFetch('AddSolutionDesign', 'POST', formData, submitForm, "", "", "");
                $('.submitBtn').attr("disabled", true);
            } else {
                App.toastMsg('Please enter all the details', 'failed', '.toastMsg', true);
            }
        });
    } else {
        $('.submitBtn').attr("disabled", true);
    }

    $('.solutionPatternResults').on('click', '.solutionDesigns', function (evt) {
        let sdid = evt.target.dataset["sdid"];
        let psid = evt.target.dataset["psid"];
        let bpid, url = `psid=${psid}&sdid=${sdid}`;

        if (evt.target.dataset["bpid"]) {
            bpid = evt.target.dataset["bpid"];
            url = `psid=${psid}&bpid=${bpid}&sdid=${sdid}`
        }
        url = App.encrypt(url);
        // console.log(psid, sdid, bpid, url);
        window.location.href = `solutionPattern?${url}`;
    });

    $('[data-toggle="tooltip"]').tooltip();
});

function submitForm(data, path, res) {
    $('.submitBtn').hide();
    if (data.message == 'success') {
        App.toastMsg('Creation Successful', 'success', '.toastMsg', true);
        setTimeout(function () {
            window.location.reload();
        }, 1000);
    } else {
        App.toastMsg('Failed to create', 'failed', '.toastMsg', 1500);
        App.clearInput('input:not(:button)');
        setTimeout(function () {
            $('.submitBtn').val('Create');
            $('.submitBtn').attr("disabled", false);
            $('.submitBtn').show();
        }, 1500);
    }
}

function renderProblemStmt(problemStmt, psid) {
    if (!App.isEmpty(problemStmt)) {
        problemStatementList = problemStmt.problemStatementList[0];
        basePatternList = problemStmt.basePatternList;
        solutionDesignList = problemStmt.solutionDesignList;
        tagsList = problemStmt.tagsList;

        if (problemStmt.problemStatementList.length > 0) {
            $('#probStatement').text(problemStatementList.problemStatement);
            $('#probStatementDescription').text(problemStatementList.problemDescription);
            $('#problemStatement').val(problemStatementList.problemStatement);
            $('#problemDescription').val(problemStatementList.problemDescription);
        }

        // Tags Adding
        for (var k = 0; k < tagsList.length; k++) {
            let htmlTags = `<a href="javascript:void(0);" id="${tagsList[k].tagid}"
                 class="badge badge-light mr-2 p-2">${tagsList[k].tagName}</a>`; // Redirect to productApc page to show problem statements
            document.querySelector(".problemTags").insertAdjacentHTML("beforeend", htmlTags);
        }

        // Rendering Base Patterns
        if (basePatternList.length > 0) {
            for (let j = 0; j < basePatternList.length; j++) {
                let urlParams = `psid=${psid}&bpid=${basePatternList[j].id}`;
                urlParams = App.encrypt(urlParams);

                let basePatternsHtml = `<li class="list-group-item basePattern"
                        data-psid="${psid}"
                        data-bpid="${basePatternList[j].id}">
                        <span class="">${basePatternList[j].baseName}</span>
                        <a href="basePattern?${urlParams}" class="h-50 badge badge-secondary p-2 pull-right basePatternView">View BP</a>
                        </li>`;
                document.querySelector(".basePatternResults").insertAdjacentHTML("afterbegin", basePatternsHtml);
            }
        } else {
            let basePatternsHtml = `<span class="text-center pt-3">No Base Patterns Found</span>`;
            document.querySelector(".basePatternResults").insertAdjacentHTML("afterbegin", basePatternsHtml);
        }

        // Solution Design Rendering
        if (solutionDesignList.length > 0) {
            for (let j = 0; j < solutionDesignList.length; j++) {
                var solutionDesignsHtml = `<li class="list-group-item solutionDesigns" 
                data-psid="${psid}" data-sdid="${solutionDesignList[j].id}">
                ${solutionDesignList[j].solutionDesignName}</li>`;
                document.querySelector(".solutionPatternResults").insertAdjacentHTML("afterbegin", solutionDesignsHtml);
            }
        } else {
            let solutionDesignsHtml = `<span class="text-center pt-3">No Solution Designs Found</span>`;
            document.querySelector(".solutionPatternResults").insertAdjacentHTML("afterbegin", solutionDesignsHtml);
        }

        $('.basePattern').on('click', function (evt) {
            $(".solutionPatternDiv").show();
            $('.solutionPatternResults').children().remove();

            let bpid = evt.target.dataset["bpid"];
            let psid = evt.target.dataset["psid"];
            if (!psid) {
                psid = evt.target.parentNode.dataset["psid"];
                bpid = evt.target.parentNode.dataset["bpid"];
            }
            $('.bpid').val(bpid);
            // Fetch solution designs and render
            // App.loader('');
            $(".basePattern").removeClass("active");
            $(this).addClass("active");
            App.genericFetch("getSolutionDesignByBpid", "POST", { "bpid": bpid }, renderSolutionDesignsForBasePattern, "", "", "");
        });
    }
    else {
        $('#probStatement').text(`PSID : ${psid} is not valid / not passed`);
        $('.allSolutions').hide();
    }
}

function renderSolutionDesignsForBasePattern(solutionPatterns) {
    if (solutionPatterns.length > 0) {
        for (let l = 0; l < solutionPatterns.length; l++) {
            let solutionPattern = `<li class="list-group-item solutionDesigns"
                            data-bpid=${solutionPatterns[l].bpid} data-psid=${solutionPatterns[l].psid} data-sdid="${solutionPatterns[l].id}">
                            ${solutionPatterns[l].solutionDesignName}</li>`;
            document.querySelector(".solutionPatternResults").insertAdjacentHTML("afterbegin", solutionPattern);
        }
    } else {
        let solutionPattern = `<span class="text-center pt-3">No Solutions Design found</span>`;
        document.querySelector(".solutionPatternResults").insertAdjacentHTML("afterbegin", solutionPattern);
    }
}