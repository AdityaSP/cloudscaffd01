
<div class="container-fluid h-100">
    <div class="row py-3">
    <div class="navigationControl ml-3">
                <a href='javascript:window.history.back();' id='previousPage'><span class="text-decoration-underline">< Back</span></a> | 
                <a href='javascript:window.history.forward();'><span class="nextPage">Next ></span></a>
            </div><br>
    <div class="col-12 pb-2 h3 text-center title">User Defined Pattern</div>
        ​<div class="col-12 p-3">
            <div class="form-group probStatementForm">
                <a href="" class="text-dark probStatementLink"><span class="probStatement h5"></span> <i class="fa fa-link linkIcon" aria-hidden="true" style="display:none;"></i></a>
                <p class="probStatementDescription"></p>
            </div>
            <hr class="my-4">
            <div class="form-group basePatternForm">
                <label class="basePattern h5"></label><span class="typeDataBP"></span>
                
                <a class="btn btn-danger pull-right deleteBP" data-toggle="tooltip" data-placement="top" title="Delete Pattern" href="javascript:void(0);" aria-label="Delete">
                    <i class="fa fa-trash-o fa-lg" aria-hidden="true"></i></a>
                <a class="btn btn-info pull-right mr-2 editBP" href="javascript:void(0);" aria-label="Edit"
                    data-toggle="modal" data-target="#editFormModal">
                    <i class="fa fa-pencil fa-lg" aria-hidden="true" data-toggle="tooltip" data-placement="left" title="Edit data"></i></a>
                
                <p class="basePatternDescription"></p>
                <div class="px-4 row">
                    <div class="col-6 border rounded">
                        <label for="basePatternForces"><b>Forces</b></label><hr class="m-0">
                        <p class="basePatternForces"></p>
                    </div>
                    <div class="col-6 border rounded">
                        <label for="basePatternConsequences"><b>Consequences</b></label><hr class="m-0">
                        <p class="basePatternConsequences"></p>
                    </div>
                </div>
            </div>
        </div>
        <div class="col-12 px-3">
            <div class="toastMsg m-0"></div>
            <div class="text-center p-2">
                <button class="btn btn-primary m-1 p-1 approve" style="width: 150px;height:auto;" tabindex="0" data-toggle="tooltip" data-placement="left" title="Approve Pattern">Approve</button>
                <button class="btn btn-primary m-1 p-1 edit" style="width: 150px;" data-toggle="tooltip" data-placement="right" title="Edit Pattern">Edit</button>
            </div>
            <#--  <img src="" class="img-fluid img-thumbnail w-100 h-100" alt="" id="basePatternImg">  -->
            <div class='svgDiv img-fluid img-thumbnail w-100 p-2' style="height:auto;"></div>
        </div>
    </div>
</div>

<!-- Modal -->
<div class="modal fade" id="editFormModal" tabindex="-1" role="dialog" aria-labelledby="editFormModalTitle" aria-hidden="true">
  <div class="modal-dialog modal-dialog-scrollable" role="document">
    <div class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title" id="editFormModalTitle">Edit User Defined Pattern</h5>
        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
          <span aria-hidden="true">&times;</span>
        </button>
      </div>
      <div class="modal-body modalBody">
            <form id="basePatternForm">
                <div class="form-group">
                    <label for="baseProblem">Pattern Name</label>
                    <input type="text" name="baseName" class="form-control" required id="baseProblem">
                </div>
                <input type="hidden" class="psid" name="psid" id="psid" value="">
                <div class="form-group">
                    <label for="baseProblemDescription">Pattern Description</label>
                    <input class="form-control" name="baseDescription" required id="baseProblemDescription" rows="3"/>
                </div>
                <div class="form-group m-0">
                    <label>Forces</label>
                    <input class="form-control" name="baseForces" required id="baseForces" rows="3"/>
                    <label>Consequences</label>
                    <input class="form-control" name="baseConsequences" required id="baseConsequences" rows="3"/>
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
<script type="module" src="../static/graphEditor/js/customJs/basePattern.js"></script>
