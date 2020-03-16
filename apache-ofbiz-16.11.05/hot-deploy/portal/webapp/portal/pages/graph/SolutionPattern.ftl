
<style>
    .modal-lg{
        width: 96 % !important;
        height: 90% !important;
        margin: 2% !important;
        padding: 0 !important;
        max-width:none !important;
    }
   .modal-lg > .modal-content {
        height: auto !important;
        min-height: 80% !important;
    }
    .modal-lg > .modal-header {
        border-bottom: 1px solid #9ea2a2 !important;
    }
    .modal-lg > .modal-footer {
        border-top: 1px solid #9ea2a2 !important;
    }
</style>

<div class="container-fluid">
    <div class="row py-3">
    <div class="navigationControl ml-3">
                <a href='javascript:window.history.back();'><span class="previousPage text-decoration-underline">< Back</span></a> | 
                <a href='javascript:window.history.forward();'><span class="nextPage">Next ></span></a>
            </div><br>
    <div class="col-12 pb-2 h3 text-center title">Solution Design</div>
        ​<div class="col-12 p-3">
            <div class="form-group probStatementForm">
                <a href="#" class="text-dark probStatementLink"><span class="text-justify probStatement h5"></span> <i class="fa fa-link linkIcon" aria-hidden="true" style="display:none;"></i></a>
                <p class="text-justify probStatementDescription"></p>
                <hr class="my-4">
            </div>
            <div class="form-group basePatternForm">
                <a href="#" class="text-dark basePatternLink"><span class="text-justify basePattern h5"></span></a>
                <span class="typeDataBP"></span> <i class="fa fa-link linkIconPT" aria-hidden="true" style="display:none;"></i>

                <a class="badge badge-secondary p-2 pull-right viewBpImage"  data-toggle="tooltip" data-placement="left" title="View Pattern" href="javascript:void(0);">
                    <i class="fa fa-picture-o fa-2x" data-target="#modalIMG" data-toggle="modal" aria-hidden="true"></i></a>
                <p class="text-justify basePatternDescription"></p>
                <hr class="my-4">
            </div>
            <div class="form-group solutionDesignForm">
              <div class="row">
                <label class="col-10 solutionDesign text-justify h5 pr-0"></label>
                <div class="col-2 text-center">
                  <span class="typeDataSD"></span>
                  <div class="mt-3">
                    <span data-toggle="modal" data-target="#viewDeploymentSummaryModal" class="">
                        <a class="btn btn-info m-1 viewDeploymentSummaryBtn" href="javascript:void(0);" aria-label="Edit"
                            data-toggle="tooltip" data-placement="left" title="Deployment Logs">
                            <i class="fa fa-list-alt fa-lg" aria-hidden="true"></i></a>
                    </span>

                    <span data-toggle="modal" data-target="#editFormModal">
                        <a class="btn btn-info m-1 editSD" href="javascript:void(0);" aria-label="Edit"
                            data-toggle="tooltip" data-placement="top" title="Edit data">
                            <i class="fa fa-pencil fa-lg" aria-hidden="true"></i></a>
                    </span>

                    <a class="btn btn-danger m-1 deleteSD" data-toggle="tooltip" data-placement="top" title="Delete Solution Design" href="javascript:void(0);" aria-label="Delete">
                        <i class="fa fa-trash-o fa-lg" aria-hidden="true"></i></a>

                  </div>
                </div>
              </div>  
                <p class="row-12 solutionDesignDescription text-justify"></p>
                <div class="p-3 row">
                    <div class="col-6 border rounded">
                        <label for="solutionDesignForces h5"><b>Forces</b></label><hr class="m-0">
                        <p class="solutionDesignForces"></p>
                    </div>
                    <div class="col-6 border rounded">
                        <label for="solutionDesignConsequences"><b>Consequences</b></label><hr class="m-0">
                        <p class="solutionDesignConsequences"></p>
                    </div>
                </div>
            </div>
        <div class="col-12 px-3">
                <div class="toastMsg m-0"></div>
                <#--  toastMsgForSolutionDesignCheck  -->
                <div class="text-center p-2" id="allButtonsDiv">
                  <button class="btn btn-primary m-1 p-1 approve" style="width: 100px;" tabindex="0" data-toggle="tooltip" data-placement="left" title="Approve Design">Approve</button>
                  <button class="btn btn-primary m-1 p-1 requestApprove" style="width: 100px;display:none;" tabindex="0" data-toggle="tooltip" data-placement="top" title="Request to Approve Design">Request</button>
                  <button class="btn btn-primary m-1 p-1 edit" style="width: 100px;" tabindex="0" data-toggle="tooltip" data-placement="top" title="Edit Design">
                      <i class="fa fa-pencil" aria-hidden="true"></i></button>
                  <span class="deployCheck">
                      <button class="btn btn-primary m-1 p-1 deploy" style="width: 100px;" type="button" tabindex="0" data-toggle="tooltip" data-placement="right" title="Deploy Design">Deploy</button>
                  </span>
                </div>
                <#--<img src="" srcset="" class="img-fluid img-thumbnail w-100 h-100" alt="..." id="solutionDesignImg">-->
                <div class='svgDiv img-fluid img-thumbnail w-100 p-2' style="height:auto;"></div>
            </div>
        </div>
    </div>
    <div aria-hidden="true" aria-labelledby="myModalLabel" class="modal fade" id="modalIMG" role="dialog" tabindex="-1">
	    <div class="modal-dialog modal-lg" role="document">
		    <div class="modal-content">
			    <div class="modal-body mb-0 p-3">
				    <#--  <img src="" alt="" style="width:100%">  -->
                    <div class='BPsvgDiv img-fluid img-thumbnail p-1' style="overflow:auto;"></div>
			    </div>
			  <div class="modal-footer">
                <span class="BPStatus"></span>
				<button class="btn btn-outline-danger btn-rounded btn-md ml-4 text-center" data-dismiss="modal" type="button">Close</button>
			</div>
		</div>
	</div>
