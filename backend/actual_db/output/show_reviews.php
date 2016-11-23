<?php
// this file retrieves the response from Android (postal_code, unit_no) and returns the relevant reviews

include "functions_output.php";
?>

<?php
// retrieves all the data in the database
$result = getReviews(getAndroidPostalCode(), getAndroidUnitNo(), getAndroidShopName());
// output in json format
echo json_encode($result);
?>