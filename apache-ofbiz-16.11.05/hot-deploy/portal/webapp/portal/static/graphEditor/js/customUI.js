
$("#menu-toggle").click(function (e) {
	e.preventDefault();
	$("#wrapper").toggleClass("toggled");
});

$(function () {

	/**
	 * <script src="https://code.jquery.com/jquery-3.3.1.slim.min.js"
  integrity="sha384-q8i/X+965DzO0rT7abK41JStQIAqVgRVzpbzo5smXKp4YfRvH+8abtTE1Pi6jizo" crossorigin="anonymous"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.14.7/umd/popper.min.js"
  integrity="sha384-UO2eT0CpHqdSJQ6hJty5KVphtPhzWj9WO1clHTMGa3JDZwrnQq4sF86dIHNDz0W1" crossorigin="anonymous"></script>
<script src="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/js/bootstrap.min.js"
  integrity="sha384-JjSmVgyd0p3pXB1rRibZUAYoIIy6OrQ6VrjIEaFf/nJGzIxFDsf4x0xIM+B07jRM" crossorigin="anonymous"></script>
<script src="js/customUI.js"></script>
	 */


	// var script = document.createElement("script");
	// script.type = "text/javascript";
	// script.src = "https://code.jquery.com/jquery-3.3.1.slim.min.js";
	// script.integrity = "sha384-q8i/X+965DzO0rT7abK41JStQIAqVgRVzpbzo5smXKp4YfRvH+8abtTE1Pi6jizo";
	// script.crossorigin = "anonymous";

	// document.getElementsByTagName("head")[0].appendChild(script);

	// var script1 = document.createElement("script");
	// script1.type = "text/javascript";
	// script1.src = "https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.14.7/umd/popper.min.js";
	// script.integrity = "sha384-UO2eT0CpHqdSJQ6hJty5KVphtPhzWj9WO1clHTMGa3JDZwrnQq4sF86dIHNDz0W1";
	// script.crossorigin = "anonymous";

	// document.getElementsByTagName("head")[0].appendChild(script1);

	// var script2 = document.createElement("script");
	// script2.type = "text/javascript";
	// script2.src = "https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/js/bootstrap.min.js";
	// script.integrity = "sha384-JjSmVgyd0p3pXB1rRibZUAYoIIy6OrQ6VrjIEaFf/nJGzIxFDsf4x0xIM+B07jRM";
	// script.crossorigin = "anonymous";

	// document.getElementsByTagName("head")[0].appendChild(script2);


	var a = `<label for="isLoggedIn">Logged on: </label>
  <label for="userName" class="text-primary" style="float: right;"> Username </label>`;

	function loadSidebar() {
		const navbar = `<div class="row-12 navigation">
		<nav class="navbar navbar-expand-lg navbar-light bg-light py-0">
			<a class="navbar-brand py-0" href="#"><span><img src="../static/graphEditor/images/logo1.png" class="pb-2" height="25%" width="25%" alt="logo"></span></a>
			<button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarNavDropdown"
				aria-controls="navbarNavDropdown" aria-expanded="false" aria-label="Toggle navigation">
				<span class="navbar-toggler-icon"></span>
			</button>
			
		</nav>
	</div>`;
		const sideBarHTML = `	
		<div class="col-md-2">
			<div id="wrapper" class="toggled">
				<div id="sidebar-wrapper" class="pt-0" style="width: auto;">
					<ul class="sidebar-nav">
						<li>
							<h3>Services</h3>
						</li>
						<li>
							<a href="#">AutoPatt Engine</a>
						</li>
						<li class="nav-item dropdown">
							<a class="nav-link dropdown-toggle pl-0" href="#" id="navbarDropdownMenuLink" data-toggle="dropdown"
								aria-haspopup="true" aria-expanded="false">
								APC
							</a>
							<div class="dropdown-menu" aria-labelledby="navbarDropdownMenuLink" style="width: 15%;">
								<a class="dropdown-item pl-0" href="patternCategories.html">Pattern Categories</a>
								<a class="dropdown-item pl-0" href="patternTypes.html">Pattern Types</a>
								<a class="dropdown-item pl-0" href="createCustom.html">Create Custom</a>
								<a class="dropdown-item pl-0" href="templateIntegration.html">Template Integration</a>
							</div>
						</li>
						<li>
							<a href="#">Administration</a>
						</li>
						<li>
							<a href="#">License</a>
						</li>
						<li>
							<a href="#">Orchestrators</a>
						</li>
					</ul>
				</div>
			</div>
  </div>`;
		$(".customNavbar").append(navbar);
		$(".customSideBar").append(sideBarHTML);
	}
	loadSidebar();

	$(".network").on("click", function () {
		
		window.open("./graphEditor.html?type=network",'_self')

		// var doc = mxUtils.parseXml(data);
		// var model = new mxGraphModel();
		// var codec = new mxCodec(doc);
		// codec.decode(doc.documentElement, model);

		// var script = document.createElement("script");
		// script.type = "text/javascript";
		// script.src = "js/customJs/fetchServer.js";
		// document.getElementsByTagName("head")[0].append(script);
		//return false;//D:\Cloud Scaffolding\Project\Graphs\graphEditor\js\customJs\fetchServer.js

		// window.editorUi.getDataFromServer("http://127.0.0.1:5501/sampleXml.xml", "xml");
	})

});


