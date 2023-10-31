<?php
    require_once "conn.php";
$tableName = "users";

header('Content-Type: application/json');
header('Content-Disposition: attachment; filename="database.json"');

$sql = "SELECT * FROM " . $tableName;
$result = $conn->query($sql);

$tableData = array();

if ($result->num_rows > 0) {
    while ($row = $result->fetch_assoc()) {
        $tableData[] = $row;
    }
}

$conn->close();

$jsonData = json_encode($tableData);

echo $jsonData;
?>
