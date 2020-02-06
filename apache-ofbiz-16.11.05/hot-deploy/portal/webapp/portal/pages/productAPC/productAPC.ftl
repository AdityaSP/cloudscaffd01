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
                                    <p><label>Tags-Cloud</label></p>

                                </div>
                                <br>
                                <#--  <form id="inputSearchForm" action="<@ofbizUrl>AddProblemStatement</@ofbizUrl>" method="POST">  -->
                                    <div class="form-group has-search">
                                        <span class="fa fa-search form-control-feedback"></span>
                                        <input type="text" name="inputSearch" class="form-control inputSearch" required placeholder="Search">
                                        <#--  <input type="submit" class="form-control submit" >  -->
                                    </div>
                                <#--  </form>  -->
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
                                <form id="problemStmt2Form" action="<@ofbizUrl>AddProblemStatement</@ofbizUrl>" method="POST">
                                    <div class="form-group">
                                        <label>Problem Statement</label>
                                        <div class="input-container">
                                            <input type="text" class="form-control" id="problemStatement" name="problemStatement" required>
                                        </div>
                                    </div>
                                    <div class="form-group">
                                        <label>Problem Description</label>
                                        <div class="input-container">
                                            <input type="text" class="form-control" id="problemDescription" name="problemDescription" required>
                                        </div>
                                    </div>
                                    <div class="form-group">
                                        <label>Tag</label>
                                        <div class="input-container">
                                            <input type="text" class="form-control" id="tag" name="tag" required>
                                        </div>
                                    </div>

                                    <div>
                                        <input type="submit" value="Create" class="btn btn-primary pull-right">
                                    </div>
                                </form>
                            </div>
                        </div></div>

                </div>
                </div>
            </div>
        </div>
    </div>

    <script type="module" src="../static/graphEditor/js/customJs/problemStatements.js"></script>