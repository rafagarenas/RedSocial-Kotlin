<?php
require_once 'db_connection.php';

$response = array();

$dbConnection = new DBConnection();

define('MAX_FAILED_ATTEMPTS', 3);
define('LOCKOUT_TIME', 25800);

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $userEmail = $_POST['userEmail'];
    $userPassword = $_POST['userPassword'];

    if (empty($userEmail) || empty($userPassword)) {
        $response['success'] = false;
        $response['message'] = "Todos los campos son obligatorios. Por favor, complete todos los campos.";
    } else {
        $failedAttemptCount = 0;
        try {
            $checkAccountLockQuery = "SELECT last_failed_attempt_time, failed_attempt_count FROM account_locks WHERE email = ?";
            $stmtCheckAccountLock = $dbConnection->prepareStatement($checkAccountLockQuery);
            $stmtCheckAccountLock->bind_param("s", $userEmail);
            $stmtCheckAccountLock->execute();

            $resultAccountLock = $stmtCheckAccountLock->get_result();

            if ($resultAccountLock->num_rows > 0) {
                $accountLockData = $resultAccountLock->fetch_assoc();
                $lastFailedAttemptTime = strtotime($accountLockData['last_failed_attempt_time']);
                $failedAttemptCount = $accountLockData['failed_attempt_count'];

                $tiempo = time();

                if ($failedAttemptCount >= MAX_FAILED_ATTEMPTS && time() - $lastFailedAttemptTime < LOCKOUT_TIME) {
                    $diferenciaTiempo = time() - $lastFailedAttemptTime;
                    $tiempoRestante = (LOCKOUT_TIME - $diferenciaTiempo);

                    $minutos = floor($tiempoRestante / 60);
                    $segundos = $tiempoRestante % 60;

                    $tiempoRestanteFormateado = sprintf('%dm %ds', $minutos, $segundos);

                    $response = array("success" => false, "message" => "Cuenta bloqueada por seguridad. Vuelve a intentarlo en: $tiempoRestanteFormateado");

                    $dbConnection->closeConnection();
                    header('Content-Type: application/json');
                    echo json_encode($response);
                    exit();
                }
            }

            $getStoredPasswordQuery = "SELECT ad_password FROM altruist_donators WHERE ad_email = ? UNION SELECT sc_password FROM social_centers WHERE sc_email = ?";
            $stmtGetStoredPassword = $dbConnection->prepareStatement($getStoredPasswordQuery);
            $stmtGetStoredPassword->bind_param("ss", $userEmail, $userEmail);
            $stmtGetStoredPassword->execute();

            $resultStoredPassword = $stmtGetStoredPassword->get_result();

            if ($resultStoredPassword->num_rows > 0) {

                $storedPasswordRow = $resultStoredPassword->fetch_assoc();
                $storedPasswordHash = $storedPasswordRow['ad_password'];

                if (password_verify($userPassword, $storedPasswordHash)) {
                    $failedAttemptCount = 0;
                    $resetFailedAttemptsQuery = "UPDATE account_locks SET failed_attempt_count = ?, last_failed_attempt_time = NULL WHERE email = ?";
                    $stmtResetFailedAttempts = $dbConnection->prepareStatement($resetFailedAttemptsQuery);
                    $stmtResetFailedAttempts->bind_param("is", $failedAttemptCount, $userEmail);
                    $stmtResetFailedAttempts->execute();

                    $response = array("success" => true, "message" => "Inicio de Sesión Exitoso");
                } else {
                    $failedAttemptCount++;
                    $updateFailedAttemptsQuery = "INSERT INTO account_locks (email, failed_attempt_count, last_failed_attempt_time) VALUES (?, ?, NOW()) ON DUPLICATE KEY UPDATE failed_attempt_count = ?, last_failed_attempt_time = NOW()";
                    $stmtUpdateFailedAttempts = $dbConnection->prepareStatement($updateFailedAttemptsQuery);
                    $stmtUpdateFailedAttempts->bind_param("sii", $userEmail, $failedAttemptCount, $failedAttemptCount);
                    $stmtUpdateFailedAttempts->execute();

                    $response = array("success" => false, "message" => "Credenciales Incorrectas");
                }
            } else {
                $failedAttemptCount++;
                $updateFailedAttemptsQuery = "INSERT INTO account_locks (email, failed_attempt_count, last_failed_attempt_time) VALUES (?, ?, NOW()) ON DUPLICATE KEY UPDATE failed_attempt_count = ?, last_failed_attempt_time = NOW()";
                $stmtUpdateFailedAttempts = $dbConnection->prepareStatement($updateFailedAttemptsQuery);
                $stmtUpdateFailedAttempts->bind_param("sii", $userEmail, $failedAttemptCount, $failedAttemptCount);
                $stmtUpdateFailedAttempts->execute();

                $response = array("success" => false, "message" => "Credenciales Incorrectas");
            }
        } catch (Exception $e) {
            $response['success'] = false;
            $response['message'] = "$e->getMessage()";
        } finally {
            $dbConnection->closeConnection();
        }
    }
} else {
    http_response_code(405);
    $response['success'] = false;
    $response['message'] = "Error: Método no permitido.";
}

header('Content-Type: application/json');
echo json_encode($response);
?>