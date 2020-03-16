import { App } from './app.js';

export const Deployment = {

    sdid: App.urlParams()['sdid'],
    psid: App.urlParams()['psid'],

    getLogs() {
        //TODO: Check if deployment summary/log is available, if present show them in modal
        App.genericFetch('getScaffoldSolutionDesignlogs', 'POST', { 'sdid': Deployment.sdid }, Deployment.renderDataToModal, "", "", "");
    },

    renderDataToModal(logs) {

        // console.log(logs)
        // Display all the Logs in modal
        if (logs.message == 'success') {
            $('.viewDeploymentSummaryBtn').show();

            let logList = logs.scaffoldLogList;

            for (let i = 0; i < logList.length; i++) {
                let compileLog = JSON.parse(logList[i].compileLogs),
                    runtimeLog = JSON.parse(logList[i].runtimeLogs),
                    table,
                    compileData = compileLog.compile_data;

                if (compileLog.status) { $('.compileStatus').addClass('text-success'); }
                else { $('.compileStatus').addClass('text-danger'); }

                $('.compileStatus').text(`COMPILE ${compileLog.status_code.toUpperCase()}`)

                if (compileData && compileData.length > 0) {
                    for (let l = 0; l < compileData.length; l++) {
                        let step = `
                        <div class="text-justify my-1 step-${l + 1}">
                            <span class="h5 stepName">${compileData[l].step_name}</span> ( <span class="text-${compileData[l].step_status_code.toLowerCase()} stepStatusCode">${compileData[l].step_status_code}</span> )
                            <span class="stepMessage">${compileData[l].step_message}</span>
                        </div>`,
                            stepCompileResults = compileData[l].step_compile_results;
                        $('.compileTabData').append(step);

                        console.log(stepCompileResults)

                        if (!App.isEmpty(stepCompileResults)) {
                            for (let j = 0; j < stepCompileResults.length; j++) {
                                let jsonData = stepCompileResults[j].component;

                                if (typeof (jsonData) != 'string') {
                                    jsonData = `<pre>${JSON.stringify(jsonData,null,'\t')}</pre>`;
                                    console.log(jsonData)
                                }

                                let table = `<tr>
                                            <td>${compileData[l].step_name}</td>
                                            <td>${jsonData}</td>
                                            <td>${stepCompileResults[j].messages[0]}</td>
                                            <td>${stepCompileResults[j].satus_code}</td>
                                        </tr>`;
                                $('.compileTabTable').append(table);
                            }
                        } else {
                            console.log('stepCompileResults is empty');
                            // $('.compileTabDataInTableDiv').hide();
                            // $('.compileTabData').html(`<span class="m-2 compileDivTitle">No logs found</span>`);
                        }
                    }
                }

                console.log(compileLog);

                // if (!App.isEmpty(runtimeLog)) {
                //     for (let k = 0; k < runtimeLog.length; k++) {
                //         table = `<tr>
                //                     <th scope="row">${k + 1}</th>
                //                     <td>${runtimeLog[k].componentData.label}</td>
                //                     <td>${runtimeLog[k].creationDetails.AttachTime}</td>
                //                     <td>${runtimeLog[k].comments}</td>
                //                 </tr>`;
                //         $('.runtimeTabTable').append(table);
                //     }
                // } else {
                //     console.log('runtimeLog is empty');
                //     $('.runtimeTabDataInTableDiv').hide();
                //     $('.runtimeTabData').html(`<span class="m-2 compileDivTitle">No logs found</span>`);
                // }


                // $('.deploymentStatus').text(logs.message.toUpperCase());
                // $('.compileTabData').text(logList[i].compileLogs);
                // $('.runtimeTabData').text(logList[i].runtimeLogs);
            }
        } else {
            console.log("Pattern Approved but not deployed");
            $('.runtimeTabData').text('No logs found');
            $('.compileTabData').text('No logs found');
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

    checkCompilationData(compileData) {
        // IF data has comiplation log and if not present hide the modal's complie tab
        Deployment.closeLoadingModal();

        if (!compileData) {//.message == 'success') {
            // After Compilation open deployment summary modal then ask for proceed
            $('#viewDeploymentSummaryModal').modal('show');
            $('#proceedBtn').show();

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
            if (!param) { Deployment.loadingModal('Deployment In progress...'); } // if param(true) i.e. = "Re Compile"

            App.genericFetch('deployScaffoldSolutionDesign', 'POST', { 'sdid': Deployment.sdid, 'psid': Deployment.psid }, Deployment.checkDeploymentData, "success", App.outputResponse, "error");
            // After successfull Deployment Change status to 'Deployed-Successful'
            // Display all the Logs in modal
        } catch (error) {
            console.log(error);
            Deployment.alertModal('Failed to Deploy!!!');
        }
    },

    checkDeploymentData(data, param, res) {
        Deployment.closeLoadingModal();

        if (param == "success") {//(data.message == 'success') {
            Deployment.alertModal('Deployment Successful');
            // App.modalFormResponse({ 'message': 'success', 'info': `${ sdid }, ${ psid } ` }, { 'submitBtn': 'proceedBtn', 'closeBtn': 'closeBtnForDeploymentSummary' });

            // remove all buttons like edit, deploy, approve,request
            $('#allButtonsDiv').hide();
        } else {
            Deployment.alertModal('Deployment Failed');
        }
    },

    dialog: "",

    loadingModal(msg) {
        Deployment.dialog = bootbox.dialog({
            message: `<p class="text-center mb-0"><i class="fa fa-spin fa-cog"></i>  ${msg}</p>`,
            closeButton: false
        });
    },

    closeLoadingModal() {
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
}