<?php
include "functions.php";
include "includes/header.php";
?>

<body>
<div class = "container">
	<div class = "col-xs-9">
		<?php
			// array for JSON response
			$response = array();
			$file = 'cinemas_json.txt';

			$result = getAllData();
			while($row = mysqli_fetch_assoc($result)){
				$location = array();
				$location['name'] = $row['name'];
				$location['address'] = $row['address'];
				
				array_push($response, $location);
			}
			$json = json_encode($response);

			file_put_contents($file, $json);
		?>
	</div>
<?php include "includes/footer.php";
?>