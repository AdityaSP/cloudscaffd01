<div class="container-fluid">
    <div class="table-title">
        <div class="row">
            <div class="col-sm-5">
                <h4>Admin Users</h4>
            </div>
            <div class="col-sm-7">
                <a href="<@ofbizUrl>new_user</@ofbizUrl>" class="btn btn-primary"><i class="material-icons">&#xE147;</i> <span>New Admin</span></a>
                <#--<a href="#" class="btn btn-primary"><i class="material-icons">&#xE24D;</i> <span>Export to Excel</span></a>-->
            </div>
        </div>
    </div>

    <div class="table-content">
        <table class="table table-striped table-hover">
            <thead>
            <tr>
                <th>#</th>
                <th>Name</th>
                <th>Date Created</th>
                <th>Role</th>
                <th>Status</th>
                <th>Action</th>
            </tr>
            </thead>
            <tbody>
            <tr>
                <td>1</td>
                <td><a href="#"><i class="material-icons" style="font-size:1.6em;">account_circle</i> Aditya</a></td>
                <td>04/10/2013</td>
                <td>Admin</td>
                <td><span class="status text-success" >&bull;</span> <span>Active</span></td>
                <td>
                    <a href="#" class="settings" title="Edit" data-toggle="tooltip"><i class="material-icons">edit</i></a>
                    <a href="#" class="delete" title="Delete" data-toggle="tooltip"><i class="material-icons">delete</i></a>
                </td>
            </tr>

            </tbody>
        </table>
    </div>


</div>