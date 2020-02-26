<div class="row">
    <div class="col-md-12">
        <div class="m-3 px-2">
            <div class="problemStatementDiv">
                <div class="form-group">
                    <a class="btn btn-info pull-right editPS" href="javascript:void(0);" aria-label="Edit"
                        data-toggle="modal" data-target="#editFormModal">
                        <i class="fa fa-pencil fa-lg" aria-hidden="true" data-toggle="tooltip" data-placement="left" title="Edit Problem Statement"></i>
                    </a>
                    <#--  <a class="btn btn-danger pull-right mr-2 deletePS" href="javascript:void(0);" aria-label="Delete">
                        <i class="fa fa-trash-o fa-lg" aria-hidden="true" data-toggle="tooltip" data-placement="left" title="Delete Problem Statement"></i>
                    </a>  -->

                    <p class="h4" id="probStatement"></p>
                    <p id="probStatementDescription"></p>
                </div>
                <div class="form-group problemTags" id='Ptags'></div>
            </div>
            <hr class="my-4">
            <br>
            <div class="row allSolutions">
                <div class="col-6">
                    <div class="tabbable" id="tabs">
                        <ul class="nav nav-tabs">
                            <li class="nav-item">
                                <a class="nav-link active show" href="#tab1" data-toggle="tab">Patterns</a>
                            </li>
                            <li class="nav-item">
                                <a class="nav-link" id="createBasePattern" href="#tab2"
                                   data-toggle="tab">Create Custom Pattern</a>
                            </li>
                        </ul>
                        <div class="tab-content">
                            <div class="tab-pane active" id="tab1">
                                <div class="form-group">
                                    <ul class="list-group basePatternResults">
                                    </ul>
                                </div>
                            </div>
                            <div class="tab-pane" id="tab2">
                                <div class="container my-2 py-2">
                                    <form id="basePatternForm">
                                        <div class="form-group">
                                            <label for="baseProblem">Pattern Name</label>
                                            <input type="text" name="baseName" class="form-control" required id="baseProblem">
                                        </div>
                                        <input type="hidden" class="psid" name="psid" id="psid" value="">
                                        <div class="form-group">
                                            <label for="baseProblemDescription">Pattern Description</label>
                                            <input class="form-control" name="baseDescription" required id="baseProblemDescription"
                                                   rows="3"/>
                                        </div>
                                        <div class="form-group">
                                            <label>Forces</label>
                                            <input class="form-control" name="baseForces" required id="baseForces"
                                                   rows="3"/>
                                            <label>Consequences</label>
                                            <input class="form-control" name="baseConsequences" required id="baseConsequences"
                                                   rows="3"/>
                                        </div>
                                        <#--  <div>
                                            <div class="toastMsg"></div>
                                            <input type="button" value="Create" class="btn btn-primary pull-right submitBtn" id="basePatternFormSubmitBtn" style="width: 100px;">
                                        </div>  -->
                                        <div class="row">
                                            <div class="col-9">
                                                <div class="toastMsg"></div>
                                            </div>
                                            <div class="col-3">
                                                <input type="button" value="Create" class="btn btn-primary pull-right submitBtn" id="basePatternFormSubmitBtn" style="width: 125px;">
                                            </div>
                                        </div>
                                    </form>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="col-6 solutionPatternDiv">
                    <div class="tabbable" id="tabss">
                        <ul class="nav nav-tabs">
                            <li class="nav-item">
                                <a class="nav-link active show" href="#solutionTab1" data-toggle="tab">Solution Design</a>
                            </li>
                            <li class="nav-item">
                                <a class="nav-link" id="createBasicPattern" href="#solutionTab2"
                                   data-toggle="tab">Create Solution Design</a>
                            </li>
                        </ul>
                        <div class="tab-content">
                            <div class="tab-pane active" id="solutionTab1">
                                <div class="form-group">
                                    <ul class="list-group solutionPatternResults">
                                    </ul>
                                </div>
                            </div>
                            <div class="tab-pane" id="solutionTab2">
                                <div class="container my-2 py-2">
                                    <form id="solutionDesignForm">
                                        <div class="form-group">
                                            <label>Solution Design Name</label>
                                            <input type="text" class="form-control" name="solutionDesignName" required
                                                   id="solutionDesignName">
                                        </div>
                                        <input type="hidden" class="psid" name="psid" value="">
                                        <input type="hidden" class="bpid" name="bpid" value="">
                                        <div class="form-group">
                                            <label>Description</label>
                                            <input type="text" class="form-control" name="solutionDesignDesc" required
                                                   id="solutionDesignDescription" rows="3">
                                        </div>
                                        <div class="form-group">
                                            <label>Forces</label>
                                            <input class="form-control" name="solutionForces" required id="solutionForces"
                                                   rows="3"/>
                                            <label>Consequences</label>
                                            <input class="form-control" name="solutionConsequences" required id="solutionConsequences"
                                                   rows="3"/>
                                        </div>
                                        <#--  <div>
                                            <div class="toastMsg"></div>
                                            <input type="button" value="Create" class="btn btn-primary pull-right submitBtn" id="solutionDesignFormSubmitBtn" style="width: 100px;">
                                        </div>  -->
                                        <div class="row">
                                            <div class="col-9">
                                                <div class="toastMsg"></div>
                                            </div>
                                            <div class="col-3">
                                                <input type="button" value="Create" class="btn btn-primary pull-right submitBtn" id="solutionDesignFormSubmitBtn" style="width: 125px;">
                                            </div>
                                        </div>
                                    </form>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
<!-- Modal -->
<div class="modal fade" id="editFormModal" tabindex="-1" role="dialog" aria-labelledby="editFormModalTitle" aria-hidden="true">
  <div class="modal-dialog modal-dialog-scrollable" role="document">
    <div class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title" id="editFormModalTitle">Edit Problem Statement</h5>
        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
          <span aria-hidden="true">&times;</span>
        </button>
      </div>
      <div class="modal-body modalBody">
            <form id="basePatternForm">
              <div class="form-group">
                <label>Problem Statement</label>
                <div class="input-container">
                    <textarea type="text" class="form-control" id="problemStatement" required></textarea>
                </div>
            </div>
            <div class="form-group">
                <label>Problem Description</label>
                <div class="input-container problemDescription">
                    <textarea type="text" class="form-control" id="problemDescription" required></textarea>
                </div>
            </div>
            <#--  <div class="form-group">
                <label>Tag</label>
                <div class="input-container">
                    <input type="text" class="form-control" id="tagInput" required>
                </div>
                <label for="tag">Note : Please use space for entering multiple tags. </label>
            </div>    -->
            <div class="formToastMsg my-1"></div>
            </form>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-secondary" data-dismiss="modal" id="closeBtn">Close</button>
        <button type="button" class="btn btn-primary saveChangesBtn" id="saveChangesBtn">Save changes</button>
      </div>
    </div>
  </div>
</div>
<script type="module" src="../static/graphEditor/js/customJs/getPatterns.js"></script>
