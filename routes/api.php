<?php

use App\Http\Controllers\AuthController;
use App\Http\Controllers\ClothesController;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Route;

Route::get('/user', function (Request $request) {
    return $request->user();
})->middleware('auth:sanctum');

Route::group(['prefix' => 'auth'], function () {
    Route::post('register', [AuthController::class, 'register']);
    Route::post('login', [AuthController::class, 'login']);
});


Route::middleware(['auth:sanctum'])->group(function () {
    Route::post('cloth/add', [ClothesController::class, 'add']);
    Route::get('cloth/{id}', [ClothesController::class, 'get']);
    Route::get('cloth', [ClothesController::class, 'get']);
    Route::patch('cloth/edit/{id}', [ClothesController::class, 'update']);
    Route::delete('cloth/delete/{id}', [ClothesController::class, 'delete']);
});