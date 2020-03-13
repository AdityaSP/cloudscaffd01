<style>
.has-search .form-control {
    padding-left: 2.375rem;
}
.has-search .form-control-feedback {
    position: absolute;
    z-index: 2;
    display: block;
    width: 2.375rem;
    height: 2.375rem;
    line-height: 2.375rem;
    text-align: center;
    pointer-events: none;
    color: #aaa;
}
.card{
    border:0px !important;
}
</style>
<div class="container-fluid">
        <div class="row">
            <div class="col-md-12 p-4">
            <#--  <div class="navigationControl">
                <a href='javascript:window.history.back();'><span class="previousPage text-decoration-underline">< Back</span></a> | 
                <a href='javascript:window.history.forward();'><span class="nextPage">Next ></span></a>
            </div><br>  -->
                <div class="tabbable" id="tabs">
                    <ul class="nav nav-tabs">
                        <li class="nav-item">
                            <a class="nav-link active show" href="#tab1" data-toggle="tab">Problem Statements</a>
                        </li>
                        <li class="nav-item">
                            <a class="nav-link" href="#tab2" data-toggle="tab">Create Problem Statements</a>
                        </li>
                    </ul>
                    <div class="tab-content">
                        <div class="tab-pane active" id="tab1">
                            <div class="container my-2 py-2 searchProblems">
                                <div class="form-group" id='tags' class="tags" style="display:none">
                                    <p><label>Tags</label></p>
                                </div>
                                <br>
                                    <div class="btn-group has-search  w-100">
                                        <span class="fa fa-search form-control-feedback"></span>
                                        <input type="text" name="inputSearch" class="form-control inputSearch" required placeholder="Search">
                                        <span class="btn btn-link pull-right border rounded ml-1"  data-toggle="tooltip" data-placement="right" title="Filter">
                                            <i class="fa fa-filter filterToggler" aria-hidden="true" type="button" data-toggle="collapse" data-target="#collapsePatternType" aria-expanded="false" aria-controls="collapseExample"></i>
                                        </span>
                                    </div>
                                    
                                    <div class="collapse mt-1" id="collapsePatternType">
                                        <div class="card card-body p-0 mb-3">
                                            <div class="row"> 
                                                <div class="col-12">
                                                    <div class="container my-4">
                                                    <span class="title font-weight-bold">Select Type</span>
                                                    <span class="pull-right">
                                                        <a href="javascript:void(0);" class="checkAll text-decoration-underline">Select All</a> /
                                                        <a href="javascript:void(0);" class="unCheckAll text-decoration-underline">Clear All</a>
                                                    </span>
                                                    <hr class="">
                                                    <div class="row text-center" id="checkboxes">
                                                        <div class="custom-control custom-checkbox col-4">
                                                            <input type="checkbox" class="custom-control-input check" name="typeProblemStatement" id="checkPS" checked>
                                                            <label class="custom-control-label" for="checkPS">Problem Statement</label>
                                                        </div>
                                                        <div class="custom-control custom-checkbox col-4">
                                                            <input type="checkbox" class="custom-control-input check" name="typeBasePattern" id="checkBP">
                                                            <label class="custom-control-label" for="checkBP">Pattern</label>
                                                        </div>
                                                        <div class="custom-control custom-checkbox col-4">
                                                            <input type="checkbox" class="custom-control-input check" name="typeSolutionDesign" id="checkSD">
                                                            <label class="custom-control-label" for="checkSD">Solution Design</label>
                                                        </div>
                                                        <#--  <div class="custom-control custom-checkbox col-3">
                                                            <input type="checkbox" class="custom-control-input" name="typeSearchAll" id="checkSearchAll" checked>
                                                            <label class="custom-control-label" for="checkSearchAll">Search All</label>
                                                        </div>  -->
                                                    </div>
                                                    </div>
                                                </div>
                                            </div>
                                            <#--  <button class="btn btn-primary pull-right applyBtn" type="button">Apply</button>  -->
                                        </div>
                                    </div>
                                    <div class="toastMsg"></div>
                                <br>
                                <div class="form-group mt-2" id="searchResults">
                                    <label>Search Result</label>
                                    <div id="accordion">
                                      <div class="card">
                                        <div class="card-header" style="cursor:pointer;" id="psHeading" data-toggle="collapse" data-target="#collapsePS" aria-expanded="true" aria-controls="collapsePS">
                                          <h5 class="mb-0">
                                            <button class="btn">
                                              Problem Statement
                                            </button>
                                          </h5>
                                        </div>
                                        <div id="collapsePS" class="collapse show" aria-labelledby="psHeading" data-parent="#accordion">
                                          <div class="card-body collapsiblePSResults">

                                          </div>
                                        </div>
                                      </div>
                                      <div class="card">
                                        <div class="card-header" style="cursor:pointer;" id="ptHeading" data-toggle="collapse" data-target="#collapsePT" aria-expanded="false" aria-controls="collapsePT">
                                          <h5 class="mb-0">
                                            <button class="btn">
                                              Pattern
                                            </button>
                                          </h5>
                                        </div>
                                        <div id="collapsePT" class="collapse" aria-labelledby="ptHeading" data-parent="#accordion">
                                          <div class="card-body collapsiblePTResults">

                                          </div>
                                        </div>
                                      </div>
                                      <div class="card">
                                        <div class="card-header" style="cursor:pointer;" id="sdHeading" data-toggle="collapse" data-target="#collapseSD" aria-expanded="false" aria-controls="collapseSD">
                                          <h5 class="mb-0">
                                            <button class="btn">
                                              Solution Design
                                            </button>
                                          </h5>
                                        </div>
                                        <div id="collapseSD" class="collapse" aria-labelledby="sdHeading" data-parent="#accordion">
                                          <div class="card-body collapsibleSDResults">

                                          </div>
                                        </div>
                                      </div>
                                    </div>
                                    <ul class="list-group searchResultsList">
                                        
                                    </ul>
                                </div>
                            </div>
                        </div>
                        <div class="tab-pane" id="tab2">
                            <div class="container my-2 py-2">
                                <form id="problemStmtForm">
                                    <div class="form-group">
                                        <label>Problem Statement</label>
                                        <div class="input-container">
                                            <input type="text" class="form-control" id="problemStatement" required>
                                        </div>
                                    </div>
                                    <div class="form-group">
                                        <label>Problem Description</label>
                                        <div class="input-container problemDescription">
                                            <input type="text" class="form-control" id="problemDescription" required>
                                        </div>
                                    </div>
                                    <div class="form-group">
                                        <label>Tag</label>
                                        <div class="input-container">
                                            <input type="text" class="form-control" id="tagInput" required>
                                        </div>
                                        <label for="tag">Note : Please use space for entering multiple tags. </label>
                                    </div>
                                    <div class="row">
                                        <div class="col-9">
                                            <div class="formToastMsg"></div>
                                        </div>
                                        <div class="col-3">
                                            <input type="button" value="Create" class="btn btn-primary pull-right submitBtn" id="problemStmtFormSubmitBtn" style="width: 125px;">
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
<script type="module" src="../static/graphEditor/js/customJs/problemStatements.js"></script>


