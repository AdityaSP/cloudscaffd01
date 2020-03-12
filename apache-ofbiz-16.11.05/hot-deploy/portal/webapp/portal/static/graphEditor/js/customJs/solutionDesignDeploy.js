import { App } from './app.js';

var urlParam = App.urlParams();

function getLogs() {
    console.log('get logs');
    console.log(urlParam)
    // Display all the Logs in modal
    App.genericFetch('getScaffoldBySdid', 'POST', { 'sdid': urlParam['sdid'] }, renderDataToModal, "", "", "");
}

function renderDataToModal(data) {

    if (data) {//message == 'success') {
        $('#viewDeploymentSummaryModal').modal('show');
        // $('#proceedBtn').show();
        $('.viewDeploymentSummaryBtn').show();

        for (let i = 0; i < data.length; i++) {

            // let row = `<tr>
            //             <td>${data[i].runtimeLogs}</td>
            //             <td>Otto</td>
            //             <td>@mdo</td></tr>`;

            $('.deploymentStatus').text(data[i].csStatus.toUpperCase());
            $('.runtimeTabData').text(data[i].runtimeLogs);

        }

        $('#proceedBtn').on('click', function (e) {
            // Call Deployment API
            // this.deploySolutionDesign();
        });
    } else {
        // Call Deployment API
        // this.deploySolutionDesign();
    }
}

function deploySolutionDesign() {
    App.modalFormResponse({ 'message': 'success', 'info': `${sdid}, ${psid}` }, { 'submitBtn': 'proceedBtn', 'closeBtn': 'closeBtnForDeploymentSummary' });
    // App.genericFetch('#', 'POST', { 'sdid': sdid, 'psid': psid }, App.modalFormResponse, { 'submitBtn': 'proceedBtn', 'closeBtn': 'closeBtnForDeploymentSummary' }, App.outputResponse, "ERROR!");
    // After successfull Deployment Change status to 'Deployed-Successful'
    // Display all the Logs in modal
}

function checkCompilationData() {
    // IF data has comiplation log and if not present hide the modal's complie tab
}
function checkRuntimeData() {
    // IF data has runtime log and if not present hide the modal's runtime tab
} 