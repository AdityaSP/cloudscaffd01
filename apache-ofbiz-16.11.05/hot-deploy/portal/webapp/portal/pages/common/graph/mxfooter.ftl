  </div>
  <!-- /#wrapper -->

  <!-- Bootstrap core JavaScript -->
  <script src="../static/vendor/jquery/jquery.min.js"></script>
  <script src="../static/vendor/bootstrap/js/bootstrap.bundle.min.js"></script>

  <script src="../static/js/portal_modals.js"></script>
  <script src="../static/js/portal_users.js"></script>
  <script src="../static/js/toast_utils.js"></script>

   <#--  Custom JS  -->

  <!-- Menu Toggle Script -->
  <script>
   /* $("#menu-toggle").click(function(e) {
      e.preventDefault();
      $("#wrapper").toggleClass("toggled");
    });*/
  $(document).ready(function(){
    $("#menu-toggle").click(function(){
      $(".text").toggleClass("collapse");
      $("#wrapper").toggleClass("collapse");
    });
  });

  $(function() {
    setTimeout(function() {
      $("#page_loading").hide();
      $("#wrapper").show(300,"swing");
    }, 200);

    $("#password_eye").click(function() {
      togglePasswordField("password");
    });
    $("#newPassword_eye").click(function() {
      togglePasswordField("newPassword");
    });
    $("#newPasswordVerify_eye").click(function() {
      togglePasswordField("newPasswordVerify");
    });
  });

  function getAppUrl (uri) {
    var appContext = "<@ofbizUrl>/</@ofbizUrl>";
    return appContext + uri;
  }

   function togglePasswordField(pwdFieldId){
     $("#"+pwdFieldId+"_eye").toggleClass('active');
     var pwdField = $("#"+pwdFieldId);
     if(pwdField.attr("type") === 'password') pwdField.attr("type","text");
     else pwdField.attr("type","password");
   }

  </script>

</body>

</html>
