<?php
include "functions.php";
include "includes/header.php";
?>

<body>
<div class = "container">
	<div class = "col-xs-9">
		<?php // <h1 class="text-center">All Data</h1> ?>
		<?php
			$result = getAllData();
			while($row = mysqli_fetch_assoc($result)){
				echo $row['name'] . "<br>" . $row['address'] . "<br><br>";
			}
		?>
	</div>
<?php include "includes/footer.php";
?>