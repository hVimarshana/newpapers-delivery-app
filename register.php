<?php
if(isset($_POST['name']) && isset($_POST['email']) && isset($_POST['password'])){

    require_once "conn.php";
    require_once "validate.php";
    
    $name = validate($_POST['name']);
    $email = validate($_POST['email']);
    $type = validate($_POST['type']);
    $password = validate($_POST['password']);
   
    $sql = "insert into users values('','$name','$email','$type','" . md5($password) . "')";
 
    if(!$conn->query($sql)){
        echo "failure";
    }else{
        echo "success";   
    }
}
?>