<?php

require_once 'include/DB_Functions.php';
$db = new DB_Functions();

// json response array
$response = array("error" => FALSE);

if (isset($_POST['email']) && isset($_POST['name']) && isset($_POST['id'])) {

    // receiving the post params
    $email = $_POST['email'];
    $name = $_POST['name'];
    $id = $_POST['id'];

    $db->udpateUser($email, $name, $id);
    $response["error"] = FALSE;
    $response["user"]["email"] = $email;
    $response["user"]["name"] = $name;
    echo json_encode($response);
}
?>

