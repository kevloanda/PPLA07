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
    $sql = "Insert into user values('" . $_GET['username'] . "','" . $_GET['email'] . "','" . $_GET['password'] . "','" . $_GET['nomorhp'] . "','" . $_GET['foto'] . "','" . $_GET['twitter'] . "')";
    if($conn->query($sql) === TRUE) {
        echo "User registration was successful!";
    } else {
        echo "Error in insertion with query: " . $sql;
    }

    $conn->close();
?>
