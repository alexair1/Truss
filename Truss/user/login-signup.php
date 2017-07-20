<!DOCTYPE html>
<html>
<!--
	Add check to see if account associated with email already exits
-->
	<?php
		if($_GET["f"] == 0){
			$title = "Sign Up";
		} else {
			$title = "Login";
		}
	?>
	<head>
		<link rel="Shortcut Icon" href="../img/favicon.ico" type="image/x-icon">
		<link href='http://fonts.googleapis.com/css?family=Open+Sans' rel='stylesheet' type='text/css'>
		<script src="//ajax.googleapis.com/ajax/libs/jquery/1.10.2/jquery.min.js"></script>
		<title><?php echo $title; ?></title>
	</head>
	<body>
		<div id="container">
			<h1><?php echo $title; ?></h1>
			<form>
				<?php 
					if($_GET["f"] == 0){
						echo '
							<input id="signup-user" type="text" name="user" placeholder="Username" /><br>
							<input id="email" type="email" name="email" placeholder="Email" /><br>
							<input id="signup-pass" type="password" name="pass" placeholder="Password" /><br>
							<input id="pass-conf" type="password" name="pass-confirm" placeholder="Confirm Password" /><br><br>
							<input id="signup-submit" type="button" value="Sign Up" />
						';
					} else {
						echo '
							<input id="login-user" type="text" name="user" placeholder="Username" /><br>
							<input id="login-pass" type="password" name="pass" placeholder="Password" /><br><br>
							<input id="login-submit" type="button" value="Login" />
						';
					}
				?>
			</form>
			<p id="error_disp"></p><br><br>
			<?php
				if($_GET["f"] != 0){
					echo "<p><a href='login-signup.php?f=0&d=".$_GET["d"]."'>Need an account?</a> // Truss 2014</p>";
				} else {
					echo "<p><a href='login-signup.php?f=1&d=".$_GET["d"]."'>Already have an account?</a> // Truss 2014</p>";
				}
			?>
		</div>
		<p id="d" style="display:none"><?php echo $_GET["d"] ?></p>
	<style>
		body {
			background: url('../img/spiral_grad.png');  
		}

		h1 {
			font-family: 'Open Sans';
			font-weight: 400;
		}

		input {
			outline: none;
			font-size: 14px;
			padding: 10px;
			border: solid 1px #ccc;
			width: 478px;
			margin-bottom: 3px;
		}

		p {
			font-family: 'Open Sans';
			font-size: 13px;
			right: 5px;
		}

		a {
			text-decoration: none;
		}

		#error_disp {
			position: absolute;
			right: 5px;
			font-family: 'Open Sans';
			font-size: 16px;
			color: #f00;
			font-weight: 700;
		}

		input[type=button] {
			width: 500px;
			border: none;
		}

		input[type=button]:hover {
			background-color: #aaa;
			cursor: pointer;
		}

		#container {
		/*	border: solid 1px;  */
			height: 400px;
			width: 500px;
			position: absolute;
		}
	</style>
	<script src="../js/ajax_calls.js"></script>
	<script>
		document.getElementById('container').style.left = (window.innerWidth-500)/2 + 'px';
		document.getElementById('container').style.top = (window.innerHeight-400)/2 + 'px';

		window.onresize = function(){
			document.getElementById('container').style.left = (window.innerWidth-500)/2 + 'px'; 
			document.getElementById('container').style.top = (window.innerHeight-400)/2 + 'px';
		}
	</script>
	</body>
<html>