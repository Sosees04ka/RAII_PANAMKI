<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    /**
     * Run the migrations.
     */
    public function up(): void
    {
        Schema::create('cloths_look', function (Blueprint $table) {
            $table->unsignedBigInteger('cloth_id');
            $table->unsignedBigInteger('look_id');

            $table->foreign('cloth_id')->references('id')->on('cloths')->onDelete('cascade');
            $table->foreign('look_id')->references('id')->on('looks')->onDelete('cascade');
        });
    }

    /**
     * Reverse the migrations.
     */
    public function down(): void
    {
        Schema::dropIfExists('cloths_look');
    }
};
