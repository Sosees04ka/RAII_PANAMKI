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
        Schema::table('cloths', function (Blueprint $table) {
           $table->dropColumn('gender');
            $table->dropColumn('article_category');
            $table->dropColumn('season');
            $table->string('size');
            $table->string('picture');
        });
    }

    /**
     * Reverse the migrations.
     */
    public function down(): void
    {
        Schema::table('cloths', function (Blueprint $table) {
            $table->boolean('gender');
            $table->string('article_category');
            $table->string('season');
            $table->dropColumn('size');
            $table->dropColumn('picture');
        });
    }
};
