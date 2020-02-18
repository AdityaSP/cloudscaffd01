
<div class="container-fluid h-100">
    <div class="row py-3">
    <div class="col-12 pb-2 h3 text-center title">Base Pattern</div>
        ​<div class="col-12 p-3">
            <div class="form-group probStatementForm">
                <label class="probStatement h5"></label>
                <p class="probStatementDescription"></p>
            </div>
            <hr class="my-4">
            <div class="form-group basePatternForm">
                <label class="basePattern h5"></label><span class="typeDataBP"></span>
                
                <a class="btn btn-danger pull-right deleteBP" data-toggle="tooltip" data-placement="top" title="Delete Base Pattern" href="javascript:void(0);" aria-label="Delete">
                    <i class="fa fa-trash-o fa-lg" aria-hidden="true"></i></a>
                <#--  <a class="btn btn-info pull-right mr-2 editBP" data-toggle="tooltip" data-placement="left" title="Edit Base Pattern" href="javascript:void(0);" aria-label="Edit">
                    <i class="fa fa-pencil fa-lg" aria-hidden="true"></i></a>  -->
                <p class="basePatternDescription"></p>
                <div class="basePatternConsequences p-4 row">
                    <div class="col-6 border rounded">
                        <label for="basePatternForces h5">Forces</label><hr class="m-0">
                        <p class="basePatternForces"></p>
                    </div>
                    <div class="col-6 border rounded">
                        <label for="basePatternBenefits">Benefits</label><hr class="m-0">
                        <p class="basePatternBenefits"></p>
                    </div>
                </div>
            </div>
        </div>
        <div class="col-12 px-3">
            <div class="toastMsg m-0"></div>
            <div class="text-center p-2">
                <button class="btn btn-primary m-1 p-1 approve" style="width: 150px;height:auto;">Approve</button>
                <button class="btn btn-primary m-1 p-1 edit" style="width: 150px;">Edit</button>
            </div>
            <#--  <img src="" class="img-fluid img-thumbnail w-100 h-100" alt="" id="basePatternImg">  -->
            <div class='svgDiv img-fluid img-thumbnail w-100 p-2' style="height:auto;"></div>
        </div>
    </div>
</div>
<script type="module" src="../static/graphEditor/js/customJs/basePattern.js"></script>
