<?php
/* this file retrieves the response from Android (category, latlong) and returns the relevant results
also, if user is logged in, update the values of user's search results
*/
include "functions_output.php";
?>

<?php
// retrieves all the data in the database
$category = getAndroidCategory();
if($category == "all")
	echo json_encode(getAllDetails(getAndroidLatLong()));
else
	echo json_encode(getDetailsOfActivity($category, getAndroidLatLong()));
?>