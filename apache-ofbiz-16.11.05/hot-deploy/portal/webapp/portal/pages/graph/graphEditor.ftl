
<body class="geEditor">
  <div class="text-center" id="page_loading" style="padding:50px;">
    <div class="spinner-border" role="status">
    </div>
  </div>
  <div class="d-flex" id="wrapper" style="display:none !important;">
	<canvas id="canvas" width="800" height="400"></canvas>
	<script type="text/javascript">
		// Extends EditorUi to update I/O action states based on availability of backend
		(function () {
			var editorUiInit = EditorUi.prototype.init;
			EditorUi.prototype.init = function () {
				editorUiInit.apply(this, arguments);
				this.actions.get('export').setEnabled(true);

				// Updates action states which require a backend
				if (Editor.useLocalStorage) {
					mxUtils.post(OPEN_URL, '', mxUtils.bind(this, function (req) {
						var enabled = req.getStatus() != 404;
						// this.actions.get('open').setEnabled(enabled || Graph.fileSupport);
						this.actions.get('import').setEnabled(enabled || Graph.fileSupport);
						this.actions.get('save').setEnabled(enabled);
						this.actions.get('export').setEnabled(enabled);
					}));
				}
			};

			// Adds required resources (disables loading of fallback properties, this can only
			// be used if we know that all keys are defined in the language specific file)
			mxResources.loadDefaultBundle = false;
			var bundle = mxResources.getDefaultBundle(RESOURCE_BASE, mxLanguage) ||
				mxResources.getSpecialBundle(RESOURCE_BASE, mxLanguage);

			// Fixes possible asynchronous requests
			mxUtils.getAll([bundle, STYLE_PATH + '/default.xml'], function (xhr) {
				// Adds bundle text to resources
				mxResources.parse(xhr[0].getText());

				// Configures the default graph theme
				var themes = new Object();
				themes[Graph.prototype.defaultThemeName] = xhr[1].getDocumentElement();

				// Main
				new EditorUi(new Editor(urlParams['chrome'] == '0', themes));
			}, function () {
				document.body.innerHTML =
					'<center style="margin-top:10%;">Error loading resource files. Please check browser console.</center>';
			});
		})();
	</script>
</body>

</html>