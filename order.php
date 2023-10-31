<?php
if(isset($_POST['longitude']) && isset($_POST['latitude'])){

    require_once "conn.php";

    $newspaperName = $_POST['newspaperName'];
    $SubscriptionPlane = $_POST['SubscriptionPlane'];
    $Copies = $_POST['Copies'];
    $longitude = $_POST['longitude'];
    $latitude = $_POST['latitude'];
    $Status = $_POST['Status'];
   
   
    $sql = "insert into orderTBL values('','$newspaperName','$SubscriptionPlane','$Copies','$longitude','$latitude','$Status')";
 
    if(!$conn->query($sql)){
        echo "failure";
    }else{
        echo "success";   
    }
}
?>