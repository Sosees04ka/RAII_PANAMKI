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
           $table->dropColumn('sub_category');
            $table->dropColumn('master_category');
            $table->dropColumn('article_category');
            $table->dropColumn('usage');
            $table->dropColumn('season');
            $table->string('category');
            $table->string('size');
            $table->string('picture');
        });
    }

    /**
     * Reverse the migrations.
     */
    public function down(): void
    {
        Schema::table('clothes', function (Blueprint $table) {
            $table->boolean('gender');
            $table->string('sub_category');
            $table->string('master_category');
            $table->string('article_category');
            $table->string('season');
            $table->string('usage');
            $table->dropColumn('category');
            $table->dropColumn('size');
            $table->dropColumn('picture');
        });
    }
};
