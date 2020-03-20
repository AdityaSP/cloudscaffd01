<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="utf-8">
  <meta name="viewport" content="width=1024, initial-scale=1, shrink-to-fit=no">
  <meta name="description" content="">
  <meta name="author" content="">

  <#if title??>
    <title>${title!} - AutoPatt Console</title>
  <#else>
      <title>AutoPatt Console</title>
  </#if>

  <!-- Bootstrap core CSS -->
  <link href="../static/vendor/bootstrap/css/bootstrap.min.css" rel="stylesheet">

  <!-- Custom styles for this template -->
  <link href="../static/css/portal-main.css" rel="stylesheet">

  <link href="../static/css/md_icon.css" rel="stylesheet">

  <link rel="stylesheet" href="../static/css/font-awesome.min.css">

	<link rel="stylesheet" type="text/css" href="../static/graphEditor/styles/grapheditor.css">
	<#--  <link rel="stylesheet" href="styles/customUI.css">  -->
	<script type="text/javascript">
		// Parses URL parameters. Supported parameters are:
		// - lang=xy: Specifies the language of the user interface.
		// - touch=1: Enables a touch-style user interface.
		// - storage=local: Enables HTML5 local storage.
		// - chrome=0: Chromeless mode.
		var urlParams = (function (url) {
			var result = new Object();
			var idx = url.lastIndexOf('?');

			if (idx > 0) {
				var params = url.substring(idx + 1).split('&');

				for (var i = 0; i < params.length; i++) {
					idx = params[i].indexOf('=');

					if (idx > 0) {
						result[params[i].substring(0, idx)] = params[i].substring(idx + 1);
					}
				}
			}

			return result;
		})(window.location.href);

		// Default resources are included in grapheditor resources
		mxLoadResources = false;
	</script>

	<script src="https://code.jquery.com/jquery-3.3.1.slim.min.js"
		integrity="sha384-q8i/X+965DzO0rT7abK41JStQIAqVgRVzpbzo5smXKp4YfRvH+8abtTE1Pi6jizo" crossorigin="anonymous">
	</script>
	<script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.14.7/umd/popper.min.js"
		integrity="sha384-UO2eT0CpHqdSJQ6hJty5KVphtPhzWj9WO1clHTMGa3JDZwrnQq4sF86dIHNDz0W1" crossorigin="anonymous">
	</script>
	<script src="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/js/bootstrap.min.js"
		integrity="sha384-JjSmVgyd0p3pXB1rRibZUAYoIIy6OrQ6VrjIEaFf/nJGzIxFDsf4x0xIM+B07jRM" crossorigin="anonymous">
	</script>
	<script src="../static/vendor/jquery/jquery.min.js"></script>

	<script type="module" src="../static/graphEditor/js/customJs/app.js"></script>
	
	<script type="text/javascript" src="../static/graphEditor/js/Init.js"></script>
	<script type="text/javascript" src="../static/graphEditor/deflate/pako.min.js"></script>
	<script type="text/javascript" src="../static/graphEditor/deflate/base64.js"></script>
	<script type="text/javascript" src="../static/graphEditor/jscolor/jscolor.js"></script>
	<script type="text/javascript" src="../static/graphEditor/sanitizer/sanitizer.min.js"></script>
	<script type="text/javascript" src="../static/graphEditor/src/js/mxClient.js"></script>
	<script type="text/javascript" src="../static/graphEditor/js/EditorUi.js"></script>
	<script type="text/javascript" src="../static/graphEditor/js/Editor.js"></script>
	<script type="text/javascript" src="../static/graphEditor/js/Sidebar.js"></script>
	<script type="text/javascript" src="../static/graphEditor/js/Graph.js"></script>
	<script type="text/javascript" src="../static/graphEditor/js/Format.js"></script>
	<script type="text/javascript" src="../static/graphEditor/js/Shapes.js"></script>
	<script type="text/javascript" src="../static/graphEditor/js/Actions.js"></script>
	<script type="text/javascript" src="../static/graphEditor/js/Menus.js"></script>
	<script type="text/javascript" src="../static/graphEditor/js/Toolbar.js"></script>
	<script type="text/javascript" src="../static/graphEditor/js/Dialogs.js"></script>
	
</head>
