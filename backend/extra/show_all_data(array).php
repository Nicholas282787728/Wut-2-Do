<?php /*
	include "functions.php";

	$file = "cinemas.txt";
	if($handle = fopen($file, 'r')){
		while(!feof($handle)){
			$id = fgets($handle);
			$name = fgets($handle);
			$address = fgets($handle);
			fgets($handle);
			createRows($id, $name, $address);
		}
		
		fclose($handle);
	}
	else{
		echo "File not found";
	}*/
?>

<?php
include "functions.php";
include "includes/header.php";
?>

<body>
<div class = "container">
	<div class = "col-xs-9">
		<h1 class="text-center">All Data</h1>
		<?php
			$result = getAllData();
			while($row = mysqli_fetch_assoc($result)){
		?>
				<pre>

				<?php
					print_r($row);
				?>
				
				</pre>
				<?php
			}
				?>
	</div>
<?php include "includes/footer.php";
?>