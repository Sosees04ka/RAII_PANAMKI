<?php

use App\Http\Controllers\AuthController;
use App\Http\Controllers\ClothesController;
use App\Http\Controllers\LookController;
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

Route::middleware('auth:sanctum')->group(function () {
    Route::post('cloth/add', [ClothesController::class, 'add']);
    Route::get('cloth/preview', [ClothesController::class, 'getPreview']);
    Route::get('cloth/{id}', [ClothesController::class, 'get']);
    Route::get('cloth', [ClothesController::class, 'get']);
    Route::post('cloth/edit/{id}', [ClothesController::class, 'update']);
    Route::delete('cloth/delete/{id}', [ClothesController::class, 'delete']);
});

Route::middleware('auth:sanctum')->group(function () {
    Route::get('/look/weather', [LookController::class, 'createLooksOnWeather']);
    Route::post('/look/picture', [LookController::class, 'createLookOnPicture']);
    Route::get('/look/cloth/{id}', [LookController::class, 'createLooksOnCloth']);
    Route::get('/look/{id}', [LookController::class, 'show']);
    Route::get('/look', [LookController::class, 'show']);
    Route::post('/look/like/{id}', [LookController::class, 'store']);
    Route::post('/look/dislike/{id}', [LookController::class, 'destroy']);
});
