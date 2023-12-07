<?php
require_once 'db_connection.php';

$response = array();

$dbConnection = new DBConnection();

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $userRecoveryEmail = $_POST['userRecoveryEmail'];
    $newPassword = $_POST['newPassword'];
    $newConfirmedPassword = $_POST['newConfirmedPassword'];

    $minPasswordLength = 8;

    if (empty($userRecoveryEmail) || empty($newPassword) || empty($newConfirmedPassword)) {
        $response['success'] = false;
        $response['message'] = "Todos los campos son obligatorios. Por favor, complete todos los campos.";
    } elseif(strlen($newPassword) < $minPasswordLength){
        $response['success'] = false;
        $response['message'] = "La contraseña debe tener al menos $minPasswordLength caracteres.";
    }elseif ($newPassword != $newConfirmedPassword) {
        $response['success'] = false;
        $response['message'] = "Las contraseñas no coinciden.";
    } else {
        $hashedPassword = password_hash($newPassword, PASSWORD_BCRYPT);

        $checkDonatorEmail = "SELECT ad_email FROM altruist_donators WHERE ad_email = ?";
        $stmtCheckDonatorEmail = $dbConnection->prepareStatement($checkDonatorEmail);
        $stmtCheckDonatorEmail->bind_param("s", $userRecoveryEmail);
        $stmtCheckDonatorEmail->execute();
        $stmtCheckDonatorEmail->store_result();

        $checkCenterEmail = "SELECT sc_email FROM social_centers WHERE sc_email = ?";
        $stmtCheckCenterEmail = $dbConnection->prepareStatement($checkCenterEmail);
        $stmtCheckCenterEmail->bind_param("s", $userRecoveryEmail);
        $stmtCheckCenterEmail->execute();
        $stmtCheckCenterEmail->store_result();

        if ($stmtCheckDonatorEmail->num_rows > 0) {
            $changeUserPassword = "UPDATE altruist_donators SET ad_password = ? WHERE ad_email = ?";
            $stmtChangeUserPassword = $dbConnection->prepareStatement($changeUserPassword);
            $stmtChangeUserPassword->bind_param("ss", $hashedPassword, $userRecoveryEmail);
    
            if ($stmtChangeUserPassword->execute()){
                $response['success'] = true;
                $response['message'] = "Contraseña Cambiada Exitosamente.";
            }
        } elseif ($stmtCheckCenterEmail->num_rows > 0) {
            $changeUserPassword = "UPDATE social_centers SET sc_password = ? WHERE sc_email = ?";
            $stmtChangeUserPassword = $dbConnection->prepareStatement($changeUserPassword);
            $stmtChangeUserPassword->bind_param("ss", $hashedPassword, $userRecoveryEmail);
    
            if ($stmtChangeUserPassword->execute()){
                $response['success'] = true;
                $response['message'] = "Contraseña Cambiada Exitosamente.";
            }
        } else {
            $response['success'] = false;
            $response['message'] = "El Correo Electrónico no está registrado.";
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