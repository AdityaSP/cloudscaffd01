import { App } from './app.js';

var problemStatementList, basePatternList, solutionDesignList, tagsList, tagIds = [];

$(function () {
    $('.toast').remove();
    $('[data-toggle="tooltip"]').tooltip();

    var urlParams = App.urlParams(),
        userRole = App.userRole,
        psid = urlParams['psid']; console.log(urlParams, userRole);

    if (!App.isEmpty(urlParams)) {
        $(".psid").val(psid); // For create forms
        $(".psid").text(psid); // For Modal

        // Fetch and render Problem Statement
        App.loader(".problemStatementDiv"); App.loader(".basePatternResults"); App.loader(".solutionPatternResults");
        App.genericFetch('getPatternByPsId', "POST", { "psid": psid }, renderProblemStmt, psid);

        if (userRole == "Planner" || userRole == "Administrator") {
            $(".editModalSaveBtn").on('click', function (e) {
                let problemStatement = $('#problemStatement').val(),
                    problemDescription = $('#problemDescription').val(),
                    oldTagIds = tagIds,
                    newTags = App.getUniqueArray($('#tagInput').val().split(' ')),
                    formData = {
                        "problemStatement": problemStatement,
                        "problemDescription": problemDescription,
                        "oldTagIds": oldTagIds.toString(),
                        "newTags": newTags.toString(),
                        "psid": psid,
                    };
                console.log(formData);
                if (!App.isEmpty(problemStatement) && !App.isEmpty(problemDescription)) {
                    // App.genericFetch('editProblemStatement', 'POST', formData, App.modalFormResponse, "", "", "");
                } else {
                    App.toastMsg('Please enter all the details', 'failed', '.formToastMsg', true);
                }
            });

            $("#basePatternFormSubmitBtn").on('click', function (e) {
                let baseName = App.unescapeHtmlText($('#baseProblem').val()),
                    baseDescription = App.unescapeHtmlText($('#baseProblemDescription').val()),
                    baseForces = App.unescapeHtmlText($('#baseForces').val()),
                    baseConsequences = App.unescapeHtmlText($('#baseConsequences').val()),
                    psid = $('.psid').val(),
                    formData = {
                        "baseName": baseName,
                        "baseDescription": baseDescription,
                        "baseForces": baseForces,
                        "baseConsequences": baseConsequences,
                        "psid": psid,
                    };
                if (!App.isEmpty(baseName) && !App.isEmpty(baseDescription) && !App.isEmpty(baseForces) && !App.isEmpty(baseConsequences)) {
                    $('.submitBtnForPattern').val('Creating...');
                    App.genericFetch('AddBasePattern', 'POST', formData, submitForm, {
                        "submitBtn": "submitBtnForPattern",
                        "toastMsgDiv": "toastMsgForPattern"
                    }, App.outputResponse, "ERROR!");
                    $('.submitBtnForPattern').attr("disabled", true);
                } else {
                    App.toastMsg('Please enter all the details', 'failed', '.toastMsgForPattern', true);
                }
            });

            $("#solutionDesignFormSubmitBtn").on('click', function (e) {
                let solutionDesignName = App.unescapeHtmlText($('#solutionDesignName').val()),
                    solutionDesignDesc = App.unescapeHtmlText($('#solutionDesignDescription').val()),
                    solutionForces = App.unescapeHtmlText($('#solutionForces').val()),
                    solutionConsequences = App.unescapeHtmlText($('#solutionConsequences').val()),
                    psid = $('.psid').val(),
                    bpid = $('.bpid').val(),
                    formData = {
                        "solutionDesignName": solutionDesignName,
                        "solutionDesignDesc": solutionDesignDesc,
                        "solutionForces": solutionForces,
                        "solutionConsequences": solutionConsequences,
                        "psid": psid,
                        "bpid": bpid,
                    };
                console.log(formData);
                if (!App.isEmpty(solutionDesignName) && !App.isEmpty(solutionDesignDesc) && !App.isEmpty(solutionForces) &&
                    !App.isEmpty(solutionConsequences) && !App.isEmpty(psid)) {
                    $('.submitBtnForSolutionDesign').val('Creating...');
                    App.genericFetch('AddSolutionDesign', 'POST', formData, submitForm, {
                        "submitBtn": "submitBtnForSolutionDesign",
                        "toastMsgDiv": "toastMsgForSolutionDesign"
                    }, App.outputResponse, "ERROR!");

                    $('.submitBtnForSolutionDesign').attr("disabled", true);
                } else {
                    App.toastMsg('Please enter all the details', 'failed', '.toastMsgForSolutionDesign', true);
                }
            });
        } else {
            $('.submitBtnForSolutionDesign').attr("disabled", true);
            $('.editPS').hide();
            $('.deletePS').hide();
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
            window.location.href = `solutionPattern?${url}`;
        });

        $('[data-toggle="tooltip"]').tooltip();

    }
    else {
        $('#probStatement').text(`No Data Found`);
        $('.editPS').hide(); $('.deletePS').hide();
        $('.allSolutions').hide();
    }
});

