import { App } from './app.js';

var isPSChecked = $('#checkPS')[0].checked, isPTChecked = $('#checkBP')[0].checked, isSDChecked = $('#checkSD')[0].checked;

$(function () {
    $('[data-toggle="tooltip"]').tooltip();

    var urlParams,
        userRole = App.userRole;
    if (window.location.search != "") {
        urlParams = App.urlParams();
    }
    console.log(urlParams, userRole);

    App.toastMsg('Input text to find Problem Statements', '', '.searchResultsList');
    App.genericFetch('getTags', "POST", "", renderTags, "", "", "");

    $("#tags").on('click', '.tag', function (evt) {
        let tag = evt.target.textContent,
            tagId = evt.target.id;
        clearSearchResults();
        App.genericFetch('getProblemStatementsByTagId', "POST", { "tagId": tagId }, renderProblemStatements, tagId, "", "");
    });

    if (urlParams && urlParams['tagid']) {
        clearSearchResults();
        App.genericFetch('getProblemStatementsByTagId', "POST", { "tagId": urlParams['tagid'] }, renderProblemStatements, "", "", "");
    }

    if (urlParams && urlParams['type'] && urlParams['status']) {
        clearSearchResults();
        let status = urlParams['status'], type = urlParams['type'].split(' ')[0],
            formData = {
                "status": status.replace(' ', '-').split(' ')[0],
                "type": type.toLowerCase()
            };
        console.log(formData);

        $('.custom-checkbox input:checked').each(function () { this.checked = false; });
        App.genericFetch('getChartData', "POST", formData, renderSearchResults, type, "", "");
    }

    let PS_input = document.querySelector(".inputSearch"),
        searchStr;
    $('.applyBtn').on('click', function (event) {
        event.preventDefault();
        let selected = [];
        $('.custom-checkbox input:checked').each(function () {
            selected.push($(this).attr('name'));
        });
        console.log(selected.toString())
    });

    $('.checkAll').on('click', function (e) {
        $('.custom-checkbox input').each(function () {
            this.checked = true;
        });
    });
    $('.unCheckAll').on('click', function (e) {
        $('.custom-checkbox input:checked').each(function () {
            this.checked = false;
        });
    });

    PS_input.addEventListener('keypress', e => {
        let selected = [], type, data, searchStrWithoutSpecialCharacters;
        if (e.key === 'Enter') {
            $('.custom-checkbox input:checked').each(function () {
                selected.push($(this).attr('name'));
            });
            if (selected.length == 3) {
                type = 'typeSearchAll';
            } else {
                type = selected.toString();
            }
            searchStr = event.target.value;
            // searchStrWithoutSpecialCharacters = searchStr.replace(/[^\w\s]/gi, '');
            // if (!App.isEmpty(searchStrWithoutSpecialCharacters) && !App.checkForSpecialChar(searchStr)) {
            if (searchStr != '') {
                if (type != '') {
                    data = { "inputSearch": searchStr, "type": type };
                    console.log(data);
                    // ajaxTest(searchStr);
                    App.genericFetch('search', "POST", data, renderSearchResults, "", "", "")
                    App.clearInput(".inputSearch");
                } else {
                    App.toastMsg('Please select the type', 'info', '.toastMsg');
                    setTimeout(function () {
                        $(".toastMsg").fadeOut(800);
                    }, 1500);
                }
            } else {
                App.toastMsg('Enter the search string', 'info', '.toastMsg');
                setTimeout(function () {
                    $(".toastMsg").fadeOut(800);
                }, 1500);
            }
            // } else {
            //     App.toastMsg('Invalid Search String', 'info', '.toastMsg');
            //     setTimeout(function () {
            //         $(".toastMsg").fadeOut(800);
            //         $('input:not(.submitBtn)').val('');
            //     }, 1500);
            // }
        }
    });
    if (userRole == "Planner" || userRole == "Administrator") {
        $("#problemStmtFormSubmitBtn").on('click', function (e) {
            let tag = App.getUniqueArray($('#tagInput').val().split(' ')),
                problemStatement = $('#problemStatement').val(),
                problemDescription = $('#problemDescription').val(),
                formData = {
                    "problemStatement": problemStatement,
                    "problemDescription": problemDescription,
                    "tag": tag.toString()
                };
            console.log(formData);
            if (!App.isEmpty(problemStatement) && !App.isEmpty(problemDescription) && tag.length > 0) {
                $('.submitBtn').val('Creating...');
                App.genericFetch('AddProblemStatement', 'POST', formData, submitForm, "", "", "");
                $('.submitBtn').attr("disabled", true);
            } else {
                App.toastMsg('Please enter all the details', 'failed', '.formToastMsg', true);
            }
        });
    } else {
        $('.submitBtn').attr("disabled", true);
    }
});

