<?php
    
    require_once "conn.php";

    $tableName = "orderTBL";
    $sql = "SELECT * FROM $tableName";
    $result = $conn->query($sql);

    $response = array();

    if ($result->num_rows > 0) {
        while ($row = $result->fetch_assoc()) {
            $response[] = $row;
        }
    }

    $conn->close();

    echo json_encode($response);
?>