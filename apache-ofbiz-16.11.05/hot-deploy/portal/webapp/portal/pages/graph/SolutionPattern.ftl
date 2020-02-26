
<div class="container-fluid">
    <div class="row py-3">
    <div class="col-12 pb-2 h3 text-center title">Solution Design</div>
        ​<div class="col-12 p-3">
            <div class="form-group probStatementForm">
                <label class="probStatement h5"></label>
                <p class="probStatementDescription"></p>
                <hr class="my-4">
            </div>
            <div class="form-group basePatternForm">
                <label class="basePattern h5"></label><span class="typeDataBP"></span>
                <a class="badge badge-secondary p-2 pull-right viewBpImage"  data-toggle="tooltip" data-placement="left" title="View Base Pattern" href="javascript:void(0);">
                    <i class="fa fa-picture-o fa-2x" data-target="#modalIMG" data-toggle="modal" aria-hidden="true"></i></a>
                <p class="basePatternDescription"></p>
                <hr class="my-4">
            </div>
            <div class="form-group solutionDesignForm">
                <label class="solutionDesign h5"></label><span class="typeDataSD"></span>
                
                <a class="btn btn-danger pull-right deleteSD" data-toggle="tooltip" data-placement="top" title="Delete Solution Design" href="javascript:void(0);" aria-label="Delete">
                    <i class="fa fa-trash-o fa-lg" aria-hidden="true"></i></a>
                <a class="btn btn-info pull-right mr-2 editSD" href="javascript:void(0);" aria-label="Edit"
                    data-toggle="modal" data-target="#editFormModal">
                    <i class="fa fa-pencil fa-lg" aria-hidden="true" data-toggle="tooltip" data-placement="left" title="Edit data"></i></a>
                
                <p class="solutionDesignDescription"></p>
                <div class="p-4 row">
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
                <div class="text-center p-2">
                <#--  <span class="d-inline-block deployCheck" tabindex="0" data-toggle="tooltip" title="">  -->
                <button class="btn btn-primary m-1 p-1 approve" style="width: 100px;">Approve</button>
                <button class="btn btn-primary m-1 p-1 edit" style="width: 100px;">
                    <i class="fa fa-pencil" aria-hidden="true"></i>
                    </button>
                <span class="deployCheck">
                    <button class="btn btn-primary m-1 p-1 deploy" style="width: 100px;" type="button" >Deploy</button>
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
				<button class="btn btn-outline-danger btn-rounded btn-md ml-4 text-center" data-dismiss="modal" type="button">Close</button>
			</div>
		</div>
	</div>
</div>

<!-- Modal -->
<div class="modal fade" id="editFormModal" tabindex="-1" role="dialog" aria-labelledby="editFormModalTitle" aria-hidden="true">
  <div class="modal-dialog modal-dialog-scrollable" role="document">
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
                    <input type="text" name="solutionDesignName" class="form-control" required id="solutionDesignProblem">
                </div>
                <input type="hidden" class="psid" name="psid" id="psid" value="">
                <div class="form-group">
                    <label for="solutionDesignDescription">Description</label>
                    <input class="form-control" name="solutionDesignDescription" required id="solutionDesignDescription" rows="3"/>
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
        <button type="button" class="btn btn-secondary" data-dismiss="modal" id="closeBtn">Close</button>
        <button type="button" class="btn btn-primary saveChangesBtn" id="saveChangesBtn">Save changes</button>
      </div>
    </div>
  </div>
</div>
<script type="module" src="../static/graphEditor/js/customJs/solutionDesign.js"></script>
