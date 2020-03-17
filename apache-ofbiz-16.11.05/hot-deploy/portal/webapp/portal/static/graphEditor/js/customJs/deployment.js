import { App } from './app.js';

export const Deployment = {

    sdid: App.urlParams()['sdid'],
    psid: App.urlParams()['psid'],

    getLogs() {
        App.genericFetch('getScaffoldSolutionDesignlogs', 'POST', { 'sdid': Deployment.sdid }, Deployment.renderDataToModal, "", "", "");
    },

    renderDataToModal(logs) {
        let logList, isJustCompile = false;

        if (logs.compileScaffoldSolutionDesignResponse) {
            logList = logs.compileScaffoldSolutionDesignResponse; isJustCompile = true;
        } else if (logs.scaffoldLogList) {
            logList = logs.scaffoldLogList;
        } else {
            logList = [];
        }

        // Display all the Logs in modal
        if (logs && logs.message == 'success') {
            $('.viewDeploymentSummaryBtn').show();
            $('.edit').attr("disabled", true);

            if (isJustCompile) {
                Deployment.renderCompileData(logList);
            } else {

                for (let i = 0; i < logList.length; i++) {
                    let compileLog, runtimeLog, compileResults, runtimeResults,
                        compileStatus, runtimeStatus, compileData, runtimeData, count = 0;

                    (logList[i].compileLogs) ? compileLog = JSON.parse(logList[i].compileLogs) : compileLog = null;
                    (logList[i].runtimeLogs) ? runtimeLog = JSON.parse(logList[i].runtimeLogs) : runtimeLog = null;

                    (compileLog.compile_results) ? compileResults = compileLog.compile_results : compileResults = null;
                    (runtimeLog.deploy_results) ? runtimeResults = runtimeLog.deploy_results : runtimeResults = null;

                    (compileResults.compile_data) ? compileData = compileResults.compile_data : compileData = null;
                    (runtimeResults.compile_data) ? runtimeData = runtimeResults.compile_data : runtimeData = null;

                    console.log(compileLog)

                    if (compileResults.status) { $('.compileStatus').addClass('text-success'); }
                    else { $('.compileStatus').addClass('text-danger'); }
                    compileStatus = `COMPILE ${compileResults.status_code}`;
                    document.querySelector('.compileStatus').insertAdjacentHTML('afterbegin', compileStatus);

                    if (compileData && compileData.length > 0) {
                        for (let l = 0; l < compileData.length; l++) {

                            let stepCompileResults = compileData[l].step_compile_results;
                            // Rendering Compile Log's All Steps
                            Deployment.renderLogSteps(compileData[l], '.compileTabData')
                            count = count + stepCompileResults.length;

                            if (!App.isEmpty(stepCompileResults)) {
                                for (let j = 0; j < stepCompileResults.length; j++) {

                                    Deployment.renderTableRow(compileData[l].step_name, stepCompileResults[j], '.compileTabTable')
                                }
                            } else {
                                // console.log('stepCompileResults is empty');
                            }
                        }
                        if (count <= 0) {
                            $('.compileTabDataInTableDiv').hide();
                        }
                    }

                    Deployment.clearRuntimeTabData();

                    if (runtimeResults.status) { $('.runtimeStatus').addClass('text-success'); }
                    else { $('.runtimeStatus').addClass('text-danger'); }
                    runtimeStatus = `RUNTIME ${runtimeResults.status_code}`;
                    document.querySelector('.runtimeStatus').insertAdjacentHTML('afterbegin', runtimeStatus)

                    if (runtimeData && runtimeData.length > 0) {
                        count = 0;
                        for (let k = 0; k < runtimeData.length; k++) {

                            let stepRuntimeResults = runtimeData[k].step_compile_results; // TODO: change key name
                            // Rendering Runtime Log's All Steps
                            Deployment.renderLogSteps(runtimeData[k], '.runtimeTabData');

                            count = count + stepRuntimeResults.length;
                            if (!App.isEmpty(stepRuntimeResults)) {
                                for (let l = 0; l < stepRuntimeResults.length; l++) {

                                    //Rendring row data
                                    Deployment.renderTableRow(runtimeData[k].step_name, stepRuntimeResults[l], '.runtimeTabTable')
                                }
                            } else {
                                // console.log('runtimeLog is empty');
                            }
                        }
                        if (count <= 0) {
                            $('.runtimeTabDataInTableDiv').hide();
                        }
                    }
                }
            }
        } else {
            console.log("Pattern Approved but not deployed");
            $('.runtimeTabData').text('No logs found'); $('.runtimeTabDataInTableDiv').hide();
            $('.compileTabData').text('No logs found'); $('.compileTabDataInTableDiv').hide();
        }
    },

    compileDesign(str) {
        let successResponseRenderMethod, reCompile = false;

        if (str && str == 'recompile') {
            Deployment.loadingModal('Deployment In progress...');
            reCompile = true;
            successResponseRenderMethod = Deployment.deploySolutionDesign;
        } else {
            Deployment.loadingModal('Compilation is in progress...');
            successResponseRenderMethod = Deployment.checkCompilationData;
        }

        try {
            // Compile Graph Design
            App.genericFetch('compileScaffoldSolutionDesign', 'POST', { 'sdid': Deployment.sdid }, successResponseRenderMethod, reCompile, "", "");
        } catch (error) {
            console.log(error);
            Deployment.alertModal('Failed to Compile!!!');
            Deployment.closeLoadingModal();
        }
    },

    checkCompilationData(compileData, isParam, res) {
        // IF data has comiplation log and if not present hide the modal's complie tab
        Deployment.closeLoadingModal();

        // If isParam(false) thwn it first time / its not recompiling
        if (isParam) {
            // Render Data to Modal Table
            Deployment.renderDataToModal(compileData);
        }

        if (compileData && compileData.message == 'success') {

            // After Compilation open deployment summary modal then ask for proceed
            $('#viewDeploymentSummaryModal').modal('show');
            $('#proceedBtn').show();

            Deployment.renderDataToModal(compileData);

            $('#proceedBtn').on('click', function (e) {
                // close the running modal
                $('#viewDeploymentSummaryModal').modal('hide');
                //change the span text status

                // recompile and Call Deployment API
                Deployment.compileDesign('recompile');
            });

        } else {
            // show error message in modal if possible
            Deployment.alertModal('Compilation Failed!!!');
        }
    },

    deploySolutionDesign(data, param, res) {
        try {
            // if (param) { Deployment.loadingModal('Deployment In progress...'); } // if param(true) i.e. = "Re Compile"

            App.genericFetch('deployScaffoldSolutionDesign', 'POST', { 'sdid': Deployment.sdid }, Deployment.checkDeploymentData, "success", App.outputResponse, "error");
            // After successfull Deployment Change status to 'Deployed-Successful'
            // Display all the Logs in modal
        } catch (error) {
            console.log(error);
            Deployment.alertModal('Failed to Deploy!!!');
        }
    },

    checkDeploymentData(data, param, res) {
        Deployment.closeLoadingModal();

        console.log(data)

        if (data.message == 'success') {

            //TODO : render deploy_results in runtime tab

            Deployment.renderRuntimeData(data.deployScaffoldSolutionDesignResponse);

            Deployment.closeLoadingModal();
            setTimeout(function () { $('#viewDeploymentSummaryModal').modal('show'); }, 250);

            // remove all buttons like edit, deploy, approve,request
            $('#allButtonsDiv').hide();
        } else {
            Deployment.alertModal('Deployment Failed');
        }
    },

    dialog: "",

    loadingModal(msg) {
        Deployment.dialog = bootbox.dialog({
            message: `<p class="text-center mb-0"><i class="fa fa-spin fa-cog"></i>   ${msg}</p>`,
            closeButton: false,
        });
        // $('.loadingSpinnerMsg').text(`  ${msg}`);
        // $('#loadingSpinnerModal').modal('show');
    },

    closeLoadingModal() {
        // $('#loadingSpinnerModal').modal('hide');
        Deployment.dialog.modal('hide');
    },

    alertModal(msg) {
        bootbox.dialog({
            message: `<p class="text-center alert mb-0 h3">${msg}</p>`,
            buttons: {
                cancel: {
                    label: 'Close',
                    className: 'btn-danger'
                }
            }
        });
    },
    checkStatus(status) {
        status = status.toLowerCase();
        switch (status) {
            case 'success': return 'text text-success'; break;
            case 'warning': return 'text text-warning'; break;
            case 'error': return 'text text-danger'; break;
            default: console.log("Status Not found: " + status); return 'text-muted'; break;
        }
    },
    renderLogSteps(data, place) {
        let step = `
            <div class="text-justify my-1 step">
                <span class="h5 stepName">${data.step_name}</span> ( <span class="${Deployment.checkStatus(data.step_status_code)} stepStatusCode">${data.step_status_code}</span> )
                <span class="stepMessage">${data.step_message}</span>
            </div>`;
        $(place).append(step);
    },
    renderTableRow(stepName, rowData, place) {
        let jsonData = rowData.component, row;
        if (typeof (jsonData) != 'string') {
            jsonData = `<pre>${JSON.stringify(jsonData, null, '\t')}</pre>`;
        }
        row = `<tr>
                    <td>${stepName}</td>
                    <td>${jsonData}</td>
                    <td>${rowData.messages[0]}</td>
                    <td class='${Deployment.checkStatus(rowData.satus_code)}'>${rowData.satus_code}</td>
                </tr>`;
        $(place).append(row);
    },
    renderCompileData(logList) {
        let compileLog, compileResults, compileData, compileStatus, count = 0;

        (logList.compileLogs) ? compileLog = JSON.parse(logList.compileLogs) : compileLog = null;
        (compileLog.compile_results) ? compileResults = compileLog.compile_results : compileResults = null;
        (compileResults.compile_data) ? compileData = compileResults.compile_data : compileData = null;

        //Removing modal data
        Deployment.clearCompileTabData();

        if (compileResults.status) { $('.compileStatus').addClass('text-success'); }
        else { $('.compileStatus').addClass('text-danger'); }
        compileStatus = `COMPILE ${compileResults.status_code}`;
        document.querySelector('.compileStatus').insertAdjacentHTML('afterbegin', compileStatus)

        //TODO: same condition inside for loop ,change it
        if (compileData && compileData.length > 0) {
            for (let l = 0; l < compileData.length; l++) {

                let stepCompileResults = compileData[l].step_compile_results;
                // Rendering Compile Log's All Steps
                Deployment.renderLogSteps(compileData[l], '.compileTabData')
                count = count + stepCompileResults.length;

                if (!App.isEmpty(stepCompileResults)) {
                    for (let j = 0; j < stepCompileResults.length; j++) {

                        Deployment.renderTableRow(compileData[l].step_name, stepCompileResults[j], '.compileTabTable')
                    }
                } else {
                    // console.log('stepCompileResults is empty');
                }
            }
            if (count <= 0) {
                $('.compileTabDataInTableDiv').hide();
            }
        }
    },
    renderRuntimeData(logList) {

        console.log(logList)

        let runtimeLog, runtimeResults, runtimeStatus, runtimeData, count = 0;

        (logList.runtimeLogs) ? runtimeLog = JSON.parse(logList.runtimeLogs) : runtimeLog = null;

        (logList.deployLogs) ? runtimeLog = JSON.parse(logList.deployLogs) : runtimeLog = null; //TODO remove later

        (runtimeLog.compile_results) ? runtimeResults = runtimeLog.compile_results : runtimeResults = null;//TODO
        (runtimeResults.compile_data) ? runtimeData = runtimeResults.compile_data : runtimeData = null;

        //Removing modal data
        Deployment.clearRuntimeTabData();

        if (runtimeResults.status) { $('.runtimeStatus').addClass('text-success'); }
        else { $('.runtimeStatus').addClass('text-danger'); }
        runtimeStatus = `RUNTIME ${runtimeResults.status_code}`;
        document.querySelector('.runtimeStatus').insertAdjacentHTML('afterbegin', runtimeStatus)

        if (runtimeData && runtimeData.length > 0) {
            count = 0;
            for (let k = 0; k < runtimeData.length; k++) {

                let stepRuntimeResults = runtimeData[k].step_compile_results;
                // Rendering Runtime Log's All Steps
                Deployment.renderLogSteps(runtimeData[k], '.runtimeTabData');

                count = count + stepRuntimeResults.length;
                if (!App.isEmpty(stepRuntimeResults)) {
                    for (let l = 0; l < stepRuntimeResults.length; l++) {

                        //Rendring row data
                        Deployment.renderTableRow(runtimeData[k].step_name, stepRuntimeResults[l], '.runtimeTabTable')
                    }
                } else {
                    // console.log('runtimeLog is empty');
                }
            }
            if (count <= 0) {
                $('.runtimeTabDataInTableDiv').hide();
            }
        }
    },
    clearCompileTabData() {
        // $('#nav-runtime-tab').hide();

        //Clear the Compile status
        $('.compileStatus').text('');

        // Removing steps
        $('.compileTabData').children().remove()

        // Removing Row data
        $('.compileTabTable').children().remove();

    },
    clearRuntimeTabData() {
        //Clear the Compile status
        $('.runtimeStatus').text('');

        // Removing steps
        $('.runtimeTabData').children().remove()

        // Removing Row data
        $('.runtimeTabTable').children().remove();
    }
}
