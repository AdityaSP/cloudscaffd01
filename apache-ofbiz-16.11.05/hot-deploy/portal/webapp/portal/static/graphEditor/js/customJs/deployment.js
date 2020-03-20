import { App } from './app.js';

export const Deployment = {

    sdid: App.urlParams()['sdid'],
    psid: App.urlParams()['psid'],

    getLogs(checkFlow) { // From deployment geting deployed value
        App.genericFetch('getScaffoldSolutionDesignlogs', 'POST', { 'sdid': Deployment.sdid }, Deployment.renderDataToModal, checkFlow, App.outputResponse, "getScaffoldSolutionDesignlogs Fetch Error!");
    },

    renderDataToModal(logs, checkFlow) {
        let logList;

        if (logs.compileScaffoldSolutionDesignResponse) {
            logList = logs.compileScaffoldSolutionDesignResponse;
        } else if (logs.scaffoldLogList) {
            logList = logs.scaffoldLogList;
        } else {
            logList = [];
        }

        // Display all the Logs in modal
        if (logs && logs.message == 'success') {
            // $('.viewDeploymentSummaryBtn').show();
            $('.edit').attr("disabled", true);

            if (checkFlow == 'compile') {
                Deployment.renderCompileData(logList, checkFlow);
                Deployment.renderRuntimeData(false, checkFlow); // this will disable the runtime tab
            } else {
                if (logList.length > 0) {
                    if (checkFlow == 'fetchLogs') $('.viewDeploymentSummaryBtn').show();

                    Deployment.renderCompileData(logList[0], checkFlow);
                    Deployment.renderRuntimeData(logList[0], checkFlow);

                } else {
                    // Checking that, deployment response data is empty and getLogs() is calling after Deployment
                    if (checkFlow == 'deploy') {
                        $('.runtimeTabData').text('No data found');
                        $('.runtimeTabDataInTableDiv').hide();
                    }
                    // Checking that fetch response data is empty and getLogs() is calling on click of viewDeploymentSummaryBtn
                    else if (checkFlow == 'fetchLogs') {
                        Deployment.showNoDataFoundForCompileTab();
                        Deployment.showNoDataFoundForRuntimeTab();
                    }
                }
            }
        } else {
            console.log("Pattern Approved but not deployed");
            // $('.viewDeploymentSummaryBtn').hide();
            Deployment.showNoDataFoundForCompileTab();
            Deployment.showNoDataFoundForRuntimeTab();
        }
    },

    compileDesign(checkFlow) {
        let successResponseRenderMethod;

        if (checkFlow && checkFlow == 'compile') {
            Deployment.loadingModal('Compilation is in progress...');
            successResponseRenderMethod = Deployment.checkCompilationData;
        }
        else if (checkFlow && checkFlow == 'recompile') {
            successResponseRenderMethod = Deployment.deploySolutionDesign;
        }

        try {
            // Compile Graph Design
            App.genericFetch('compileScaffoldSolutionDesign', 'POST', { 'sdid': Deployment.sdid }, successResponseRenderMethod, checkFlow, App.outputResponse, "Fetch Error!!");
        } catch (error) {
            console.log(error);
            Deployment.alertModal('Failed to Compile!!!');
            Deployment.closeLoadingModal();
        }
    },

    checkCompilationData(compileData, checkFlow) {
        // IF data has comiplation log and if not present hide the modal's complie tab
        Deployment.closeLoadingModal();

        // If "reCompile" thwn it first time / its not recompiling
        if (checkFlow == 'reCompile') {
            // Render Data to Modal Table
            Deployment.renderDataToModal(compileData, checkFlow);
        }

        if (checkFlow == 'compile' && compileData && compileData.message == 'success') {

            Deployment.renderDataToModal(compileData, checkFlow);

            // After Compilation open deployment summary modal then ask for proceed
            $('#viewDeploymentSummaryModal').modal('show');

            $('#proceedBtn').on('click', function () {
                $('#viewDeploymentSummaryModal').modal('hide');
                //change the span text status

                // recompile and Call Deployment API
                checkFlow = 'recompile';
                Deployment.compileDesign(checkFlow);
            });

        }
        else if (compileData && compileData.message == 'error') {
            Deployment.alertModal(compileData.info);
        }
        else {
            // show error message in modal if possible
            Deployment.alertModal('Compilation Failed!!!');
        }
    },

    deploySolutionDesign(data, checkFlow, res) {
        Deployment.closeLoadingModal();
        try {
            if (checkFlow == 'recompile') {
                checkFlow = 'deploy';
                App.genericFetch('deployScaffoldSolutionDesign', 'POST', { 'sdid': Deployment.sdid }, Deployment.checkDeploymentData, checkFlow,
                    App.outputResponse, "deployScaffoldSolutionDesign Fetch Error");
                // After successfull Deployment Change status to 'Deployed-Successful'
            }
        } catch (error) {
            console.log(error);
            Deployment.alertModal('Failed to Deploy!!!');
        } finally {
            Deployment.checkLoadingModalIsStillPresent();
        }
    },

    checkDeploymentData(data, param) {
        // Deployment.closeLoadingModal();
        if (data.message == 'success') {
            Deployment.loadingModal('Deployment is in progress...');
            setTimeout(function () {
                Deployment.closeLoadingModal();
                Deployment.checkLoadingModalIsStillPresent();
                Deployment.alertModal("Deployment started click on logs button to view logs");
            }, 2000);
            try {
                // Fetching the latest Logs
                Deployment.getLogs(param); // if param == 'deploy' represents deployed data
            } catch (error) {
                Deployment.alertModal(error);
            } finally {
                Deployment.checkLoadingModalIsStillPresent();
            }

            // remove all buttons like edit, deploy, approve,request
            // $('#allButtonsDiv').hide();
        } else {
            Deployment.alertModal('Deployment Failed');
        }
    },

    dialog: "",

    loadingModal(msg, timer) {
        Deployment.dialog = bootbox.dialog({
            message: `<p class="text-center mb-0"><i class="fa fa-spin fa-cog"></i>   ${msg}</p>`,
            closeButton: false,
        });
        (timer) ? timer = timer : timer = 1500;
        setTimeout(function () {
            Deployment.dialog.modal('hide');
        }, timer);
    },

    closeLoadingModal() {
        Deployment.dialog.modal('hide');
        bootbox.hideAll();
        Deployment.checkLoadingModalIsStillPresent();
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
    checkLoadingModalIsStillPresent() {
        if ($('.modal:visible').length == 2) {
            bootbox.hideAll();
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
            if (jsonData.hasOwnProperty("mxCell")) delete jsonData['mxCell'];
            if (jsonData.hasOwnProperty("immediate_parent")) delete jsonData['immediate_parent'];
            if (jsonData.hasOwnProperty("parents")) delete jsonData['parents'];
            jsonData = `<pre>${JSON.stringify(jsonData, null, '\t')}</pre>`;
        }
        row = `<tr>
                    <td>${stepName}</td>
                    <td>${jsonData}</td>
                    <td>${rowData.messages[0]}</td>
                    <td class='${Deployment.checkStatus(rowData.status_code)}'>${rowData.status_code}</td>
                </tr>`;
        $(place).append(row);
    },
    renderCompileData(logList, checkFlow) {
        if (!App.isEmpty(logList)) {
            $('#nav-compile-tab').show();
            //Removing modal data
            Deployment.clearCompileTabData();

            let compileLog, compileResults, compileData, compileStatus,
                scaffoldStatus = logList.csStatus, count = 0;

            (!App.isEmpty(logList.compileLogs)) ?
                compileLog = JSON.parse(logList.compileLogs) : compileLog = null;

            (!App.isEmpty(compileLog) && !App.isEmpty(compileLog.compile_results)) ?
                compileResults = compileLog.compile_results : compileResults = null;

            if (!App.isEmpty(compileResults) && !App.isEmpty(compileResults.compile_data)) {
                compileData = compileResults.compile_data;

                console.log(compileResults)
                if (compileResults.status) {
                    $('.compileStatus').addClass('text-success');
                    checkFlow = 'deploy';
                    if (scaffoldStatus == 'DEPLOY_SUCCESS') {
                        $('#proceedBtn').hide();
                    } else {
                        $('#proceedBtn').show();
                    }
                }
                else {
                    $('.compileStatus').addClass('text-danger');
                }
                compileStatus = `COMPILE ${compileResults.status_code}`;
                $('.compileStatus').text(compileStatus);

                $('.deploymentStatus').show();
                if (!App.isEmpty(scaffoldStatus)) $('.deploymentStatus').text(`( ${scaffoldStatus.toUpperCase()} )`);
            }
            else {
                compileData = false;
            }

            if (compileData && compileData.length > 0) {
                for (let l = 0; l < compileData.length; l++) {

                    let stepCompileResults = compileData[l].step_compile_results;
                    $('.compileTabDataInTableDiv').show();

                    // Rendering Compile Log's All Steps
                    Deployment.renderLogSteps(compileData[l], '.compileTabData')
                    count = count + stepCompileResults.length;

                    if (!App.isEmpty(stepCompileResults)) {
                        for (let j = 0; j < stepCompileResults.length; j++) {

                            Deployment.renderTableRow(compileData[l].step_name, stepCompileResults[j], '.compileTabTable')
                        }
                    }// steps results
                }
                if (count <= 0) { $('.compileTabDataInTableDiv').hide(); }
                Deployment.checkLoadingModalIsStillPresent();
            } else {
                Deployment.showNoDataFoundForCompileTab();
            }
        } else {
            Deployment.hideCompileTab();
        }
    },
    renderRuntimeData(logList, checkFlow) {
        if (logList) {
            $('#nav-runtime-tab').show();
            //Removing modal data
            Deployment.clearRuntimeTabData();

            let runtimeLog, runtimeResults, runtimeStatus, runtimeData, count = 0;

            (!App.isEmpty(logList.runtimeLogs)) ?
                runtimeLog = JSON.parse(logList.runtimeLogs) : runtimeLog = null;

            (!App.isEmpty(runtimeLog) && !App.isEmpty(runtimeLog.deploy_results)) ?
                runtimeResults = runtimeLog.deploy_results : runtimeResults = null;

            if (!App.isEmpty(runtimeResults) && !App.isEmpty(runtimeResults.compile_data)) {
                runtimeData = runtimeResults.compile_data;
                if (runtimeResults.status) {
                    $('.runtimeStatus').addClass('text-success');
                }
                else {
                    $('.runtimeStatus').addClass('text-danger');
                }
                runtimeStatus = `RUNTIME ${runtimeResults.status_code}`;
                $('.runtimeStatus').text(runtimeStatus);
            }
            else { runtimeData = false; }

            if (runtimeData && runtimeData.length > 0) {
                count = 0;
                for (let k = 0; k < runtimeData.length; k++) {

                    let stepRuntimeResults = runtimeData[k].step_compile_results;
                    $('.runtimeTabDataInTableDiv').show();

                    // Rendering Runtime Log's All Steps
                    Deployment.renderLogSteps(runtimeData[k], '.runtimeTabData');

                    count = count + stepRuntimeResults.length;
                    if (!App.isEmpty(stepRuntimeResults)) {
                        for (let l = 0; l < stepRuntimeResults.length; l++) {
                            //Rendring row data
                            Deployment.renderTableRow(runtimeData[k].step_name, stepRuntimeResults[l], '.runtimeTabTable')
                        }
                    }// steps results
                }
                if (count <= 0) {
                    $('.runtimeTabDataInTableDiv').hide();
                }
                Deployment.checkLoadingModalIsStillPresent();
            } else {
                Deployment.showNoDataFoundForRuntimeTab();
                if (checkFlow == 'fetchLogs') $('#proceedBtn').hide();
            }
        } else {
            Deployment.hideRuntimeTab();
        }
    },
    clearCompileTabData() {
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
    },
    showNoDataFoundForCompileTab() {
        App.clearLoader(); Deployment.checkLoadingModalIsStillPresent();
        // Set compiletime tab data as no data found
        // Deployment.clearCompileTabData();
        $('.compileStatus').text('No Data Found');
        $('.compileTabDataInTableDiv').hide();
    },
    showNoDataFoundForRuntimeTab() {
        App.clearLoader(); Deployment.checkLoadingModalIsStillPresent();
        // Set runtime tab data as no data found
        // Deployment.clearRuntimeTabData();
        $('.runtimeStatus').text('No Data Found');
        $('.runtimeTabDataInTableDiv').hide();
    },
    hideCompileTab() {
        $('#nav-compile-tab').hide();
        $('.compileTabDataInTableDiv').hide();
    },
    hideRuntimeTab() {
        $('#nav-runtime-tab').hide();
        $('.runtimeTabDataInTableDiv').hide();
    },
}

window.Deployment = Deployment;
