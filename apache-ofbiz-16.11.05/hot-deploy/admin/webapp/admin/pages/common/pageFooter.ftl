</div>
<!-- /#page-content-wrapper -->

<!-- TOASTS -->
<div aria-live="polite" aria-atomic="true">
    <!-- Position toast to top-right  -->
    <div style="position: absolute; top: 6rem; right: 1rem; z-index:999;">
        <div class="toast" id="success_toaster" style="width:600px" role="alert" aria-live="assertive" aria-atomic="true" data-delay="5000">
            <#--<div class="toast-header">
                <strong class="mr-auto">Toast Title</strong>
                <button type="button" class="ml-2 mb-1 close" data-dismiss="toast" aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>
            </div>-->
            <div class="toast-body" style="padding:0.1rem;">
                <div class="alert alert-success alert-dismissible fade show" role="alert" style="margin:0 !important;">
                    <i class="fa fa-check" aria-hidden="true"></i>
                    <span id="success_toaster_message">
                        <strong>Success!</strong> Transaction completed successfully.
                    </span>
                    <button type="button" class="close" data-dismiss="toast" aria-label="Close">
                        <span aria-hidden="true">&times;</span>
                    </button>
                </div>
            </div>
        </div>
    </div>


    <div style="position: absolute; top: 6rem; right: 1rem; z-index:999;">
        <div class="toast" id="error_toaster" style="width:600px; " role="alert" aria-live="assertive" aria-atomic="true" data-delay="5000">
            <#--<div class="toast-header">
                <strong class="mr-auto">Toast Title</strong>
                <button type="button" class="ml-2 mb-1 close" data-dismiss="toast" aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>
            </div>-->
            <div class="toast-body" style="padding:0.1rem;">
                <div class="alert alert-danger alert-dismissible fade show" role="alert" style="margin:0 !important;">
                    <i class="fa fa-exclamation-triangle" aria-hidden="true"></i>
                    <span id="error_toaster_message">
                        <strong>Error!</strong> Transaction completed successfully.
                    </span>
                    <button type="button" class="close" data-dismiss="toast" aria-label="Close">
                        <span aria-hidden="true">&times;</span>
                    </button>
                </div>
            </div>
        </div>
    </div>
</div>