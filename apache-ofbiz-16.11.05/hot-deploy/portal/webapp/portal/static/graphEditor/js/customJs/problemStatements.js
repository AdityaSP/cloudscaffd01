import { App } from './app.js';
import { ProblemStatement } from './Elements.js';

$(function () {

    App.toastMsg('Input text to find Problem Statements', '', '.searchResultsList');

    App.genericFetch('getTags', "POST", "", renderTags, "", notFound, "");

    $("#tags").on('click', '.tag', function (evt) {
        let tag = evt.target.textContent;
        let tagId = evt.target.id;
        App.genericFetch('getProblemStatementsByTagId', "POST", { "tagId": tagId }, renderSearchResultData, "", notFound, "");
    });

    var PS_input = document.querySelector(".inputSearch");

    let searchStr;
    PS_input.addEventListener('keypress', e => {
        if (e.key === 'Enter') {
            searchStr = event.target.value;
            if (searchStr != '') {
                console.log(searchStr);
                //                 getDataForSearchResults(searchStr);
                App.genericFetch('searchProblemStatements', "POST", { "inputSearch": searchStr }, renderSearchResultData, "", "", "")
                App.clearInput(".inputSearch");
            } else {
                App.addToastMsgDiv(".inputSearch");
                App.toastMsg('Please input data', 'info', '.toastMsg');
                setTimeout(function () {
                    $(".toastMsg").fadeOut(800);
                }, 1500);
                // $('.searchResultsList').children().remove();
                // App.toastMsg('Sorry, no results found', '', '.searchResultsList');
            }
        }
    });

    $("form").on('submit', function (e) {
        //e.preventDefault();
        let tag = App.getUniqueArray($('#tagInput').val().split(',')),
            formData = {
                "problemStatement": $('#problemStatement').val(),
                "problemDescription": $('#problemDescription').val(),
                "tag": tag.toString()
            }
        console.log(formData)
        App.genericFetch('AddProblemStatement', 'POST', formData, submitForm, "", "", "");
    });
});

function submitForm(data) {
    console.log(data)
    console.log("form submitted");
}

function renderSearchResultData(problems) {
    // Remove Existing Data in search result
    $('.searchResultsList').children().remove();
    App.loader('.searchResultsList');

    if (problems.length > 0) {
        for (let i = 0; i < problems.length; i++) {
            let queryStr = `psid=${problems[i].id}`;
            var row = `<li class="list-group-item"><a href="problemPatternSearch?${App.encrypt(queryStr)}"
            rel="noopener noreferrer">${problems[i].problemStatement}</a></li>`;
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
