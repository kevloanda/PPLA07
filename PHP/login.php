<?php
    $servername = "localhost";
    $username = "christian";
    $password = "uwaaaaa";
    $db_name = "ppl";

    // Create connection
    $conn = new mysqli($servername, $username, $password, $db_name);

    // Check connection
    if ($conn->connect_error) {
        die("Connection failed: " . $conn->connect_error);
    } 
    echo "Connected successfully</br>";
    $sql = "SELECT * FROM user where username='". $_GET['username'] ."' and password='". $_GET['password'] ."'"
    if($conn->query($sql) === TRUE) {
        echo "User login was successful!";
    } else {
        echo "Error in insertion with query: " . $sql;
    }

    $conn->close();
?>
