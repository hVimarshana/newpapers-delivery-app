<?php

if(isset($_POST['new_value'])){
    require_once "conn.php";

//$id = $_POST['id'];
$newValue = $_POST['new_value'];


$sql = "UPDATE orderTBL SET Status = '$newValue' WHERE ID = 7";

if ($conn->query($sql) === TRUE) {
    echo "Record updated successfully";
} else {
    echo "Error updating record: " . $conn->error;
}

$conn->close();
}
?>