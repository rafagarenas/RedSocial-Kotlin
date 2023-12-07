<?php
class DBConnection {
    private $servername = "localhost";
    private $username = "root";
    private $password = "";
    private $dbname = "solidarapp";
    private $conn;

    public function __construct() {
        $this->conn = new mysqli($this->servername, $this->username, $this->password, $this->dbname);

        if ($this->conn->connect_error) {
            die("Conexión fallida: " . $this->conn->connect_error);
        }
    }

    public function closeConnection() {
        if ($this->conn) {
            $this->conn->close();
        }
    }

    public function getConnection() {
        return $this->conn;
    }

    public function prepareStatement($sql) {
        return $this->conn->prepare($sql);
    }
}
?>
