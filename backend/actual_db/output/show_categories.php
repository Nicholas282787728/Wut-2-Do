<?php
// this file retrieves the response from Android (genre) and returns the relevant categories

include "functions_output.php";
?>

<?php
// retrieves all the data in the database
$result = getCategory(getAndroidGenre());
// output in json format
echo json_encode($result);
?>