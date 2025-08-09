<?php

use App\Http\Controllers\AuthController;
use App\Http\Controllers\ClothesController;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Route;

Route::middleware('auth:sanctum')->group(function () {
    Route::get('/user', [AuthController::class, 'me']);
    Route::post('/logout', [AuthController::class, 'logout']);
});

Route::group(['prefix' => 'auth'], function () {
    Route::post('register', [AuthController::class, 'register']);
    Route::post('login', [AuthController::class, 'login']);
});


Route::group(['prefix' => 'cloth'], function () {
    Route::post('baseAdd', [ClothesController::class, 'baseAdd']);
    Route::post('addAfterComp', [ClothesController::class, 'addAfterComp']);
    Route::get('{id}', [ClothesController::class, 'get']);
    Route::get('', [ClothesController::class, 'get']);
    Route::patch('edit/{id}', [ClothesController::class, 'update']);
    Route::delete('delete/{id}', [ClothesController::class, 'delete']);
})->middleware('auth:sanctum');
