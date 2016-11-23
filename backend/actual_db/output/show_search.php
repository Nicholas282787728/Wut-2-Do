<?php
// this file retrieves the response from Android (search, latlong) and returns the relevant results

include "functions_output.php";
?>

<?php
// retrieves all the data in the database
$result = getSearch(getAndroidSearch(), getAndroidLatLong());
// output in json format
echo json_encode($result);
?>