import { App } from './app.js';

var problemStatementList, basePatternList, solutionDesignList, tagsList;

$(function () {

    var urlParams = App.urlParams();
    console.log(urlParams);

    var psid = urlParams['psid'];
    $(".psid").val(psid);

    // Fetch and render Problem Statement
    App.genericFetch('getPatternByPsId', "POST", { "psid": psid }, renderProblemStmt, psid);

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

function renderProblemStmt(problemStmt, psid) {
    if (!App.isEmpty(problemStmt)) {
        problemStatementList = problemStmt.problemStatementList[0];
        basePatternList = problemStmt.basePatternList;
        solutionDesignList = problemStmt.solutionDesignList;
        tagsList = problemStmt.tagsList;

        if (problemStmt.problemStatementList.length > 0) {
            $('#probStatement').text(problemStatementList.problemStatement);
            $('#probStatementDescription').text(problemStatementList.problemDescription);
        }

        // Tags Adding
        for (var k = 0; k < tagsList.length; k++) {
            let htmlTags = `<a href="#" id="${tagsList[k].id}"
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
            console.log(bpid,psid)
            // Fetch solution designs and render
            // App.loader('');
            $(".basePattern").removeClass("active");
            $(this).addClass("active");
            App.genericFetch("getBasePatternByBpid", "POST", { "bpid": bpid }, renderSolutionDesignsForBasePattern, "", "", "");

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