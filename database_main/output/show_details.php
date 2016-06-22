<?php
/* this file retrieves the response from Android (genre) and returns the relevant categories
Algorithm: use POST to receive the genre. Then pass the value into 'genre' to get the id.
Pass the 'gen_id' into 'contains' to get 'cat_id'. For all the 'cat_id', pass it into 'category'
to get the 'name'.
*/
include "functions_output.php";
?>

<?php
// retrieves all the data in the database
$result = getDetailsOfActivity(getAndroidCategory());

// output in json format
echo json_encode($result);
?>