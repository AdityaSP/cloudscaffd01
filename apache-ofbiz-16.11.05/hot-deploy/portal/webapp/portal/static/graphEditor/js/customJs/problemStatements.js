import { App } from './app.js';

$(function () {
    $('[data-toggle="tooltip"]').tooltip()

    var userRole = App.userRole; console.log(userRole);

    App.toastMsg('Input text to find Problem Statements', '', '.searchResultsList');

    App.genericFetch('getTags', "POST", "", renderTags, "", notFound, "");

    $("#tags").on('click', '.tag', function (evt) {
        let tag = evt.target.textContent;
        let tagId = evt.target.id;
        App.genericFetch('getProblemStatementsByTagId', "POST", { "tagId": tagId }, renderSearchResultData, "", notFound, "");
    });

    var PS_input = document.querySelector(".inputSearch");

    let searchStr, searchType = 'typeProblemStatement';

    $('.applyBtn').on('click', function (event) {
        event.preventDefault();
        let selected = [];
        $('.custom-checkbox input:checked').each(function () {
            selected.push($(this).attr('name'));
        });

        console.log(selected.toString())
    });

    PS_input.addEventListener('keypress', e => {
        let selected = [], type, data;
        $('.custom-checkbox input:checked').each(function () {
            selected.push($(this).attr('name'));
        });
        type = selected.toString();
        console.log(type)

        if (e.key === 'Enter') {
            searchStr = event.target.value;
            if (searchStr != '') {
                data = { "inputSearch": searchStr, "type": type };
                console.log(data);
                //getDataForSearchResults(searchStr);
                App.genericFetch('searchProblemStatements', "POST", data, renderSearchResultData, "", "", "")
                App.clearInput(".inputSearch");

            } else {
                App.toastMsg('Please input data', 'info', '.toastMsg');
                setTimeout(function () {
                    $(".toastMsg").fadeOut(800);
                }, 1500);
                // $('.searchResultsList').children().remove();
                // App.toastMsg('Sorry, no results found', '', '.searchResultsList');
            }
        }
    });

    $('.unCheckAll').on('click', function (e) {
        $('.custom-checkbox input:checked').each(function () {
            this.checked = false;
        });
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

function renderSearchResultData(problems) {
    // Remove Existing Data in search result
    $('.searchResultsList').children().remove();
    App.loader('.searchResultsList');

    if (problems.length > 0) {
        for (let i = 0; i < problems.length; i++) {
            let queryStr = `psid=${problems[i].id}`;
            var row = `<li class="list-group-item"><a href="problemPatternSearch?${App.encrypt(queryStr)}"
            rel="noopener noreferrer">PS ${problems[i].id} - ${problems[i].problemStatement}</a></li>`;
            document.querySelector('.searchResultsList').insertAdjacentHTML("afterbegin", row)
        }
    } else {
        App.toastMsg('Sorry, no results found', '', '.searchResultsList');
    }
    App.clearLoader();
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
