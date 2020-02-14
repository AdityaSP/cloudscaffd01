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
</style>
<div class="container-fluid">
        <div class="row">
            <div class="col-md-12 p-4">
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
                                    <div class="form-group has-search">
                                        <span class="fa fa-search form-control-feedback"></span>
                                        <input type="text" name="inputSearch" class="form-control inputSearch" required placeholder="Search">
                                    </div>
                                    <#--  <span class="btn btn-link pull-right"  data-toggle="tooltip" data-placement="right" title="Filter">
                                        <i class="fa fa-filter" aria-hidden="true" type="button" data-toggle="collapse" data-target="#collapseExample" aria-expanded="false" aria-controls="collapseExample"></i>
                                    </span>  -->
                                    <div class="collapse" id="collapseExample">
                                        <div class="card card-body">
                                            <div class="row"> 
                                                <div class="col-12">
                                                    <article class="card-group-item">
		                                                <header class="card-header">
			                                                <h5 class="title">Select Pattern Type</h5>
		                                                </header>
		                                                <div class="filter-content">
			                                                <div class="card-body">
				                                            <div class="custom-control custom-checkbox">
					                                        <#--  <span class="float-right badge badge-light round">52</span>  -->
				  	                                        <input type="checkbox" class="custom-control-input" id="checkProblemStatement">
				  	                                            <label class="custom-control-label" for="Check1">Problem Statements</label>

                                                            <input type="checkbox" class="custom-control-input" id="checkBasePattern">
				 	                                        <label class="custom-control-label" for="Check2">Base Patterns</label>

                                                            <input type="checkbox" class="custom-control-input" id="checkSolutionDesign">
				  	                                        <label class="custom-control-label" for="Check3">Solution Designs</label>
				                                        </div> 
				                                        <#--  <div class="custom-control custom-checkbox">
				                                        	<span class="float-right badge badge-light round">132</span>
				                                          	<input type="checkbox" class="custom-control-input" id="checkBasePattern">
				                                         	<label class="custom-control-label" for="Check2">Base Patterns</label>
				                                        </div> -->
	                                                </article> 
                                                </div>
                                                <#--  <button class="btn btn-primary pull-right" type="button">Search</button>  -->
                                            </div>
                                        </div>
                                    </div>
                                <br>
                                <div class="form-group" id="searchResults">
                                    <label>Search Result...</label>
                                    <ul class="list-group searchResultsList">
                                        
                                    </ul>
                                </div>
                            </div>
                        </div>
                        <div class="tab-pane" id="tab2">
                            <div class="container my-2 py-2">
                                <form id="problemStmtForm">
                                 <#--  action="<@ofbizUrl>AddProblemStatement</@ofbizUrl>" method="POST"  -->
                                    <div class="form-group">
                                        <label>Problem Statement</label>
                                        <div class="input-container">
                                            <input type="text" class="form-control" id="problemStatement" required>
                                        </div>
                                    </div>
                                    <div class="form-group">
                                        <label>Problem Description</label>
                                        <div class="input-container">
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
                                    <div>
                                        <div class="toastMsg"></div>
                                        <input type="button" value="Create" class="btn btn-primary pull-right submitBtn" id="problemStmtFormSubmitBtn" style="width: 125px;">
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