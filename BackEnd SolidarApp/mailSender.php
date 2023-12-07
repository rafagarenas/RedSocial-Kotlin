<?php

use PHPMailer\PHPMailer\PHPMailer;
use PHPMailer\PHPMailer\Exception;

require 'vendor/phpmailer/phpmailer/src/Exception.php';
require 'vendor/phpmailer/phpmailer/src/PHPMailer.php';
require 'vendor/phpmailer/phpmailer/src/SMTP.php';

class CorreoElectronico
{
    private $remitente = 'solidarapp@hotmail.com';
    private $asunto = 'Asunto del Correo';

    public function enviarCorreo($destinatario, $asunto, $mensaje){
        $mail = new PHPMailer(true);

        try {

            $mail->isSMTP();

            $mail -> CharSet = "UTF-8"; 

            $mail -> Host = 'smtp-mail.outlook.com';
            $mail -> Port = 587;
            $mail -> SMTPSecure = 'tls';

            $mail -> SMTPAuth = true;

            // Credenciales de la Cuenta Gmail de SolidarApp.
            $mail -> Username = 'solidarapp@hotmail.com';
            $mail -> Password = 'Proyecto2023';

            $mail -> setFrom($this->remitente, 'SolidarApp Servicios');
            $mail -> addAddress($destinatario);
            $mail -> Subject = $asunto;
            $mail -> Body = $mensaje;

            $mail->IsHTML(true); 

            $mail->send();

            return "Correo Electrónico Enviado con Éxito.";

        } catch (Exception $e) {
            return "Error al Enviar el Correo Electrónico. Detalles: " . $mail->ErrorInfo;
        }
    }
}

?>