function submitForm(data, params) {
    $(`${params.submitBtn}`).hide();
    if (data.message == 'success') {
        App.toastMsg(data.info, 'success', `.${params.toastMsgDiv}`, true);
        setTimeout(function () {
            window.location.reload();
        }, 1000);
    } else {
        App.toastMsg(data.info, 'failed', `.${params.toastMsgDiv}`, 1500);
        App.clearInput('input:not(:button)');
        setTimeout(function () {
            $(`.${params.toastMsgDiv}`).val('Create');
            $(`.${params.toastMsgDiv}`).attr("disabled", false);
            $(`.${params.toastMsgDiv}`).show();
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
        } else {
            $('.editPS').hide();
            let problemStmt = `<span class="text-center pt-3">No Data Found</span>`;
            document.querySelector("#probStatement").insertAdjacentHTML("afterbegin", problemStmt);
        }

        // Tags Adding
        for (var k = 0; k < tagsList.length; k++) {
            //Adding tags to modal
            $('#tagInput').val(`${$('#tagInput').val()} ${tagsList[k].tagName}`);
            tagIds.push(tagsList[k].tagid);

            //Count for modal
            $('.tagsCount').text(tagsList.length + "-Tag");

            let url = `productAPC?`, queryStr = `tagid=${tagsList[k].tagid}`,
                htmlTags = `<a href="${url}${App.encrypt(queryStr)}" id="${tagsList[k].tagid}"
                 class="badge badge-light mr-2 p-2">${tagsList[k].tagName}</a>`;
            document.querySelector(".problemTags").insertAdjacentHTML("beforeend", htmlTags);
        }

        // Rendering Base Patterns
        if (basePatternList.length > 0) {
            // Pattern count for modal
            $('.ptCount').text(basePatternList.length + "-Pattern");

            for (let j = 0; j < basePatternList.length; j++) {
                let urlParams = `psid=${psid}&bpid=${basePatternList[j].id}`;
                urlParams = App.encrypt(urlParams);

                let basePatternsHtml = `<li class="list-group-item basePattern"
                        data-psid="${psid}"
                        data-bpid="${basePatternList[j].id}">
                        <span class="">${basePatternList[j].baseName}</span>
                        <a href="basePattern?${urlParams}" class="h-50 badge badge-secondary p-2 pull-right
                        ">View Pattern</a>
                        </li>`;
                document.querySelector(".basePatternResults").insertAdjacentHTML("afterbegin", basePatternsHtml);
            }
        } else {
            let basePatternsHtml = `<span class="text-center pt-3">No Patterns Found</span>`;
            document.querySelector(".basePatternResults").insertAdjacentHTML("afterbegin", basePatternsHtml);
            // Pattern count for modal
            $('.ptCount').text("0-Pattern");
        }

        // Solution Design Rendering
        if (solutionDesignList.length > 0) {
            // Design count for modal
            $('.sdCount').text(solutionDesignList.length + "-Solution Design");

            for (let j = 0; j < solutionDesignList.length; j++) {
                var solutionDesignsHtml = `<li class="list-group-item solutionDesigns"
                data-psid="${psid}" data-sdid="${solutionDesignList[j].id}">
                ${solutionDesignList[j].solutionDesignName}</li>`;
                document.querySelector(".solutionPatternResults").insertAdjacentHTML("afterbegin", solutionDesignsHtml);
            }
        } else {
            let solutionDesignsHtml = `<span class="text-center pt-3">No Solution Designs Found</span>`;
            document.querySelector(".solutionPatternResults").insertAdjacentHTML("afterbegin", solutionDesignsHtml);
            // Design count for modal
            $('.sdCount').text("0-Solution Design");
        }

        $('.basePattern').on('click', function (evt) {
            $(".solutionPatternDiv").show();
            $('.solutionPatternResults').children().remove();
            App.loader('.solutionPatternResults');

            let bpid = evt.target.dataset["bpid"];
            let psid = evt.target.dataset["psid"];
            if (!psid) {
                psid = evt.target.parentNode.dataset["psid"];
                bpid = evt.target.parentNode.dataset["bpid"];
            }
            $('.bpid').val(bpid);
            // Fetch solution designs based on BPID and render
            $(".basePattern").removeClass("active");
            $(this).addClass("active");
            App.genericFetch("getSolutionDesignByBpid", "POST", { "bpid": bpid }, renderSolutionDesignsForBasePattern, "", "", "");
        });
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