<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    public function up(): void
    {
        Schema::table('cloths', function (Blueprint $table) {
            $table->dropColumn('base_color');
            $table->dropColumn('size');
            $table->json('base_color');
        });
    }

    public function down(): void
    {
        Schema::table('cloths', function (Blueprint $table) {
            $table->dropColumn('base_color');
            $table->string('size');
            $table->string('base_color');
        });
    }
};
