<?php
/**
 * @author Ravi Tamada
 * @link http://www.androidhive.info/2012/01/android-login-and-registration-with-php-mysql-and-sqlite/ Complete tutorial
 */

class DB_Connect {
    private $conn;

    // Connecting to database
    public function connect() {
        require_once 'include/Config.php';
        
        // Connecting to mysql database
        $this->conn = new mysqli("192.168.173.1", "ppl", "ppla07", "android_api");
        
        // return database handler
        return $this->conn;
    }
}

?>
