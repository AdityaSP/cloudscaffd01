import { App } from './app.js';

$(function () {
    $('[data-toggle="tooltip"]').tooltip()

    var userRole = App.userRole; console.log(userRole);

    App.toastMsg('Input text to find Problem Statements', '', '.searchResultsList');

    App.genericFetch('getTags', "POST", "", renderTags, "", notFound, "");

    $("#tags").on('click', '.tag', function (evt) {
        let tag = evt.target.textContent;
        let tagId = evt.target.id;
        // Remove Existing Data in search result
        clearSearchResults();
        App.genericFetch('getProblemStatementsByTagId', "POST", { "tagId": tagId }, renderProblemStatements, "", notFound, "");
    });

    var PS_input = document.querySelector(".inputSearch");

    let searchStr;

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
        let selected = [], type, data;
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
            if (searchStr != '') {
                if (type != '') {
                    data = { "inputSearch": searchStr, "type": type };
                    console.log(data);
                    //getDataForSearchResults(searchStr);
                    App.genericFetch('search', "POST", data, checkBeforeRender, "", "", "")
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
                // $('.searchResultsList').children().remove();
                // App.toastMsg('Sorry, no results found', '', '.searchResultsList');
            }
        }
    });

    if (userRole == "Planner" || userRole == "Administrator") { // || userRole == "Deployer"
        $("#problemStmtFormSubmitBtn").on('click', function (e) {
            let tag = App.getUniqueArray($('#tagInput').val().split(' ')),
                formData = {
                    "problemStatement": $('#problemStatement').val(),
                    "problemDescription": $('#problemDescription').val(),
                    "tag": tag.toString()
                };
            console.log(formData);
            $('.submitBtn').val('Creating...');
            App.genericFetch('AddProblemStatement', 'POST', formData, submitForm, "", "", "");
            $('.submitBtn').attr("disabled", true);
        });
    } else {
        $('.submitBtn').attr("disabled", true);
    }

});

function submitForm(data) {
    console.log(data)
    window.location.reload();
}

function checkBeforeRender(data) {
    // Remove Existing Data in search result
    clearSearchResults();

    if (data.basePatterns) {
        renderBasePatterns(data.basePatterns);
    }
    if (data.solutionDesigns) {
        renderSolutionDesigns(data.solutionDesigns);
    }
    if (data.ProblemStatements) {
        renderProblemStatements(data.ProblemStatements);
    }
    if ((data.basePatterns && data.basePatterns.length == 0) &&
        (data.solutionDesigns && data.solutionDesigns.length == 0) &&
        (data.ProblemStatements && data.ProblemStatements.length == 0)) {
        App.toastMsg('Sorry, no results found', '', '.searchResultsList');
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

// function renderSearchResultData(problems, name) {
//     let url, queryStr, psid, bpid, sdid;
//     if (App.isEmpty(name) || name == "problemStatement") {
//         name = "problemStatement";
//         url = "problemPatternSearch?";
//     } else if (name == "baseName") {
//         url = "basePattern?";
//     } else if (name == "solutionDesignName") {
//         url = "solutionPattern?";
//     }

//     if (problems.length > 0) {
//         for (let i = 0; i < problems.length; i++) {
//             psid = `psid=${problems[i].id}`;

//             if (bpid in problems[i]) {
//                 bpid = problems[i].bpid;
//                 queryStr = `${psid}&bpid=${bpid}`;
//             }
//             if (sdid in problems[i]) {
//                 sdid = problems[i].sdid;
//                 queryStr = `${psid}&sdid=${sdid}`;
//             }
//             if ((bpid in problems[i]) && (sdid in problems[i])) {
//                 bpid = problems[i].bpid;
//                 sdid = problems[i].sdid;
//                 queryStr = `${psid}&bpid=${bpid}&sdid=${sdid}`;
//             }

//             console.log(queryStr);

//             var row = `<li class="list-group-item"><a href="${url}${App.encrypt(queryStr)}"
//             rel="noopener noreferrer">${problems[i].id} : ${problems[i][name]}</a></li>`;
//             document.querySelector('.searchResultsList').insertAdjacentHTML("afterbegin", row)
//         }
//     } else {
//         App.toastMsg('Sorry, no results found', '', '.searchResultsList');
//     }
//     App.clearLoader();

//     if ($('.searchResultsList')[0].firstChild == "") {
//         App.toastMsg('Sorry, no results found', '', '.searchResultsList');
//     }
// }

function renderProblemStatements(problems) {
    if (problems.length > 0) {
        for (let i = 0; i < problems.length; i++) {
            let queryStr = `psid=${problems[i].id}`;
            var row = `<li class="list-group-item"><a href="problemPatternSearch?${App.encrypt(queryStr)}"
        rel="noopener noreferrer">${problems[i].id} - ${problems[i].problemStatement}</a></li>`;
            document.querySelector('.searchResultsList').insertAdjacentHTML("afterbegin", row);
        }
    } else {
        App.toastMsg('Sorry, no results found', '', '.searchResultsList');
    }
}

function renderBasePatterns(basePattern) {
    if (basePattern.length > 0) {
        for (let i = 0; i < basePattern.length; i++) {
            let queryStr, bpid = `bpid=${basePattern[i].id}`, psid;

            psid = basePattern[i].psid;
            queryStr = `${bpid}&psid=${psid}`;

            console.log(queryStr);

            var row = `<li class="list-group-item"><a href="basePattern?${App.encrypt(queryStr)}"
        rel="noopener noreferrer">${basePattern[i].id} - ${basePattern[i].baseName}</a></li>`;
            document.querySelector('.searchResultsList').insertAdjacentHTML("afterbegin", row);
        }
    } else {
        App.toastMsg('Sorry, no results found', '', '.searchResultsList');
    }
}
function renderSolutionDesigns(solutionDesign) {
    if (solutionDesign.length > 0) {
        for (let i = 0; i < solutionDesign.length; i++) {
            let queryStr, sdid = `sdid=${solutionDesign[i].id}`, psid, bpid;
            queryStr = `${sdid}&psid=${solutionDesign[i].psid}`;

            if (bpid in solutionDesign[i]) {
                bpid = solutionDesign[i].bpid;
                queryStr = `${queryStr}&bpid=${bpid}`;
            }

            console.log(queryStr);

            var row = `<li class="list-group-item"><a href="solutionPattern?${App.encrypt(queryStr)}"
        rel="noopener noreferrer">${solutionDesign[i].id} - ${solutionDesign[i].solutionDesignName}</a></li>`;
            document.querySelector('.searchResultsList').insertAdjacentHTML("afterbegin", row);
        }
    } else {
        App.toastMsg('Sorry, no results found', '', '.searchResultsList');
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

function getDataForSearchResults(searchStr) { // Remove this method
    $.ajax({
        method: "POST",
        url: "getAPCDetailsInCount",
        data: { "inputSearch": searchStr },
        success: function (res) {
            console.log(res);
        },
        error: function (err) {
            console.log(err);
        }
    });
}
function notFound() {
    App.toastMsg(`Nothing found related to '${searchStr}'`, "failed", ".searchResultsList", false)
}