function submitForm(data) {
    $('.submitBtn').hide();
    if (data.message == 'success') {
        App.toastMsg('Creation Successful', 'success', '.formToastMsg', true);
        setTimeout(function () {
            window.location.reload();
        }, 1000);
    } else {
        App.toastMsg('Failed to create', 'failed', '.formToastMsg', 1500);
        App.clearInput('input:not(:button)');
        setTimeout(function () {
            $('.submitBtn').val('Create');
            $('.submitBtn').attr("disabled", false);
            $('.submitBtn').show();
        }, 1500);
    }
}

function clearSearchResults() {
    let isExpanded = $('.filterToggler').attr("aria-expanded");
    if (isExpanded == 'true') {
        $('.filterToggler').click();
    }
    $('.searchResultsList').children().remove();
    App.loader('.searchResultsList');
}

// Rendering Problem Statements based on TagID
function renderProblemStatements(problems, tagId) {
    if (problems && problems.length > 0) {
        for (let i = 0; i < problems.length; i++) {
            let queryStr = `psid=${problems[i].id}`;
            var row = `<li class="list-group-item"><a href="problemPatternSearch?${App.encrypt(queryStr)}"
                        rel="noopener noreferrer">${problems[i].id} : ${problems[i].problemStatement}</a></li>`;
            document.querySelector('.searchResultsList').insertAdjacentHTML("afterbegin", row);
        }
    } else {
        console.log("PS is empty");
        if (tagId) {
            console.log("PS is empty for tag id " + tagId);
            App.toastMsg('Sorry, no results found', '', '.searchResultsList');
        }

    }
}

function renderSearchResults(data, type) {
    // Remove Existing Data in search result
    clearSearchResults();

    let problems = data.ProblemStatements, PSLength = 0, patterns = data.basePatterns, PTLength = 0,
        solutionDesigns = data.solutionDesigns, SDLength = 0;

    if (problems) PSLength = problems.length;
    if (patterns) PTLength = patterns.length;
    if (solutionDesigns) SDLength = solutionDesigns.length;

    // Rendering Problem Statements
    renderProblemStatements(problems);

    // Rendering Patterns
    if (PTLength > 0) {
        for (let i = 0; i < PTLength; i++) {
            let queryStr, bpid = `bpid=${patterns[i].id}`, psid = patterns[i].psid;
            queryStr = `${bpid}&psid=${psid}`;

            var row = `<li class="list-group-item"><a href="basePattern?${App.encrypt(queryStr)}"
                        rel="noopener noreferrer">${patterns[i].id} : ${patterns[i].baseName}</a></li>`;
            document.querySelector('.searchResultsList').insertAdjacentHTML("afterbegin", row);
        }
        checkTheMatchingType(type);
    }

    // Rendering Solution Designs
    if (SDLength > 0) {
        for (let i = 0; i < SDLength; i++) {
            let queryStr, sdid = solutionDesigns[i].id,
                psid = solutionDesigns[i].psid, bpid;
            queryStr = `sdid=${sdid}&psid=${psid}`;

            if (solutionDesigns[i].bpid != null) {
                bpid = solutionDesigns[i].bpid;
                queryStr = `${queryStr}&bpid=${bpid}`;
            }
            var row = `<li class="list-group-item"><a href="solutionPattern?${App.encrypt(queryStr)}"
                        rel="noopener noreferrer">${solutionDesigns[i].id} : ${solutionDesigns[i].solutionDesignName}</a></li>`;
            document.querySelector('.searchResultsList').insertAdjacentHTML("afterbegin", row);
        }
        checkTheMatchingType(type);
    }
    if ((PSLength + PTLength + SDLength) <= 0) {
        App.toastMsg('Sorry, no results found', '', '.searchResultsList');
    }
}

function checkTheMatchingType(type) {
    type = type.toLowerCase();
    switch (type) {
        case 'pattern': $('#checkBP')[0].checked = true; break;
        case 'solution': $('#checkSD')[0].checked = true; break;
        default: console.log("Type Not found");
    }
}

function renderTags(tags) {
    if (!App.isEmpty(tags)) {
        $('#tags').show();
        for (var i = 0; i < tags.length; i++) {
            let htmlTags = `<a href="javascript:void(0)" class="badge badge-light mr-2 p-2 tag" id="${tags[i].id}">${tags[i].tagName}</a>`;
            document.querySelector("#tags").insertAdjacentHTML("beforeend", htmlTags);
        }
    } else {
        $('#tags').hide();
    }
}

function ajaxTest(searchStr) {
    $.ajax({
        method: "POST",
        url: "getAll",
        data: { "inputSearch": searchStr },
        success: function (res) {
            console.log(res);
        },
        error: function (err) {
            console.log(err);
        }
    });
}