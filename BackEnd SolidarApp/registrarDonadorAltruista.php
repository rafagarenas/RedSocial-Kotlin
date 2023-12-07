<?php
require_once 'db_connection.php';

$response = array();

$dbConnection = new DBConnection();

if ($_SERVER['REQUEST_METHOD'] === 'POST') {

    $userName = $_POST['userName'];
    $userEmail = $_POST['userEmail'];
    $userPassword = $_POST['userPassword'];
    $userConfirmPassword = $_POST['userConfirmPassword'];

    $minPasswordLength = 8;

    if (empty($userName) || empty($userEmail) || empty($userPassword) || empty($userConfirmPassword)) {
        $response['success'] = false;
        $response['message'] = "Todos los campos son obligatorios. Por favor, complete todos los campos.";
    } elseif (strlen($userPassword) < $minPasswordLength) {
        $response['success'] = false;
        $response['message'] = "La contraseña debe tener al menos $minPasswordLength caracteres.";
    } elseif ($userPassword != $userConfirmPassword) {
        $response['success'] = false;
        $response['message'] = "Las contraseñas no coinciden.";
    } elseif (!filter_var($userEmail, FILTER_VALIDATE_EMAIL)) {
        $response['success'] = false;
        $response['message'] = "El formato del correo electrónico no es válido.";
    } else {

        $userExistsQuery = "SELECT * FROM altruist_donators WHERE ad_username = ?";
        $stmtUserExists = $dbConnection->prepareStatement($userExistsQuery);
        $stmtUserExists->bind_param("s", $userName);
        $stmtUserExists->execute();
        $resultUserExists = $stmtUserExists->get_result();
        $stmtUserExists->close();

        if ($resultUserExists->num_rows === 0) {
            $userExistsQueryAlt = "SELECT * FROM social_centers WHERE sc_name = ?";
            $stmtUserExistsAlt = $dbConnection->prepareStatement($userExistsQueryAlt);
            $stmtUserExistsAlt->bind_param("s", $userName);
            $stmtUserExistsAlt->execute();
            $resultUserExistsAlt = $stmtUserExistsAlt->get_result();
            $stmtUserExistsAlt->close();
        }

        $emailExistsQuery = "SELECT * FROM altruist_donators WHERE ad_email = ?";
        $stmtEmailExists = $dbConnection->prepareStatement($emailExistsQuery);
        $stmtEmailExists->bind_param("s", $userEmail);
        $stmtEmailExists->execute();
        $resultEmailExists = $stmtEmailExists->get_result();
        $stmtEmailExists->close();

        if ($resultEmailExists->num_rows === 0) {
            $emailExistsQueryAlt = "SELECT * FROM social_centers WHERE sc_email = ?";
            $stmtEmailExistsAlt = $dbConnection->prepareStatement($emailExistsQueryAlt);
            $stmtEmailExistsAlt->bind_param("s", $userEmail);
            $stmtEmailExistsAlt->execute();
            $resultEmailExistsAlt = $stmtEmailExistsAlt->get_result();
            $stmtEmailExistsAlt->close();
        }

        if ($resultUserExists->num_rows > 0 || $resultUserExistsAlt->num_rows > 0) {
            $response['success'] = false;
            $response['message'] = "El nombre de usuario ya está registrado. Por favor, elija otro nombre de usuario.";
        } elseif ($resultEmailExists->num_rows > 0 || $resultEmailExistsAlt->num_rows > 0) {
            $response['success'] = false;
            $response['message'] = "El correo electrónico ya está registrado. Por favor, utilice otro correo electrónico.";
        } else {
            $hashedPassword = password_hash($userPassword, PASSWORD_BCRYPT);

            $insertQuery = "INSERT INTO altruist_donators (ad_username, ad_email, ad_password) VALUES (?, ?, ?)";
            $stmtInsert = $dbConnection->prepareStatement($insertQuery);
            $stmtInsert->bind_param("sss", $userName, $userEmail, $hashedPassword);

            if ($stmtInsert->execute()) {
                $response['success'] = true;
                $response['message'] = "Donador Altruista registrado exitosamente.";
            } else {
                $response['success'] = false;
                $response['message'] = "Error al Registrarse: " . $stmtInsert->error;
            }

            $stmtInsert->close();
        }
    }
} else {
    http_response_code(405); 
    $response['success'] = false;
    $response['message'] = "Error: Método no permitido.";
}

$dbConnection->closeConnection();

header('Content-Type: application/json');
echo json_encode($response);
?>