<div class="row">
    <div class="col-md-12">
        <div class="container my-2 py-2">
            <div class="form-group">
                    <p class="h4" id="probStatement"></p>
                    <p id="probStatementDescription"></p>
            </div>
            <div class="form-group problemTags" id='Ptags'></div>
            <hr class="my-4">
            <br>
            <div class="row allSolutions">
                <div class="col-6">
                    <div class="tabbable" id="tabs">
                        <ul class="nav nav-tabs">
                            <li class="nav-item">
                                <a class="nav-link active show" href="#tab1" data-toggle="tab">Base Patterns</a>
                            </li>
                            <li class="nav-item">
                                <a class="nav-link" id="createBasePattern" href="#tab2"
                                   data-toggle="tab">Create Base Pattern</a>
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
                                    <form method="POST"  action="<@ofbizUrl>AddBasePattern</@ofbizUrl>">
                                        <div class="form-group">
                                            <label for="baseProblem">Base Pattern Name</label>
                                            <input type="text" name="baseName" class="form-control" required id="baseProblem">
                                        </div>
                                        <input type="hidden" class="psid" name="psid" value="">
                                        <div class="form-group">
                                            <label for="baseProblemDescription">Base Pattern Description</label>
                                            <input class="form-control" name="baseDescription" required id="baseProblemDescription"
                                                   rows="3"/>
                                        </div>
                                        <button class="btn btn-primary pull-right">Create</button>
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
                                <a class="nav-link active show" href="#solutionTab1" data-toggle="tab">Solution Patterns</a>
                            </li>
                            <li class="nav-item">
                                <a class="nav-link" id="createBasicPattern" href="#solutionTab2"
                                   data-toggle="tab">Create Solution Pattern</a>
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
                                    <form method="POST"action="<@ofbizUrl>AddSolutionDesign</@ofbizUrl>">

                                        <div class="form-group">
                                            <label>Solution Design Name</label>
                                            <input type="text" class="form-control" name="solutionDesignName" required
                                                   id="solutionDesignName">
                                        </div>
                                        <input type="hidden" class="psid" name="psid" value="">
                                        <input type="hidden" class="bpid" name="bpid" value="">
                                        <div class="form-group">
                                            <label>Discription</label>
                                            <input type="text" class="form-control" name="solutionDesignDesc" required
                                                   id="solutionDesignDescription" rows="3">
                                        </div>
                                        <button class="btn btn-primary pull-right">Create</button>
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
<script type="module" src="../static/graphEditor/js/customJs/getPatterns.js"></script>
