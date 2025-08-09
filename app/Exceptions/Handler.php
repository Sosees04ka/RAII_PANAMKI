<?php
namespace App\Exceptions;

use Illuminate\Foundation\Exceptions\Handler as ExceptionHandler;
use Throwable;

class Handler extends ExceptionHandler
{
    protected $dontReport = [
        //
    ];

    protected $dontFlash = [
        'current_password',
        'password',
        'password_confirmation',
    ];

    public function render($request, Throwable $e)
    {
        if ($request->expectsJson()) {
            $status = method_exists($e, 'getStatusCode') ? $e->getStatusCode() : 500;

            return response()->json([
                'message' => $e->getMessage(),
                'errors' => method_exists($e, 'errors') ? $e->errors() : null,
            ], $status);
        }

        return parent::render($request, $e);
    }
}
