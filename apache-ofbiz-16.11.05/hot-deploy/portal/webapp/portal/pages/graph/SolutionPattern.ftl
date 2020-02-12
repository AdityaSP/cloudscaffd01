
<div class="container-fluid">
    <div class="row py-3">
    <div class="col-12 pb-2 h3 text-center">Solution Design</div>
        ​<div class="col-12 p-3">
            <div class="form-group">
                <label class="probStatement h5"></label>
                <p class="probStatementDescription"></p>
                <hr class="my-4">
            </div>
            <div class="form-group basePatternForm">
                <label class="basePattern h5"></label>
                <a class="badge badge-light p-2 mr-2 pull-right viewBpImage" 
                        data-target="#modalIMG" data-toggle="modal" href="#">View BP Image</a>
                <p class="basePatternDescription"></p>
                <hr class="my-4">
            </div>
            <div class="form-group">
                <label class="solutionDesign h5"></label>
                <p class="solutionDesignDescription"></p>
            </div>
        <div class="col-12 px-3">
                <div class="toastMsg m-0"></div>
                <div class="text-center p-4">
                <#--  <span class="d-inline-block deployCheck" tabindex="0" data-toggle="tooltip" title="">  -->
                  <button class="btn btn-primary m-1 p-1 deploy" style="width: 150px;" type="button" >Deploy</button>
                <#--  </span>  -->
                <button class="btn btn-primary m-1 p-1 edit" style="width: 150px;">Edit</button>
                <button class="btn btn-primary m-1 p-1 approve" style="width: 150px;">Approve</button>
                </div>
                <#--<img src="" srcset="" class="img-fluid img-thumbnail w-100 h-100" alt="..." id="solutionDesignImg">-->
                <div class='svgDiv img-fluid img-thumbnail w-100 p-2' style="height:auto;"></div>
            </div>
        </div>
    </div>
    <div aria-hidden="true" aria-labelledby="myModalLabel" class="modal fade" id="modalIMG" role="dialog" tabindex="-1">
	<div class="modal-dialog modal-lg" role="document">
		<div class="modal-content">
			<div class="modal-body mb-0 p-1">
				<#--  <img src="" alt="" style="width:100%">  -->
        <div class='BPsvgDiv img-fluid img-thumbnail p-2'></div>
			</div>
			<div class="modal-footer">
				<button class="btn btn-outline-danger btn-rounded btn-md ml-4 text-center" data-dismiss="modal" type="button">Close</button>
			</div>
		</div>
	</div>
</div>
<script type="module" src="../static/graphEditor/js/customJs/solutionDesign.js"></script>
