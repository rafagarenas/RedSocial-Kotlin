<?php
require_once 'db_connection.php';

$response = array();

$dbConnection = new DBConnection();

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $userRecoveryEmail = $_POST['userRecoveryEmail'];
    $recoveryKey = $_POST['recoveryKey'];

    if (empty($userRecoveryEmail) || empty($recoveryKey)) {
        $response['success'] = false;
        $response['message'] = "Escribe el Código de Recuperación enviado a tu Correo Electrónico.";
    } else {
        $checkIfCodeIsCorrect = "SELECT expiration_time, is_used FROM account_tokens WHERE token = ? AND email = ?";
        $stmtCheckIfCodeIsCorrect = $dbConnection->prepareStatement($checkIfCodeIsCorrect);
        $stmtCheckIfCodeIsCorrect->bind_param("ss", $recoveryKey, $userRecoveryEmail);
        $stmtCheckIfCodeIsCorrect->execute();

        $resultCheckIfCodeIsCorrect = $stmtCheckIfCodeIsCorrect->get_result();

        if ($resultCheckIfCodeIsCorrect->num_rows > 0) {

            $accountTokenData = $resultCheckIfCodeIsCorrect->fetch_assoc();
            $tokenExpirationTime = strtotime($accountTokenData['expiration_time']);
            $tokenAvailability = $accountTokenData['is_used'];

            if ($tokenAvailability == 1){
                $response['success'] = false;
                $response['message'] = "El Código de Recuperación ya fue utilizado. Genere otro si es necesario.";
            } else{

                $currentTime = time();

                if ($currentTime > $tokenExpirationTime) {
                    $response['success'] = false;
                    $response['message'] = "El Código de Recuperación ha expirado, repita el proceso nuevamente.";
                } else {
                    $newIsUsedValue = 1;
    
                    $setTokenAsUsed = "UPDATE account_tokens SET is_used = ? WHERE token = ? AND email = ?";
                    $stmtSetTokenAsUsed = $dbConnection->prepareStatement($setTokenAsUsed);
                    $stmtSetTokenAsUsed->bind_param("sss", $newIsUsedValue, $recoveryKey, $userRecoveryEmail);
                    
                    if ($stmtSetTokenAsUsed->execute()){
                        $response['success'] = true;
                        $response['message'] = "Código de Recuperación Validado. Cambie su Contraseña.";
                    }
                }
            }

        } else{
            $response['success'] = false;
            $response['message'] = "El Código de Recuperación no es válido, porfavor revise nuevamente.";
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