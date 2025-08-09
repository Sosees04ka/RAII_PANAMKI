<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class Cloth extends Model
{
    public $timestamps = false;
    protected $fillable = [
        'user_id',
        'master_category',
        'sub_category',
        'base_color',
        'usage',
        'product_display_name',
        'picture'
    ];

    protected $casts = [
        'base_color' => 'array',
    ];

    public function user()
    {
        return $this->belongsTo('App\User', 'user_id');
    }
}