</div>

<!-- Edit Modal -->
<div class="modal fade" id="editFormModal" tabindex="-1" role="dialog" aria-labelledby="editFormModalTitle" aria-hidden="true">
  <div class="modal-dialog modal-dialog" role="document">
    <div class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title" id="editFormModalTitle">Edit User Defined Design</h5>
        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
          <span aria-hidden="true">&times;</span>
        </button>
      </div>
      <div class="modal-body modalBody">
            <form id="solutionDesignPatternForm">
                <div class="form-group">
                    <label for="solutionDesignProblem">Solution Design Name</label>
                    <textarea type="text" name="solutionDesignName" class="form-control" required id="solutionDesignProblem"></textarea>
                </div>
                <input type="hidden" class="psid" name="psid" id="psid" value="">
                <div class="form-group">
                    <label for="solutionDesignDescription">Description</label>
                    <textarea class="form-control" name="solutionDesignDescription" required id="solutionDesignDescription" rows="3"></textarea>
                </div>
                <div class="form-group m-0">
                    <label>Forces</label>
                    <input class="form-control" name="solutionDesignForces" required id="solutionDesignForces" rows="3"/>
                    <label>Consequences</label>
                    <input class="form-control" name="solutionDesignConsequences" required id="solutionDesignConsequences" rows="3"/>
                    <div class="formToastMsg my-1"></div>
                </div>
            </form>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-secondary" data-dismiss="modal" id="closeBtnForEditModal">Close</button>
        <button type="button" class="btn btn-primary saveChangesBtn" id="saveChangesBtn">Save changes</button>
      </div>
    </div>
  </div>
</div>

<!-- View Deployment Summary Modal -->
<div class="modal fade bd-example-modal-lg" id="viewDeploymentSummaryModal" tabindex="-1" role="dialog" aria-labelledby="viewDeploymentSummaryModalTitle" aria-hidden="true">
  <div class="modal-dialog modal-lg" role="document">
    <div class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title" id="viewDeploymentSummaryModalTitle">Deployment Log Summary</h5>
        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
          <span aria-hidden="true">&times;</span>
        </button>
      </div>
      <div class="modal-body modalBody deploymentSummaryModalBody">
        <nav>
          <div class="nav nav-tabs" id="nav-tab" role="tablist">
            <a class="nav-item nav-link active" id="nav-compile-tab" data-toggle="tab" href="#nav-compile" role="tab" aria-controls="nav-compile" aria-selected="true">Compile Logs</a>
            <a class="nav-item nav-link" id="nav-runtime-tab" data-toggle="tab" href="#nav-runtime" role="tab" aria-controls="nav-runtime" aria-selected="false">Runtime Logs</a>
          </div>
        </nav>
        <div class="tab-content" id="nav-tabContent">
          <div class="tab-pane fade show active" id="nav-compile" role="tabpanel" aria-labelledby="nav-compile-tab">
            <center class="h4 mt-2 compileStatus"></center>
            <div class="compileTabData p-2">
                
            </div>
            <div class="compileTabDataInTableDiv">
              <table class="table table-striped table-condensed">
              <thead>
                <tr>
                  <th scope="col">Step Name</th>
                  <th scope="col">Component Data</th>
                  <th scope="col">Creation Details</th>
                  <th scope="col">Comments</th>
                </tr>
              </thead>
              <tbody class="compileTabTable">
                
              </tbody>
            </table>
            </div>
          </div>
          <div class="tab-pane fade" id="nav-runtime" role="tabpanel" aria-labelledby="nav-runtime-tab">
          <center class="h4 mt-2 runtimeStatus"></center>
          <div class="runtimeTabData p-2">
                
            </div>
            <div class="runtimeTabDataInTableDiv">
              <table class="table table-striped table-condensed">
              <thead>
                <tr>
                  <th scope="col">Step Name</th>
                  <th scope="col">Component Data</th>
                  <th scope="col">Creation Details</th>
                  <th scope="col">Comments</th>
                </tr>
              </thead>
              <tbody class="runtimeTabTable">
                
              </tbody>
            </table>
            </div>
          </div>
        </div>
      </div>
      <div class="modal-footer">
        <span class="text mr-3 p-2 deploymentStatus"></span>
        <button type="button" class="btn btn-secondary" data-dismiss="modal" id="closeBtnForDeploymentSummary">Close</button>
        <button type="button" class="btn btn-primary proceedBtn" id="proceedBtn">Proceed</button>
      </div>
    </div>
  </div>
</div>

<script type="module" src="../static/graphEditor/js/customJs/solutionDesign.js"></script>
