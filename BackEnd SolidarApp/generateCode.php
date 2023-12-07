<?php
class CodigoAleatorio {
    public static function generarCodigo() {
        $caracteresPermitidos = '0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz!@#$%&*()=?';
        $longitudCodigo = 8;

        $codigo = '';
        for ($i = 0; $i < $longitudCodigo; $i++) {
            $codigo .= $caracteresPermitidos[rand(0, strlen($caracteresPermitidos) - 1)];
        }

        return $codigo;
    }
}
?>
